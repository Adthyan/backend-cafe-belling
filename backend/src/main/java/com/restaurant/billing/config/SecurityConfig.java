package com.restaurant.billing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                                auth ->
                                        auth.requestMatchers(HttpMethod.GET, "/menu/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/menu-items/active")
                                                .permitAll()
                                        .requestMatchers(HttpMethod.POST, "/api/sales/checkout")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.PUT, "/api/sales/*/mark-paid")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.POST, "/api/sales/*/gateway-qr")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/sales/monthly")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/payments/upi-uri")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.POST, "/api/invoices")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.POST, "/api/payments/qr")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/payments/*/status")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.POST, "/api/payments/webhook/razorpay")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/settings")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/menu-items/**")
                                        .authenticated()
                                        .requestMatchers(HttpMethod.POST, "/api/menu-items")
                                        .authenticated()
                                        .requestMatchers(HttpMethod.PUT, "/api/menu-items/**")
                                        .authenticated()
                                        .requestMatchers(HttpMethod.DELETE, "/api/menu-items/**")
                                        .authenticated()
                                        .requestMatchers(HttpMethod.PATCH, "/api/settings")
                                        .authenticated()
                                        .anyRequest()
                                        .permitAll())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin").password(encoder.encode("admin")).roles("ADMIN").build());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
