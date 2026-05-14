package com.bigdata.portal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final int CODE_LENGTH = 6;
    private static final long CODE_EXPIRE_MS = 5 * 60 * 1000;
    private static final long CODE_RESEND_INTERVAL_MS = 60 * 1000;

    private final Map<String, CodeEntry> codeStore = new ConcurrentHashMap<>();

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public boolean sendVerificationCode(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        CodeEntry existing = codeStore.get(email);
        if (existing != null && System.currentTimeMillis() - existing.createdAt < CODE_RESEND_INTERVAL_MS) {
            return false;
        }

        String code = generateCode();
        codeStore.put(email, new CodeEntry(code, System.currentTimeMillis()));

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("大数据资讯平台 - 邮箱验证码");
            message.setText("您的验证码为：" + code + "\n\n验证码5分钟内有效，请勿泄露给他人。\n\n如非本人操作，请忽略此邮件。");
            mailSender.send(message);
            log.info("Verification code sent to {}", email);
            return true;
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", email, e.getMessage());
            codeStore.remove(email);
            return false;
        }
    }

    public boolean verifyCode(String email, String code) {
        if (email == null || code == null) {
            return false;
        }
        CodeEntry entry = codeStore.get(email);
        if (entry == null) {
            return false;
        }
        if (System.currentTimeMillis() - entry.createdAt > CODE_EXPIRE_MS) {
            codeStore.remove(email);
            return false;
        }
        boolean valid = entry.code.equals(code);
        if (valid) {
            codeStore.remove(email);
        }
        return valid;
    }

    private String generateCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void cleanExpiredCodes() {
        long now = System.currentTimeMillis();
        codeStore.entrySet().removeIf(entry -> now - entry.getValue().createdAt > CODE_EXPIRE_MS);
    }

    private static class CodeEntry {
        final String code;
        final long createdAt;

        CodeEntry(String code, long createdAt) {
            this.code = code;
            this.createdAt = createdAt;
        }
    }
}
