package com.openfree_api.common.email;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/*
 * Envio de e-mail pela API HTTP do Resend.
 *
 * O Railway bloqueia SMTP nos planos mais baratos, por isso
 * usamos HTTPS. Sem RESEND_API_KEY (dev/teste) o e-mail não
 * é enviado: só registramos no log que ele seria enviado.
 */
@Service
public class EmailService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailService.class);

    private final RestClient restClient;

    private final String apiKey;

    private final String remetente;

    public EmailService(
            RestClient.Builder restClientBuilder,
            @Value("${app.email.resend-api-key:}") String apiKey,
            @Value("${app.email.from:OpenFree <nao-responda@genovaforge.com.br>}") String remetente
    ) {

        this.restClient =
                restClientBuilder
                        .baseUrl("https://api.resend.com")
                        .build();

        this.apiKey = apiKey;

        this.remetente = remetente;
    }

    public boolean configurado() {
        return apiKey != null && !apiKey.isBlank();
    }

    @Async
    public void enviar(EmailMensagem mensagem) {

        if (!configurado()) {

            log.warn(
                    "E-mail '{}' não enviado: RESEND_API_KEY não configurada.",
                    mensagem.assunto()
            );

            return;
        }

        Map<String, Object> corpo =
                Map.of(
                        "from", remetente,
                        "to", List.of(mensagem.para()),
                        "subject", mensagem.assunto(),
                        "html", mensagem.html(),
                        "text", mensagem.texto()
                );

        try {

            restClient
                    .post()
                    .uri("/emails")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(corpo)
                    .retrieve()
                    .toBodilessEntity();

            log.info("E-mail '{}' enviado.", mensagem.assunto());

        } catch (RestClientException exception) {

            /*
             * Não registramos o destinatário (LGPD) nem
             * relançamos: quem chamou já respondeu ao cliente.
             */
            log.error(
                    "Falha ao enviar e-mail '{}': {}",
                    mensagem.assunto(),
                    exception.getMessage()
            );
        }
    }
}
