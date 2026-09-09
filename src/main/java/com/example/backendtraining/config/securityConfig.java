package com.example.backendtraining.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class securityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        http.authenticationProvider(authenticationProvider());
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/sellers/**").hasAnyRole("ADMIN", "SELLER", "CUSTOMER")
                .requestMatchers("/sellers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/customers/**").hasAnyRole("ADMIN", "SELLER", "CUSTOMER")
                .requestMatchers("/customers/**").hasAnyRole("ADMIN", "CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/products/**").hasAnyRole("ADMIN", "SELLER", "CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/products/**").hasRole("SELLER")
                .requestMatchers("/products/**").hasAnyRole("ADMIN", "SELLER")
                .requestMatchers(HttpMethod.POST, "/orders/**").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.PUT, "/orders/*/cancel").hasAnyRole("ADMIN", "CUSTOMER")
                .requestMatchers(HttpMethod.PUT, "/orders/*/accept").hasAnyRole("ADMIN", "SELLER")
                .requestMatchers(HttpMethod.PUT, "/orders/*/ship").hasAnyRole("ADMIN", "SELLER")
                .requestMatchers(HttpMethod.PUT, "/orders/*/assign/**").hasAnyRole("ADMIN", "SELLER")
                .requestMatchers(HttpMethod.PUT, "/orders/*/deliver").hasAnyRole("ADMIN", "SELLER", "DRIVER")
                .requestMatchers("/orders/**").hasAnyRole("ADMIN", "CUSTOMER", "SELLER", "DRIVER")
                .requestMatchers(HttpMethod.GET, "/drivers/**").hasAnyRole("ADMIN", "DRIVER")
                .requestMatchers("/drivers/**").hasRole("ADMIN")
                .anyRequest().authenticated()
        );
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
