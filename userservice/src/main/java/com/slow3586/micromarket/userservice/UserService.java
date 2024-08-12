package com.slow3586.micromarket.userservice;


import com.slow3586.micromarket.api.user.LoginRequest;
import com.slow3586.micromarket.api.user.RegisterUserRequest;
import com.slow3586.micromarket.api.user.UserConfig;
import com.slow3586.micromarket.api.user.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    @NonFinal
    @Value("${auth.token.minutes:600}")
    int tokenMinutes;
    PasswordEncoder passwordEncoder;
    SecretKey secretKey;

    public UserDto registerUser(RegisterUserRequest request) {
        return userMapper.toDto(
            userRepository.save(
                new User()
                    .setName(request.getLogin())
                    .setLogin(request.getLogin())
                    .setPassword(passwordEncoder.encode(
                        request.getPassword()))));
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByLogin(request.getLogin()).orElse(null);
        if (user == null
            || !passwordEncoder.matches(
            request.getPassword(),
            user.getPassword())
        ) {
            throw new IllegalArgumentException("Incorrect login or password!");
        }

        return Jwts.builder()
            .subject(user.getId().toString())
            .expiration(Date.from(
                Instant.now().plus(
                    Duration.ofMinutes(tokenMinutes))))
            .signWith(secretKey)
            .compact();
    }

    public UserDto token(String token) {
        final Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        if (Instant.now().isAfter(claims.getExpiration().toInstant())) {
            throw new IllegalArgumentException("Token expired!");
        }

        return userMapper.toDto(
            userRepository
                .findByLogin("login")
                .orElseThrow());
    }

    @Cacheable(value = UserConfig.TOPIC, key = "#userId", cacheManager = "userCacheManager")
    public UserDto getUserById(UUID userId) {
        return userRepository.findById(userId)
            .map(userMapper::toDto)
            .orElseThrow();
    }
}
