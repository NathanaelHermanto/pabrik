package com.nathancorp.pabrik.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

// temp to allow h2-console before configuring the filter chain
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/v1/health", "/h2-console/**").permitAll()
                        .anyRequest().permitAll()  // for testing allow all request
                ).headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)) // for h2-console
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
