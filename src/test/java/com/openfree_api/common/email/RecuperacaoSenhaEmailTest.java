package com.openfree_api.common.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RecuperacaoSenhaEmailTest {

    @Test
    void montaEmailComLinkEPrimeiroNome() {

        EmailMensagem email = RecuperacaoSenhaEmail.montar(
                "ana@email.com",
                "Ana Souza",
                "https://openfree.genovaforge.com.br/reset-password?token=abc",
                30
        );

        assertEquals("ana@email.com", email.para());
        assertEquals(RecuperacaoSenhaEmail.ASSUNTO, email.assunto());
        assertTrue(email.html().contains("Olá, Ana!"));
        assertTrue(email.html().contains("reset-password?token=abc"));
        assertTrue(email.texto().contains("reset-password?token=abc"));
        assertTrue(email.texto().contains("30 minutos"));
    }

    @Test
    void escapaHtmlNoNome() {

        EmailMensagem email = RecuperacaoSenhaEmail.montar(
                "x@email.com",
                "<script>alert(1)</script>",
                "https://site/reset-password?token=abc",
                30
        );

        assertFalse(email.html().contains("<script>"));
    }

    @Test
    void semNomeUsaSaudacaoSimples() {

        EmailMensagem email = RecuperacaoSenhaEmail.montar(
                "x@email.com", null, "https://site/r", 30);

        assertTrue(email.texto().startsWith("Olá!"));
    }
}
