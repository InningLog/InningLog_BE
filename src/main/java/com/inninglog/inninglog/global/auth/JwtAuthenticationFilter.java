package com.inninglog.inninglog.global.auth;

import com.inninglog.inninglog.global.auth.service.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider; // ✅ 이름 반영

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtProvider.resolveToken(request);  // Authorization 헤더에서 JWT 추출 (resolveToken())

        if (token != null && jwtProvider.validateToken(token)) { //validateToken()으로 유효한지 검사
            Authentication authentication = jwtProvider.getAuthentication(token); // 유효하면 → getAuthentication() 호출 → 사용자 인증 객체 생성
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            log.debug("유효하지 않은 토큰이거나 토큰 없음");
        }

        filterChain.doFilter(request, response);
    }
}
