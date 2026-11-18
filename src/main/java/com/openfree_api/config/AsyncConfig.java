package com.openfree_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/*
 * Habilita @Async. Usado no envio de e-mail para que a
 * requisição responda na hora e o tempo de resposta não
 * revele se o e-mail está cadastrado.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
