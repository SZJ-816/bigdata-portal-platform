package com.bigdata.portal.framework.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 * 提供Token的生成、验证、刷新等功能
 */
@Component
public class JwtTokenUtil {

    /** JWT密钥，从配置文件读取 */
    @Value("${jwt.secret:zhixun-bigdata-portal-secret-key-2024}")
    private String secret;

    /** Token过期时间（毫秒），默认24小时 */
    @Value("${jwt.expiration:86400000}")
    private long expiration;

    /** Token前缀 */
    private static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 生成JWT Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param claims   自定义声明
     * @return JWT Token字符串
     */
    public String generateToken(Long userId, String username, Map<String, Object> claims) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setId(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 生成JWT Token（无自定义声明）
     */
    public String generateToken(Long userId, String username) {
        return generateToken(userId, username, null);
    }

    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.parseLong(claims.getId());
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断Token是否可以刷新
     */
    public boolean canRefresh(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 刷新Token
     */
    public String refreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        claims.put("iat", new Date());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 从请求头中提取Token（去除Bearer前缀）
     */
    public String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 从Token中解析Claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 判断Token是否过期
     */
    private boolean isTokenExpired(String token) {
        Date expiration = getClaimsFromToken(token).getExpiration();
        return expiration.before(new Date());
    }
}
