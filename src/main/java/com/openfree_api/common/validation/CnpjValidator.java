package com.openfree_api.common.validation;

public final class CnpjValidator {

    private CnpjValidator() {
    }

    public static boolean isValid(String cnpj) {

        if (cnpj == null) {
            return false;
        }

        String numbers =
                cnpj.replaceAll("\\D", "");

        if (numbers.length() != 14) {
            return false;
        }

        /*
         * Rejeita sequências:
         * 00000000000000
         * 11111111111111
         * etc.
         */
        if (
                numbers.chars()
                        .distinct()
                        .count() == 1
        ) {
            return false;
        }

        try {

            int firstDigit =
                    calculateDigit(
                            numbers.substring(0, 12),
                            new int[]{
                                    5, 4, 3, 2,
                                    9, 8, 7, 6,
                                    5, 4, 3, 2
                            }
                    );

            if (
                    firstDigit
                    != Character.getNumericValue(
                            numbers.charAt(12)
                    )
            ) {
                return false;
            }

            int secondDigit =
                    calculateDigit(
                            numbers.substring(0, 13),
                            new int[]{
                                    6, 5, 4, 3, 2,
                                    9, 8, 7, 6,
                                    5, 4, 3, 2
                            }
                    );

            return (
                    secondDigit
                    == Character.getNumericValue(
                            numbers.charAt(13)
                    )
            );

        } catch (Exception exception) {

            return false;
        }
    }

    public static String normalize(
            String cnpj
    ) {

        if (cnpj == null) {
            return null;
        }

        return cnpj.replaceAll(
                "\\D",
                ""
        );
    }

    private static int calculateDigit(
            String base,
            int[] weights
    ) {

        int sum = 0;

        for (
                int index = 0;
                index < base.length();
                index++
        ) {

            int number =
                    Character.getNumericValue(
                            base.charAt(index)
                    );

            sum +=
                    number
                    * weights[index];
        }

        int remainder =
                sum % 11;

        return (
                remainder < 2
                ? 0
                : 11 - remainder
        );
    }
}