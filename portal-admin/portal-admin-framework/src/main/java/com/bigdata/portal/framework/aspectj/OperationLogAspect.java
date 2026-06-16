package com.bigdata.portal.framework.aspectj;

import com.alibaba.fastjson2.JSON;
import com.bigdata.portal.common.annotation.OperationLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * AOP操作日志切面
 * 拦截带有@OperationLog注解的方法，记录操作日志
 */
@Aspect
@Component
public class OperationLogAspect {

    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

    /**
     * 环绕通知，记录操作日志
     */
    @Around("@annotation(com.bigdata.portal.common.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 执行目标方法
        Object result = point.proceed();

        long elapsed = System.currentTimeMillis() - startTime;

        // 记录日志
        saveLog(point, elapsed, result);

        return result;
    }

    /**
     * 保存操作日志
     */
    private void saveLog(ProceedingJoinPoint point, long elapsed, Object result) {
        try {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            OperationLog operationLog = method.getAnnotation(OperationLog.class);

            // 获取请求信息
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

            // 构建日志信息
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append("\n========== 操作日志 ==========\n");
            logBuilder.append("模块：").append(operationLog.module()).append("\n");
            logBuilder.append("类型：").append(operationLog.type()).append("\n");
            logBuilder.append("描述：").append(operationLog.description()).append("\n");
            logBuilder.append("方法：").append(method.getDeclaringClass().getName())
                    .append(".").append(method.getName()).append("\n");

            if (request != null) {
                logBuilder.append("请求URL：").append(request.getRequestURL()).append("\n");
                logBuilder.append("请求方式：").append(request.getMethod()).append("\n");
                logBuilder.append("IP地址：").append(getIpAddress(request)).append("\n");
            }

            logBuilder.append("请求参数：").append(JSON.toJSONString(point.getArgs())).append("\n");
            logBuilder.append("返回结果：").append(JSON.toJSONString(result)).append("\n");
            logBuilder.append("耗时：").append(elapsed).append("ms\n");
            logBuilder.append("==============================");

            log.info(logBuilder.toString());
        } catch (Exception e) {
            log.warn("记录操作日志异常：{}", e.getMessage());
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
