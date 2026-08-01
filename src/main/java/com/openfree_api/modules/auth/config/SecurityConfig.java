package com.openfree_api.modules.auth.config;

import com.openfree_api.modules.auth.jwt.JwtAuthenticationFilter;
import com.openfree_api.modules.auth.security.CustomAuthenticationEntryPoint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomAuthenticationEntryPoint authenticationEntryPoint
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                authenticationEntryPoint
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Rotas públicas de autenticação
                        .requestMatchers(
                                "/auth/**",
                                "/error"
                        ).permitAll()

                        // Cadastro público de usuário
                        .requestMatchers(
                                HttpMethod.POST,
                                "/usuarios",
                                "/usuarios/"
                        ).permitAll()

                        // Console H2 para desenvolvimento
                        .requestMatchers(
                                "/h2-console/**"
                        ).permitAll()

                        // Somente EMPRESA pode criar vaga
                        .requestMatchers(
                            HttpMethod.POST,
                                "/jobs",
                                "/jobs/"
                        ).hasRole("EMPRESA")
                        // Todo o restante exige autenticação
                        .anyRequest().authenticated()
                )

                // Necessário para o H2 Console abrir dentro de iframe
                .headers(headers ->
                        headers.frameOptions(frame ->
                                frame.sameOrigin()
                        )
                )

                // Executa o filtro JWT antes do filtro padrão de login
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}