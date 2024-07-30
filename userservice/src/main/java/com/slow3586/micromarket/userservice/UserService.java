package com.slow3586.micromarket.userservice;


import com.slow3586.micromarket.api.order.OrderTransaction;
import com.slow3586.micromarket.api.user.LoginRequest;
import com.slow3586.micromarket.api.user.RegisterUserRequest;
import com.slow3586.micromarket.api.user.UserDto;
import com.slow3586.micromarket.userservice.entity.User;
import com.slow3586.micromarket.userservice.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    public OrderTransaction processNewOrder(OrderTransaction order) {
        return order.setBuyer(userRepository.findById(order.getBuyer().getId())
                .map(userMapper::toDto)
                .orElseThrow())
            .setOrderItemList(order.getOrderItemList().stream()
                .map(item ->
                    item.setSeller(userRepository.findById(item.getSeller().getId())
                        .map(userMapper::toDto)
                        .orElseThrow()))
                .toList());
    }

    public UserDto registerUser(RegisterUserRequest request) {
        User user = userRepository.save(new User()
            .setName(request.getLogin())
            .setLogin(request.getLogin())
            .setPassword(passwordEncoder.encode(request.getPassword())));

        return userMapper.toDto(user);
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
            .subject(user.getLogin())
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

        final User user = userRepository.findByLogin("login").orElseThrow();

        if (Instant.now().isAfter(claims.getExpiration().toInstant())) {
            throw new IllegalArgumentException("Token expired!");
        }

        return userMapper.toDto(user);
    }

    public UserDto findUserById(UUID uuid) {
        return userRepository.findById(uuid)
            .map(userMapper::toDto)
            .orElseThrow();
    }
}
