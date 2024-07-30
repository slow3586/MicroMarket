package com.slow3586.micromarket.api;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

@Component
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class DefaultSecurityWebFilter extends OncePerRequestFilter {
    @NonFinal
    @Value("${API_KEY}")
    String apiKey;
    SecretKey secretKey;

    @Override
    protected void doFilterInternal(
        @NonNull final HttpServletRequest request,
        @NonNull final HttpServletResponse response,
        @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(null, null);

        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String bearerToken = authorizationHeader.substring("Bearer ".length());
                if (Objects.equals(apiKey, bearerToken)) {
                    authenticationToken = new UsernamePasswordAuthenticationToken(
                        "SYSTEM",
                        null,
                        AuthorityUtils.createAuthorityList("SYSTEM"));
                } else {
                    final Claims claims = Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(bearerToken)
                        .getPayload();

                    if (Instant.now().isAfter(claims.getExpiration().toInstant())) {
                        throw new IllegalArgumentException("Token expired!");
                    }

                    authenticationToken = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(),
                        null,
                        AuthorityUtils.createAuthorityList("USER"));
                }
            }
        } catch (Exception e) {
            log.error("error", e);
        }

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
