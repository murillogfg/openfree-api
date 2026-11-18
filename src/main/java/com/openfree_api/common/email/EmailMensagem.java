package com.openfree_api.common.email;

public record EmailMensagem(
        String para,
        String assunto,
        String html,
        String texto
) {
}
