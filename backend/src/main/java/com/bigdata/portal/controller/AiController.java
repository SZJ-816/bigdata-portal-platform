package com.bigdata.portal.controller;

import com.bigdata.portal.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();
        try {
            String answer = aiService.searchNews(keyword);
            result.put("data", answer);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping(value = "/search/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter searchStream(@RequestParam String keyword, HttpServletResponse response) {
        response.setHeader("X-Accel-Buffering", "no");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        SseEmitter emitter = new SseEmitter(90000L);
        executor.execute(() -> {
            try {
                aiService.searchNewsStream(keyword, new AiService.StreamCallback() {
                    @Override
                    public void onToken(String token) {
                        try {
                            emitter.send(SseEmitter.event().data(token));
                        } catch (Exception e) {
                            emitter.completeWithError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        emitter.complete();
                    }

                    @Override
                    public void onError(Exception e) {
                        emitter.completeWithError(e);
                    }
                });
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().data("[ERROR] " + e.getMessage()));
                } catch (Exception ignored) {}
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    @GetMapping("/hot-summary")
    public Map<String, Object> hotSummary(@RequestParam(required = false) String instruction) {
        Map<String, Object> result = new HashMap<>();
        try {
            String summary = aiService.hotSummary(instruction);
            result.put("data", summary);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping(value = "/hot-summary/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter hotSummaryStream(@RequestParam(required = false) String instruction, HttpServletResponse response) {
        response.setHeader("X-Accel-Buffering", "no");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        SseEmitter emitter = new SseEmitter(120000L);
        executor.execute(() -> {
            try {
                aiService.hotSummaryStream(instruction, new AiService.StreamCallback() {
                    @Override
                    public void onToken(String token) {
                        try {
                            emitter.send(SseEmitter.event().data(token));
                        } catch (Exception e) {
                            emitter.completeWithError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        emitter.complete();
                    }

                    @Override
                    public void onError(Exception e) {
                        emitter.completeWithError(e);
                    }
                });
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().data("[ERROR] " + e.getMessage()));
                } catch (Exception ignored) {}
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    @GetMapping("/translate")
    public Map<String, Object> translate(@RequestParam String text) {
        Map<String, Object> result = new HashMap<>();
        try {
            String translated = aiService.translateText(text);
            result.put("data", translated);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
