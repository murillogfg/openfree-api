package com.openfree_api.common.validation;

/*
 * Validação de CPF pelos dígitos verificadores.
 * Sempre trabalhamos com os 11 números, sem pontos.
 */
public final class CpfValidator {

    private CpfValidator() {
    }

    public static String normalize(
            String cpf
    ) {

        if (cpf == null) {
            return null;
        }

        return cpf.replaceAll(
                "\\D",
                ""
        );
    }

    public static boolean isValid(
            String cpf
    ) {

        String numeros =
                normalize(cpf);

        if (
                numeros == null
                || numeros.length() != 11
        ) {
            return false;
        }

        /*
         * Rejeita 000.000.000-00, 111.111.111-11 etc.
         */
        if (
                numeros.chars()
                        .distinct()
                        .count() == 1
        ) {
            return false;
        }

        return digito(numeros, 9) == numeros.charAt(9) - '0'
                && digito(numeros, 10) == numeros.charAt(10) - '0';
    }

    private static int digito(
            String numeros,
            int quantidade
    ) {

        int soma = 0;

        for (int i = 0; i < quantidade; i++) {
            soma += (numeros.charAt(i) - '0') * (quantidade + 1 - i);
        }

        int resto = (soma * 10) % 11;

        return resto == 10 ? 0 : resto;
    }
}
