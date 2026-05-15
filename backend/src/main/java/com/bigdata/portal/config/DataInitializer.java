package com.bigdata.portal.config;

import com.bigdata.portal.entity.User;
import com.bigdata.portal.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserMapper userMapper;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        if (userMapper.findByUsername("admin") == null) {
            String adminPwd = "Admin@2024!";
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode(adminPwd));
            admin.setEmail("admin@bigdata-portal.local");
            userMapper.insert(admin);
            logger.warn("Default admin account created - username: admin, password: {} - CHANGE IMMEDIATELY!", adminPwd);
        }
        if (userMapper.findByUsername("test") == null) {
            String testPwd = "Test@2024!";
            User test = new User();
            test.setUsername("test");
            test.setPassword(passwordEncoder.encode(testPwd));
            test.setEmail("test@bigdata-portal.local");
            userMapper.insert(test);
            logger.warn("Default test account created - username: test, password: {} - CHANGE IMMEDIATELY!", testPwd);
        }
    }
}
