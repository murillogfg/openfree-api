package com.openfree_api.common.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;


import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /*
     * Regras de negócio controladas pela aplicação.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {

        log.warn(
                "Regra de negócio rejeitada na rota '{}': {}",
                request.getRequestURI(),
                exception.getMessage()
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                List.of(exception.getMessage())
        );
    }

    /*
     * Erros de validação provenientes de @Valid.
     */
  @ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
) {

    List<String> errors = exception
            .getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::formatarErroDeBinding)
            .toList();

    boolean possuiErroDeConversao = exception
            .getBindingResult()
            .getFieldErrors()
            .stream()
            .anyMatch(FieldError::isBindingFailure);

    String message = possuiErroDeConversao
            ? "Valor inválido para os parâmetros informados."
            : "Erro de validação.";

    log.warn(
            "Erro de validação ou conversão na rota '{}': {}",
            request.getRequestURI(),
            errors
    );

    return buildResponse(
            HttpStatus.BAD_REQUEST,
            message,
            errors
    );



    
}

    /*
     * JSON ausente, malformado ou com tipo incompatível.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonInvalido(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {

        log.warn(
                "JSON inválido na rota '{}'. Motivo: {}",
                request.getRequestURI(),
                exception.getMostSpecificCause().getMessage()
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Não foi possível interpretar o JSON enviado.",
                List.of(
                        "Verifique a estrutura do JSON, os tipos dos campos "
                                + "e os valores dos enums."
                )
        );
    }

    /*
     * Enum ou parâmetro de URL com tipo inválido.
     *
     * Exemplo:
     * GET /jobs?status=ABERTA
     * quando ABERTA não existe no enum.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {

        String parametro = exception.getName();
        Object valorRecebido = exception.getValue();

        String mensagem = String.format(
                "O valor '%s' é inválido para o parâmetro '%s'.",
                valorRecebido,
                parametro
        );

        log.warn(
                "Parâmetro inválido na rota '{}': parâmetro='{}', valor='{}'",
                request.getRequestURI(),
                parametro,
                valorRecebido
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                mensagem,
                List.of(
                        "Informe um valor compatível com o tipo esperado."
                )
        );
    }

    /*
     * Parâmetro obrigatório ausente.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {

        String mensagem = String.format(
                "O parâmetro '%s' é obrigatório.",
                exception.getParameterName()
        );

        log.warn(
                "Parâmetro obrigatório ausente na rota '{}': {}",
                request.getRequestURI(),
                exception.getParameterName()
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                mensagem,
                List.of(mensagem)
        );
    }

    /*
     * E-mail, CNPJ ou outra informação com constraint UNIQUE repetida.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {

        log.warn(
                "Violação de integridade na rota '{}'. Tipo: {}",
                request.getRequestURI(),
                exception.getClass().getSimpleName()
        );

        return buildResponse(
                HttpStatus.CONFLICT,
                "Não foi possível concluir a operação porque os dados entram em conflito.",
                List.of(
                        "Verifique se e-mail, CNPJ ou outro campo único já está cadastrado."
                )
        );
    }

    /*
     * Método HTTP incorreto.
     *
     * Exemplo: POST em um endpoint que aceita apenas GET.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {

        String mensagem = String.format(
                "O método HTTP '%s' não é suportado nesta rota.",
                exception.getMethod()
        );

        log.warn(
                "Método HTTP não suportado na rota '{}': {}",
                request.getRequestURI(),
                exception.getMethod()
        );

        return buildResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                mensagem,
                List.of(mensagem)
        );
    }

    /*
     * Rota ou recurso estático inexistente.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            NoResourceFoundException exception,
            HttpServletRequest request
    ) {

        log.warn(
                "Recurso não encontrado: método='{}', rota='{}'",
                request.getMethod(),
                request.getRequestURI()
        );

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Recurso não encontrado.",
                List.of(
                        "Verifique a URL e o método HTTP utilizados."
                )
        );
    }

    /*
     * Usuário autenticado, mas sem permissão suficiente.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {

        log.warn(
                "Acesso negado na rota '{}'.",
                request.getRequestURI()
        );

        return buildResponse(
                HttpStatus.FORBIDDEN,
                "Você não possui permissão para realizar esta operação.",
                List.of("Acesso negado.")
        );
    }

    /*
     * Falha de autenticação que chegar até o ControllerAdvice.
     *
     * Os erros bloqueados diretamente pelo Spring Security continuam sendo
     * tratados pelo CustomAuthenticationEntryPoint.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException exception,
            HttpServletRequest request
    ) {

        log.warn(
                "Falha de autenticação na rota '{}'. Tipo: {}",
                request.getRequestURI(),
                exception.getClass().getSimpleName()
        );

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Usuário inexistente ou credenciais inválidas.",
                List.of("Não foi possível autenticar o usuário.")
        );
    }

    /*
     * Última camada de proteção para falhas inesperadas.
     *
     * A exceção completa fica nos logs, mas não é exposta ao cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception exception,
            HttpServletRequest request
    ) {

        log.error(
                "Erro inesperado na rota '{}'. Tipo: {}. Mensagem: {}",
                request.getRequestURI(),
                exception.getClass().getName(),
                exception.getMessage(),
                exception
        );

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno.",
                List.of(
                        "Erro inesperado no servidor."
                )
        );
    }
@ExceptionHandler(BindException.class)
public ResponseEntity<ErrorResponse> handleBindException(
        BindException exception,
        HttpServletRequest request
) {

    List<String> errors = exception
            .getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> formatarErroDeBinding(error))
            .toList();

    log.warn(
            "Erro de conversão de parâmetros na rota '{}': {}",
            request.getRequestURI(),
            errors
    );

    return buildResponse(
            HttpStatus.BAD_REQUEST,
            "Valor inválido para os filtros informados.",
            errors
    );
}



    

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            List<String> errors
    ) {

        ErrorResponse response = new ErrorResponse(
                false,
                message,
                errors,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

  private String formatarErroDeBinding(FieldError error) {

    if ("status".equals(error.getField())
            && error.isBindingFailure()) {

        Object valorRecebido = error.getRejectedValue();

        return "Status da vaga '"
                + valorRecebido
                + "' é inválido. Valores permitidos: "
                + "RASCUNHO, PUBLICADA, FINALIZADA.";
    }

    if (error.isBindingFailure()) {

        return "O valor '"
                + error.getRejectedValue()
                + "' é inválido para o campo '"
                + error.getField()
                + "'.";
    }

    return error.getField()
            + ": "
            + error.getDefaultMessage();
}


}