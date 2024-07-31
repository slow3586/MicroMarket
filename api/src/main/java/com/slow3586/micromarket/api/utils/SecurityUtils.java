package com.slow3586.micromarket.api.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

@Slf4j
public class SecurityUtils {
    public static String BEARER_PREFIX = "Bearer ";
    public static String AUTH_HEADER_NAME = "Authorization";

    public static boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(role);
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static UUID getPrincipalId() {
        try {
            return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(p -> p instanceof UUID)
                .map(p -> ((UUID) p))
                .orElseThrow(() ->
                    new IllegalStateException("Not logged in"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException("Could not get current user ID: " + e.getMessage(), e);
        }
    }
}
