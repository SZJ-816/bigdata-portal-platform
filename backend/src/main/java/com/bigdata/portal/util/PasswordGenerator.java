package com.bigdata.portal.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123456";
        String hashed = encoder.encode(password);
        System.out.println("BCrypt hash for '" + password + "':");
        System.out.println(hashed);
    }
}