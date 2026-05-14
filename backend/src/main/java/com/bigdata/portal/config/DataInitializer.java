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
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@bigdata.com");
            userMapper.insert(admin);
            logger.info("Created default admin user");
        }
        if (userMapper.findByUsername("test") == null) {
            User test = new User();
            test.setUsername("test");
            test.setPassword(passwordEncoder.encode("test123"));
            test.setEmail("test@bigdata.com");
            userMapper.insert(test);
            logger.info("Created default test user");
        }
    }
}
