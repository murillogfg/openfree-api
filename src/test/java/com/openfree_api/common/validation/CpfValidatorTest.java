package com.openfree_api.common.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CpfValidatorTest {

    @Test
    void aceitaCpfValidoComOuSemPontuacao() {
        assertTrue(CpfValidator.isValid("529.982.247-25"));
        assertTrue(CpfValidator.isValid("52998224725"));
        assertTrue(CpfValidator.isValid("111.444.777-35"));
    }

    @Test
    void recusaDigitoVerificadorErrado() {
        assertFalse(CpfValidator.isValid("529.982.247-24"));
    }

    @Test
    void recusaSequenciaRepetidaETamanhoErrado() {
        assertFalse(CpfValidator.isValid("111.111.111-11"));
        assertFalse(CpfValidator.isValid("1234567890"));
        assertFalse(CpfValidator.isValid(null));
    }

    @Test
    void normalizaRemovendoPontuacao() {
        assertEquals("52998224725", CpfValidator.normalize("529.982.247-25"));
    }
}
