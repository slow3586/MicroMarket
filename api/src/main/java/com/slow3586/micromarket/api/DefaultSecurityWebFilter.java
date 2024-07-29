package com.slow3586.micromarket.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class DefaultSecurityWebFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
        @NonNull final HttpServletRequest request,
        @NonNull final HttpServletResponse response,
        @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                null,
                null);
            String authorizationHeader = request.getHeader("Authorization");
            /*if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                Worker worker = workerClient.token(authorizationHeader.substring("Bearer ".length()));
                token = new UsernamePasswordAuthenticationToken(
                    worker.getName(),
                    null,
                    AuthorityUtils.createAuthorityList(worker.getRole()));
            }*/
            SecurityContextHolder.getContext().setAuthentication(token);
        } catch (Exception e) {
            log.trace("error", e);
        }

        filterChain.doFilter(request, response);
    }
}
