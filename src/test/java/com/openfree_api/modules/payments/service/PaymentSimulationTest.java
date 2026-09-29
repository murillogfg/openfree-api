package com.openfree_api.modules.payments.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.auth.service.EmpresaAuthService;
import com.openfree_api.modules.auth.service.UsuarioAuthService;
import com.openfree_api.modules.payments.entity.PaymentMethod;
import com.openfree_api.modules.payments.mapper.PaymentMapper;
import com.openfree_api.modules.payments.repository.PaymentRepository;

/*
 * Sem a propriedade app.payments.simulation-enabled=true
 * (só ligada no perfil dev), ninguém consegue marcar um
 * pagamento como feito sem pagar de verdade.
 */
@ExtendWith(MockitoExtension.class)
class PaymentSimulationTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentFeeService paymentFeeService;

    @Mock
    private EmpresaAuthService empresaAuthService;

    @Mock
    private UsuarioAuthService usuarioAuthService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void simulacaoDesligadaPorPadrao() {

        BusinessException erro = assertThrows(
                BusinessException.class,
                () -> paymentService.simularPagamento(
                        1L,
                        PaymentMethod.PIX,
                        authentication
                )
        );

        assertEquals(
                "O pagamento pela plataforma ainda não está disponível.",
                erro.getMessage()
        );

        verify(paymentRepository, never()).save(any());
    }
}
