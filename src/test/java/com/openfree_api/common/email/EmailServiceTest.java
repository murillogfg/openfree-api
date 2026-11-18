package com.openfree_api.common.email;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class EmailServiceTest {

    private final EmailMensagem mensagem =
            new EmailMensagem("ana@email.com", "Assunto", "<p>oi</p>", "oi");

    @Test
    void enviaPeloResendQuandoHaChave() {

        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(requestTo("https://api.resend.com/emails"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer re_teste"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("ana@email.com")))
                .andRespond(withSuccess("{\"id\":\"1\"}", MediaType.APPLICATION_JSON));

        EmailService service = new EmailService(builder, "re_teste", "OpenFree <a@b.com>");

        assertTrue(service.configurado());
        service.enviar(mensagem);

        server.verify();
    }

    @Test
    void falhaDoResendNaoDerrubaQuemChamou() {

        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(requestTo("https://api.resend.com/emails"))
                .andRespond(withServerError());

        EmailService service = new EmailService(builder, "re_teste", "OpenFree <a@b.com>");

        service.enviar(mensagem);

        server.verify();
    }

    @Test
    void semChaveNaoChamaOResend() {

        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        EmailService service = new EmailService(builder, "", "OpenFree <a@b.com>");

        assertFalse(service.configurado());
        service.enviar(mensagem);

        server.verify();
    }
}
