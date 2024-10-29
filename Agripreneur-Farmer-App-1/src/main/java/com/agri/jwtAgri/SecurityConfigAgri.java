package com.agri.jwtAgri;
/*
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableWebSecurity
public class SecurityConfigAgri {

    private final JwtRequestFilterAgri jwtAuthFilter;
    private final CustomUserDetailsServiceAgri userDetailsService;

    public SecurityConfigAgri(JwtRequestFilterAgri jwtAuthFilter, CustomUserDetailsServiceAgri userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/authenticate","/api/agripreneurs/**","/api/register/","/api/admin/**","/api/agripreneurs/updateAgripreneurProfile","/admin/categories/**","/api/consultant/**","/districts/**","/api/farmers/**","/states/**","/subdistricts/**", "/error","/api/agripreneurs/login","/api/agripreneurs/verify-login-otp"
                		,"/api/agripreneurs/viewProfile","/api/agripreneurs/register"
                		,"/api/agripreneurs/verify-registration-otp","/api/agripreneurs/agripreneurs-by-keyword/{keywords}").permitAll().anyRequest().authenticated())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
*/