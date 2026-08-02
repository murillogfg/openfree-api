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

        // Login e tratamento de erro
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

        // Swagger / OpenAPI
        .requestMatchers(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**"
        ).permitAll()

        // Console H2
        .requestMatchers(
                "/h2-console/**"
        ).permitAll()

        // Somente empresa cria vaga
        .requestMatchers(
                HttpMethod.POST,
                "/jobs",
                "/jobs/"
        ).hasRole("EMPRESA")

        // Dashboard da empresa
        .requestMatchers(
                "/dashboard/company"
        ).hasRole("EMPRESA")

        // Dashboard do freelancer
        .requestMatchers(
                "/dashboard/freelancer"
        ).hasRole("FREELANCER")

        // Restante exige autenticação

        .requestMatchers(
        HttpMethod.GET,
        "/applications/me"
).hasRole("FREELANCER")

.requestMatchers(
        "/notifications/**"
).authenticated()


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
