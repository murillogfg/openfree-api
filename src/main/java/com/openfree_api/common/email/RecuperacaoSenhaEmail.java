package com.openfree_api.common.email;

import org.springframework.web.util.HtmlUtils;

/*
 * Conteúdo do e-mail de recuperação de senha.
 */
public final class RecuperacaoSenhaEmail {

    public static final String ASSUNTO =
            "Redefinição de senha - OpenFree";

    private RecuperacaoSenhaEmail() {
    }

    public static EmailMensagem montar(
            String para,
            String nome,
            String link,
            int minutosValidade
    ) {

        String saudacao =
                saudacao(nome);

        String linkHtml =
                HtmlUtils.htmlEscape(link);

        String html =
                "<!DOCTYPE html>"
                + "<html lang=\"pt-BR\"><body style=\"margin:0;padding:0;background:#f4f6fb;font-family:Arial,Helvetica,sans-serif;color:#1f2937;\">"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"padding:32px 16px;\"><tr><td align=\"center\">"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:520px;background:#ffffff;border-radius:12px;padding:32px;\">"
                + "<tr><td style=\"font-size:22px;font-weight:bold;color:#4f46e5;padding-bottom:24px;\">OpenFree</td></tr>"
                + "<tr><td style=\"font-size:16px;line-height:24px;\">"
                + "<p style=\"margin:0 0 16px;\">" + HtmlUtils.htmlEscape(saudacao) + "</p>"
                + "<p style=\"margin:0 0 24px;\">Recebemos um pedido para redefinir a senha da sua conta. Clique no botão abaixo para criar uma nova senha.</p>"
                + "</td></tr>"
                + "<tr><td align=\"center\" style=\"padding-bottom:24px;\">"
                + "<a href=\"" + linkHtml + "\" style=\"display:inline-block;background:#4f46e5;color:#ffffff;text-decoration:none;font-weight:bold;padding:14px 28px;border-radius:8px;\">Redefinir senha</a>"
                + "</td></tr>"
                + "<tr><td style=\"font-size:14px;line-height:21px;color:#4b5563;\">"
                + "<p style=\"margin:0 0 12px;\">O link vale por " + minutosValidade + " minutos e só pode ser usado uma vez.</p>"
                + "<p style=\"margin:0 0 12px;\">Se o botão não funcionar, copie e cole este endereço no navegador:<br>"
                + "<a href=\"" + linkHtml + "\" style=\"color:#4f46e5;word-break:break-all;\">" + linkHtml + "</a></p>"
                + "<p style=\"margin:0;\">Se você não pediu a redefinição, ignore este e-mail. Sua senha continua a mesma.</p>"
                + "</td></tr>"
                + "</table>"
                + "<p style=\"font-size:12px;color:#9ca3af;margin:16px 0 0;\">Este é um e-mail automático, não responda.</p>"
                + "</td></tr></table></body></html>";

        String texto =
                saudacao + "\n\n"
                + "Recebemos um pedido para redefinir a senha da sua conta no OpenFree.\n"
                + "Para criar uma nova senha, acesse:\n\n"
                + link + "\n\n"
                + "O link vale por " + minutosValidade + " minutos e só pode ser usado uma vez.\n"
                + "Se você não pediu a redefinição, ignore este e-mail.";

        return new EmailMensagem(
                para,
                ASSUNTO,
                html,
                texto
        );
    }

    private static String saudacao(String nome) {

        if (nome == null || nome.isBlank()) {
            return "Olá!";
        }

        return "Olá, " + nome.trim().split("\\s+")[0] + "!";
    }
}
