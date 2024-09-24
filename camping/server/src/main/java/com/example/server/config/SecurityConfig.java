package com.example.server.config;

import com.example.server.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors
                        .configurationSource(request -> {
                            CorsConfiguration configuration = new CorsConfiguration();
                            configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                            configuration.setAllowedMethods(Collections.singletonList("*")); // 모든 메서드를 허용
                            configuration.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더를 허용
                            configuration.setAllowCredentials(true);
                            return configuration;
                        })
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/**")
                        .ignoringRequestMatchers("/payment/**")
                        .ignoringRequestMatchers("/member/**")
                        .ignoringRequestMatchers("/oauth2/**")// CSRF 토큰을 제외할 요청 패턴
                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/payment/**").permitAll() // 결제 관련 요청을 모두 허용
                                .requestMatchers("/api/**").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                .requestMatchers("/favicon.ico").permitAll()
                                .requestMatchers("/member/**").permitAll()
                                .requestMatchers("/oauth2/**").permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}