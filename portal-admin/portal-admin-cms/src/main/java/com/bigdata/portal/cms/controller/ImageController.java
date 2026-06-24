package com.bigdata.portal.cms.controller;

import com.bigdata.portal.common.result.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 图片上传Controller
 */
@RestController
@RequestMapping("/image")
public class ImageController {

    @Value("${upload.path:/app/news-images}")
    private String uploadPath;

    @Value("${upload.url-prefix:/image/}")
    private String urlPrefix;

    private final org.springframework.web.client.RestTemplate restTemplate;

    public ImageController() {
        org.springframework.http.client.SimpleClientHttpRequestFactory factory =
                new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        this.restTemplate = new org.springframework.web.client.RestTemplate(factory);
    }

    /**
     * 上传图片
     */
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }
        try {
            // 生成文件名
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

            // 确保目录存在
            Path dir = Paths.get(uploadPath);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            // 保存文件
            Path filePath = dir.resolve(fileName);
            file.transferTo(filePath.toFile());

            // 返回访问URL
            String url = urlPrefix + fileName;
            return R.ok(url);
        } catch (IOException e) {
            return R.fail("上传失败: " + e.getMessage());
        }
    }

    /**
     * 图片代理 - 转发外部图片URL，解决跨域和网络访问问题
     */
    @GetMapping("/proxy")
    public ResponseEntity<byte[]> proxy(@RequestParam("url") String imageUrl) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    imageUrl, HttpMethod.GET, requestEntity, byte[].class);

            HttpHeaders responseHeaders = new HttpHeaders();
            MediaType contentType = response.getHeaders().getContentType();
            if (contentType != null) {
                responseHeaders.setContentType(contentType);
            } else {
                responseHeaders.setContentType(MediaType.IMAGE_JPEG);
            }
            responseHeaders.setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic());

            return new ResponseEntity<>(response.getBody(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }
}
