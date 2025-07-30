package com.inninglog.inninglog.global.auth;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.remote.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                        .requestMatchers(
                                "/login/page",
                                "/callback",
                                "/test",
                                "/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/kbo/**",
                                "/actuator/**",
                                "/health",
                                "/error",
                                "/"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:8000",
                "http://127.0.0.1:8000",
                "http://inninglog.shop",
                "https://inninglog.shop",
                "https://inninglog.shop/callback",
                "https://api.inninglog.shop",
                "http://api.inninglog.shop",
                "http://localhost:62378",
                "http://localhost:8002",
                "http://127.0.0.1:8002",
                "https://*.netlify.app",
                "https://spontaneous-yeot-598898.netlify.app",
                "http://localhost:5000/onboarding6"



                ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}