package com.burntoburn.easyshift.config;

import com.burntoburn.easyshift.repository.user.UserRepository;
import com.burntoburn.easyshift.service.login.CustomUserDetailsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private static final String[] PUBLIC_STATIC_RESOURCES = {
            "/", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/h2-console/**" , "/actuator/prometheus/**"
    };
    private static final String[] PUBLIC_API_ENDPOINTS = {
            "/api/auth/**", "/api/public/**", "/oauth2/**",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityContext(securityContext->securityContext.requireExplicitSave(false))
                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 필요한 메서드만 허용
                    configuration.setAllowCredentials(true); // 세션 기반 인증
                    configuration.setMaxAge(7200L); // Preflight 요청 캐싱
                    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept","ngrok-skip-browser-warning")); // 명시적으로 허용할 헤더
                    configuration.setExposedHeaders(List.of("Access", "Authorization", "ngrok-skip-browser-warning")); // 노출할 헤더 추가
                    return configuration;
                }))
                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())
                // 로그인 폼 활성화
                .formLogin(form -> form.disable())

                .logout(logout -> logout
                        .logoutSuccessUrl("/") // 로그아웃 성공 후 메인 페이지로 이동
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // 쿠키 삭제
                )

                // HTTP Basic 인증 비활성화
                .httpBasic(httpBasic -> httpBasic.disable())
                // 경로별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_STATIC_RESOURCES).permitAll() // Swagger 허용
                        .requestMatchers(PUBLIC_API_ENDPOINTS).permitAll() // Swagger 허용
                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
                )
        // 세션 설정
                .sessionManagement(session -> session
                        .maximumSessions(1) // 한 사용자당 하나의 세션만 유지
                        .maxSessionsPreventsLogin(false) // 이전 세션 무효
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable())); // ✅ 여기가 정석

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}