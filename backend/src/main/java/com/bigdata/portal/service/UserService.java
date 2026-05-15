package com.bigdata.portal.service;

import com.bigdata.portal.config.JwtConfig;
import com.bigdata.portal.entity.User;
import com.bigdata.portal.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtConfig jwtConfig;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Map<String, Object> login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        
        boolean passwordValid = passwordEncoder.matches(password, user.getPassword());

        if (!passwordValid) {
            return null;
        }
        String token = jwtConfig.generateToken(user.getId(), user.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("username", user.getUsername());
        result.put("userId", user.getId());
        result.put("email", user.getEmail());
        return result;
    }

    public Map<String, Object> register(String username, String password, String email) {
        User existing = userMapper.findByUsername(username);
        if (existing != null) {
            return null;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        userMapper.insert(user);

        String token = jwtConfig.generateToken(user.getId(), user.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("username", user.getUsername());
        result.put("userId", user.getId());
        result.put("email", user.getEmail());
        return result;
    }

    public User getProfile(Long userId) {
        return userMapper.findById(userId);
    }

    public User getProfileByUsername(String username) {
        return userMapper.findByUsername(username);
    }
}
