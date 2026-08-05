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

import org.springframework.security.config.Customizer;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


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
                .cors(Customizer.withDefaults())

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

                        // Apenas empresas podem criar vagas
                        .requestMatchers(
                                HttpMethod.POST,
                                "/jobs",
                                "/jobs/"
                        ).hasRole("EMPRESA")

                        // Dashboard da empresa
                        .requestMatchers(
                                "/dashboard/company"
                        ).hasRole("EMPRESA")

                        /*
                         * Usuários FREELANCER e EMPRESA podem acessar
                         * o dashboard pessoal.
                         *
                         * Isso é necessário porque, atualmente, quando
                         * o usuário cria uma empresa, a role dele muda
                         * de FREELANCER para EMPRESA.
                         */
                        .requestMatchers(
                                "/dashboard/freelancer"
                        ).hasAnyRole(
                                "FREELANCER",
                                "EMPRESA"
                        )

                        // Candidaturas do próprio freelancer
                        .requestMatchers(
                                HttpMethod.GET,
                                "/applications/me"
                        ).hasAnyRole(
                                "FREELANCER",
                                "EMPRESA"
                        )

                        // Módulos disponíveis para usuários autenticados
                        .requestMatchers(
                                "/notifications/**",
                                "/chat/**",
                                "/reviews/**"
                        ).authenticated()

                        // Qualquer outra rota exige autenticação


                        .requestMatchers(
                        HttpMethod.GET,
                        "/uploads/**"
                              ).permitAll()
                        .anyRequest()
                        .authenticated()
                )

                // Permite abrir o H2 Console dentro de iframe
                .headers(headers ->
                        headers.frameOptions(frame ->
                                frame.sameOrigin()
                        )
                )

                // Executa o JWT antes do filtro padrão de autenticação
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration =
            new CorsConfiguration();

    /*
     * Ambiente de desenvolvimento:
     * aceita qualquer porta do Angular em localhost.
     */
    configuration.setAllowedOriginPatterns(
            List.of(
                    "http://localhost:*",
                    "http://127.0.0.1:*"
            )
    );

    configuration.setAllowedMethods(
            List.of(
                    "GET",
                    "POST",
                    "PUT",
                    "PATCH",
                    "DELETE",
                    "OPTIONS"
            )
    );

    configuration.setAllowedHeaders(
            List.of(
                    "Authorization",
                    "Content-Type",
                    "Accept",
                    "Origin"
            )
    );

    configuration.setExposedHeaders(
            List.of(
                    "Authorization",
                    "Content-Disposition"
            )
    );

    configuration.setAllowCredentials(true);

    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration(
            "/**",
            configuration
    );

    return source;
}
}