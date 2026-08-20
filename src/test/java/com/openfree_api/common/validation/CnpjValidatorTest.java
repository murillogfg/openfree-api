package com.openfree_api.common.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CnpjValidatorTest {

    @Test
    void deveRejeitarCnpjNulo() {

        assertFalse(
                CnpjValidator.isValid(null)
        );
    }

    @Test
    void deveRejeitarCnpjCurto() {

        assertFalse(
                CnpjValidator.isValid(
                        "123"
                )
        );
    }

    @Test
    void deveRejeitarCnpjComNumerosRepetidos() {

        assertFalse(
                CnpjValidator.isValid(
                        "00.000.000/0000-00"
                )
        );
    }

    @Test
    void deveRejeitarCnpjComDigitosVerificadoresInvalidos() {

        assertFalse(
                CnpjValidator.isValid(
                        "12.345.678/0001-99"
                )
        );
    }
}