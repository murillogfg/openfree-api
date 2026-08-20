package com.openfree_api.modules.auth.config;

import com.openfree_api.modules.auth.jwt.JwtAuthenticationFilter;
import com.openfree_api.modules.auth.security.CustomAuthenticationEntryPoint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;


    /*
     * As origens permitidas são definidas
     * de acordo com o ambiente.
     *
     * DEV:
     * localhost / 127.0.0.1
     *
     * PROD:
     * domínio oficial do frontend.
     */
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;


    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomAuthenticationEntryPoint authenticationEntryPoint
    ) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;

        this.authenticationEntryPoint =
                authenticationEntryPoint;
    }


    /*
     * Encoder utilizado para armazenar
     * senhas de forma segura.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    /*
     * AuthenticationManager utilizado
     * pelo fluxo de login.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration
                .getAuthenticationManager();
    }


    /*
     * Configuração principal de segurança.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                /*
                 * Utiliza a configuração CORS
                 * definida abaixo.
                 */
                .cors(Customizer.withDefaults())


                /*
                 * API REST utilizando JWT.
                 *
                 * Como não utilizamos sessão/cookie
                 * para autenticação, CSRF fica
                 * desabilitado.
                 */
                .csrf(csrf ->
                        csrf.disable()
                )


                /*
                 * A aplicação não mantém sessão
                 * no servidor.
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                /*
                 * Tratamento personalizado para
                 * requisições não autenticadas.
                 */
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                authenticationEntryPoint
                        )
                )


                /*
                 * Regras de autorização.
                 */
                .authorizeHttpRequests(auth -> auth


                        /*
                         * Autenticação, recuperação
                         * de senha e tratamento
                         * padrão de erro.
                         */
                        .requestMatchers(
                                "/auth/**",
                                "/error"
                        )
                        .permitAll()


                        /*
                         * Cadastro público
                         * de usuário.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/usuarios",
                                "/usuarios/"
                        )
                        .permitAll()


                        /*
                         * Swagger / OpenAPI.
                         *
                         * Em produção o Swagger
                         * será desabilitado através
                         * do application-prod.
                         */
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        )
                        .permitAll()


                        /*
                         * Console H2.
                         *
                         * Em produção o console
                         * estará desabilitado.
                         */
                        .requestMatchers(
                                "/h2-console/**"
                        )
                        .permitAll()


                        /*
                         * Uploads públicos.
                         *
                         * Permite que imagens e
                         * arquivos públicos sejam
                         * carregados pelo frontend.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/uploads/**"
                        )
                        .permitAll()


                        /*
                                * VAGAS PÚBLICAS PARA SEO
                                *
                                * A listagem e o detalhe de uma vaga
                                * podem ser consultados sem login.
                                *
                                * /jobs       -> listagem
                                * /jobs/{id}  -> detalhe
                                *
                                * Rotas mais profundas continuam privadas,
                                * como /jobs/{id}/applications.
                                */
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/jobs",
                                        "/jobs/",
                                        "/jobs/*"
                                )
                                .permitAll()


                        /*
                         * Apenas empresas podem
                         * criar vagas.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/jobs",
                                "/jobs/"
                        )
                        .hasRole("EMPRESA")


                        /*
                         * Dashboard da empresa.
                         */
                        .requestMatchers(
                                "/dashboard/company"
                        )
                        .hasRole("EMPRESA")


                        /*
                         * Dashboard pessoal.
                         *
                         * Atualmente usuários com
                         * role FREELANCER ou EMPRESA
                         * podem acessar.
                         */
                        .requestMatchers(
                                "/dashboard/freelancer"
                        )
                        .hasAnyRole(
                                "FREELANCER",
                                "EMPRESA"
                        )


                        /*
                         * Candidaturas do próprio
                         * usuário.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/applications/me"
                        )
                        .hasAnyRole(
                                "FREELANCER",
                                "EMPRESA"
                        )


                        /*
                         * Módulos disponíveis apenas
                         * para usuários autenticados.
                         */
                        .requestMatchers(
                                "/notifications/**",
                                "/chat/**",
                                "/reviews/**"
                        )
                        .authenticated()


                        /*
                         * Qualquer outra rota da API
                         * exige autenticação.
                         */
                        .anyRequest()
                        .authenticated()
                )


                /*
                 * Necessário para o H2 Console
                 * funcionar em desenvolvimento.
                 */
                .headers(headers ->
                        headers.frameOptions(frame ->
                                frame.sameOrigin()
                        )
                )


                /*
                 * Executa nosso filtro JWT antes
                 * do filtro padrão de autenticação
                 * do Spring Security.
                 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }


    /*
     * =====================================================
     * CORS
     * =====================================================
     *
     * As origens são carregadas de:
     *
     * app.cors.allowed-origins
     *
     * DEV:
     * http://localhost:*
     * http://127.0.0.1:*
     *
     * PROD:
     * domínio real do frontend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();


        /*
         * Permite informar mais de uma origem
         * separada por vírgula.
         */
        List<String> origins =
                Arrays.stream(
                                allowedOrigins.split(",")
                        )
                        .map(String::trim)
                        .filter(origin ->
                                !origin.isBlank()
                        )
                        .toList();


        configuration.setAllowedOriginPatterns(
                origins
        );


        /*
         * Métodos HTTP utilizados pela OpenFree.
         */
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


        /*
         * Headers que o frontend pode enviar.
         */
        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept",
                        "Origin"
                )
        );


        /*
         * Headers que o navegador poderá
         * acessar na resposta.
         */
        configuration.setExposedHeaders(
                List.of(
                        "Authorization",
                        "Content-Disposition"
                )
        );


        /*
         * Mantemos habilitado para suportar
         * corretamente requisições autenticadas
         * entre frontend e API.
         */
        configuration.setAllowCredentials(
                true
        );


        /*
         * Cache do preflight CORS:
         * 1 hora.
         */
        configuration.setMaxAge(
                3600L
        );


        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        /*
         * Aplica a configuração para
         * toda a API.
         */
        source.registerCorsConfiguration(
                "/**",
                configuration
        );


        return source;
    }
}