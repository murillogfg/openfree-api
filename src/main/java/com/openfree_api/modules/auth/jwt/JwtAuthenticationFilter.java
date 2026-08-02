package com.openfree_api.modules.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        if (authorizationHeader == null
                || !authorizationHeader.startsWith(BEARER_PREFIX)) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader
                .substring(BEARER_PREFIX.length())
                .trim();

        if (token.isBlank()) {

            log.warn(
                    "Cabeçalho Authorization sem token na rota '{}'.",
                    request.getRequestURI()
            );

            filterChain.doFilter(request, response);
            return;
        }

        try {

            String email =
                    jwtService.extrairEmail(token);

            boolean usuarioAindaNaoAutenticado =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null;

            if (email != null && usuarioAindaNaoAutenticado) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                if (jwtService.tokenValido(token, userDetails)) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);

                    log.debug(
                            "Usuário '{}' autenticado pelo JWT na rota '{}'.",
                            email,
                            request.getRequestURI()
                    );

                } else {

                    log.warn(
                            "JWT rejeitado para o usuário '{}' na rota '{}'.",
                            email,
                            request.getRequestURI()
                    );
                }
            }

        } catch (ExpiredJwtException exception) {

            SecurityContextHolder.clearContext();

            log.warn(
                    "Token JWT expirado na rota '{}'.",
                    request.getRequestURI()
            );

        } catch (UsernameNotFoundException exception) {

            SecurityContextHolder.clearContext();

            log.warn(
                    "JWT referencia um usuário inexistente na rota '{}'.",
                    request.getRequestURI()
            );

        } catch (JwtException | IllegalArgumentException exception) {

            SecurityContextHolder.clearContext();

            log.warn(
                    "Token JWT inválido ou malformado na rota '{}'. Motivo: {}",
                    request.getRequestURI(),
                    exception.getClass().getSimpleName()
            );
        }

        filterChain.doFilter(request, response);
    }
}