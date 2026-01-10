package com.example.catalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // ปิด CSRF เพราะเราใช้ JWT (Stateless)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/graphiql/**", "/graphql/**").permitAll() // เปิดให้เข้าถึง GraphQL Endpoint (เดี๋ยวเราไปจัดการ Logic ข้างใน หรือถ้าจะเข้มงวดให้เปิดแค่ Login Mutation ก็ได้ แต่เพื่อให้ง่ายต่อการ Test เปิดไว้ก่อน)
                .anyRequest().authenticated()
            )
            // ใส่ Filter ของเราไปแทรกก่อน Filter เดิมของ Spring
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); 
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
