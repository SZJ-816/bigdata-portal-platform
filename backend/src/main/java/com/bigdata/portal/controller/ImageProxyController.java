package com.bigdata.portal.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/image")
public class ImageProxyController {

    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(
            Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml", "image/bmp", "image/ico", "image/jpg")
    );

    private static final int MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 10000;
    private static final int MAX_CACHE_SIZE = 200;
    private static final long CACHE_TTL_MS = 3600_000L;

    private static final ConcurrentHashMap<String, CacheEntry> imageCache = new ConcurrentHashMap<>();

    private static class CacheEntry {
        final byte[] data;
        final MediaType contentType;
        final long timestamp;

        CacheEntry(byte[] data, MediaType contentType) {
            this.data = data;
            this.contentType = contentType;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL_MS;
        }
    }

    @GetMapping("/proxy")
    public ResponseEntity<byte[]> proxyImage(@RequestParam String url) {
        if (url == null || url.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return ResponseEntity.badRequest().build();
        }

        CacheEntry cached = imageCache.get(url);
        if (cached != null && !cached.isExpired()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(cached.contentType);
            headers.setContentLength(cached.data.length);
            headers.setCacheControl("public, max-age=86400");
            headers.set("X-Cache", "HIT");
            return new ResponseEntity<>(cached.data, headers, HttpStatus.OK);
        }
        if (cached != null && cached.isExpired()) {
            imageCache.remove(url, cached);
        }

        if (imageCache.size() > MAX_CACHE_SIZE) {
            evictCache();
        }

        try {
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; ImageProxy/1.0)");
            conn.setRequestProperty("Referer", "");
            conn.setRequestProperty("Cookie", "");

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                conn.disconnect();
                return ResponseEntity.status(responseCode).build();
            }

            String contentType = conn.getContentType();
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            if (contentType != null) {
                String ct = contentType.split(";")[0].trim().toLowerCase();
                if (!ALLOWED_CONTENT_TYPES.contains(ct)) {
                    conn.disconnect();
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
                }
                mediaType = MediaType.parseMediaType(ct);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream(32768);
            try (InputStream is = conn.getInputStream()) {
                byte[] buffer = new byte[16384];
                int bytesRead;
                int totalRead = 0;
                while ((bytesRead = is.read(buffer)) != -1) {
                    totalRead += bytesRead;
                    if (totalRead > MAX_IMAGE_SIZE) {
                        conn.disconnect();
                        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
                    }
                    baos.write(buffer, 0, bytesRead);
                }
            }
            conn.disconnect();

            byte[] imageData = baos.toByteArray();

            if (imageData.length > 0 && imageData.length <= MAX_IMAGE_SIZE) {
                imageCache.put(url, new CacheEntry(imageData, mediaType));
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentLength(imageData.length);
            headers.setCacheControl("public, max-age=86400");
            headers.set("X-Cache", "MISS");

            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void evictCache() {
        long now = System.currentTimeMillis();
        imageCache.entrySet().removeIf(e -> e.getValue().isExpired());
        if (imageCache.size() > MAX_CACHE_SIZE * 3 / 4) {
            imageCache.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue((a, b) -> Long.compare(a.timestamp, b.timestamp)))
                    .limit(imageCache.size() - MAX_CACHE_SIZE / 2)
                    .forEach(e -> imageCache.remove(e.getKey(), e.getValue()));
        }
    }
}
