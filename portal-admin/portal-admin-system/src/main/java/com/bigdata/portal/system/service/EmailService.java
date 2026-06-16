package com.bigdata.portal.system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 邮件验证码Service
 * 负责生成、发送和校验邮箱验证码
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    /** 验证码有效期（毫秒）：5分钟 */
    private static final long CODE_EXPIRE_MS = 5 * 60 * 1000;

    /** 发送间隔限制（毫秒）：60秒 */
    private static final long SEND_INTERVAL_MS = 60 * 1000;

    private final JavaMailSender mailSender;
    private final Random random = new Random();

    /** 验证码缓存：email -> { code, timestamp } */
    private final Map<String, CodeEntry> codeCache = new ConcurrentHashMap<>();

    /** 发送频率限制：email -> lastSendTimestamp */
    private final Map<String, Long> sendLimitCache = new ConcurrentHashMap<>();

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 发送验证码到指定邮箱
     * @return 错误信息，null表示成功
     */
    public String sendVerificationCode(String email) {
        // 检查发送频率
        Long lastSend = sendLimitCache.get(email);
        if (lastSend != null && System.currentTimeMillis() - lastSend < SEND_INTERVAL_MS) {
            long remainSec = (SEND_INTERVAL_MS - (System.currentTimeMillis() - lastSend)) / 1000;
            return "发送过于频繁，请" + remainSec + "秒后重试";
        }

        // 生成6位数字验证码
        String code = String.format("%06d", random.nextInt(1000000));

        // 发送邮件
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("智讯科技 <" + getFromAddress() + ">");
            message.setTo(email);
            message.setSubject("【智讯科技】邮箱验证码");
            message.setText("您的注册验证码为：" + code + "\n\n验证码5分钟内有效，请勿泄露给他人。");
            mailSender.send(message);
            log.info("验证码已发送至邮箱: {}", email);
        } catch (Exception e) {
            log.error("发送邮件失败: {}", e.getMessage(), e);
            return "邮件发送失败，请检查邮箱地址或稍后重试";
        }

        // 缓存验证码
        codeCache.put(email, new CodeEntry(code, System.currentTimeMillis()));
        sendLimitCache.put(email, System.currentTimeMillis());

        return null; // 成功
    }

    /**
     * 校验验证码
     * @return true=验证码正确
     */
    public boolean verifyCode(String email, String code) {
        if (email == null || code == null) return false;

        CodeEntry entry = codeCache.get(email);
        if (entry == null) return false;

        // 检查是否过期
        if (System.currentTimeMillis() - entry.timestamp > CODE_EXPIRE_MS) {
            codeCache.remove(email);
            return false;
        }

        // 校验验证码
        if (code.equals(entry.code)) {
            codeCache.remove(email); // 使用后立即删除
            return true;
        }
        return false;
    }

    private String getFromAddress() {
        try {
            // 从mailSender中获取发件人地址
            return ((org.springframework.mail.javamail.JavaMailSenderImpl) mailSender).getUsername();
        } catch (Exception e) {
            return "noreply@zhixun.com";
        }
    }

    /**
     * 验证码缓存条目
     */
    private static class CodeEntry {
        final String code;
        final long timestamp;

        CodeEntry(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
    }
}
