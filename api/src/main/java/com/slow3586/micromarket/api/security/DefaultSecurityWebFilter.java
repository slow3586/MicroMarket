package com.slow3586.micromarket.api.security;

import com.slow3586.micromarket.api.utils.SecurityUtils;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Component
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
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
            final String authorizationHeader = request.getHeader(SecurityUtils.AUTH_HEADER_NAME);
            if (authorizationHeader != null && authorizationHeader.startsWith(SecurityUtils.BEARER_PREFIX)) {
                final String bearerToken = authorizationHeader.substring(SecurityUtils.BEARER_PREFIX.length());
                if (Objects.equals(apiKey, bearerToken)) {
                    authenticationToken = new UsernamePasswordAuthenticationToken(
                        "SYSTEM",
                        null,
                        AuthorityUtils.createAuthorityList("API"));
                } else {
                    final Claims claims = Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(bearerToken)
                        .getPayload();

                    if (Instant.now().isAfter(claims.getExpiration().toInstant())) {
                        throw new IllegalArgumentException("Token expired");
                    }

                    authenticationToken = new UsernamePasswordAuthenticationToken(
                        UUID.fromString(claims.getSubject()),
                        null,
                        AuthorityUtils.createAuthorityList("USER"));
                }
            }
        } catch (Exception e) {
            throw new AccessDeniedException("Access denied", e);
        }

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
