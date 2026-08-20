package com.openfree_api.common.cnpj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.http.HttpStatusCode;

import org.springframework.stereotype.Service;

import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;


@Service
public class BrasilApiCnpjLookupService
        implements CnpjLookupService {


    private final RestClient restClient;


    public BrasilApiCnpjLookupService(
            RestClient.Builder restClientBuilder
    ) {

        this.restClient =
                restClientBuilder
                        .baseUrl(
                                "https://brasilapi.com.br"
                        )
                        .build();
    }


    @Override
    public CnpjLookupResult consultar(
            String cnpj
    ) {

        try {

            return restClient
                    .get()
                    .uri(
                            "/api/cnpj/v1/{cnpj}",
                            cnpj
                    )
                    .exchange(
                            (
                                    request,
                                    response
                            ) -> {

                                HttpStatusCode status =
                                        response
                                                .getStatusCode();


                                /*
                                 * CNPJ não encontrado.
                                 */
                                if (
                                        status.value()
                                        == 404
                                ) {

                                    return new CnpjLookupResult(
                                            CnpjLookupStatus.NOT_FOUND,
                                            cnpj,
                                            null,
                                            null,
                                            null
                                    );
                                }


                                /*
                                 * Qualquer outro erro da API
                                 * será tratado como indisponibilidade.
                                 *
                                 * Não derrubamos o cadastro.
                                 */
                                if (
                                        status.isError()
                                ) {

                                    return new CnpjLookupResult(
                                            CnpjLookupStatus.UNAVAILABLE,
                                            cnpj,
                                            null,
                                            null,
                                            null
                                    );
                                }


                                BrasilApiCnpjResponse body =
                                        response
                                                .bodyTo(
                                                        BrasilApiCnpjResponse.class
                                                );


                                if (body == null) {

                                    return new CnpjLookupResult(
                                            CnpjLookupStatus.UNAVAILABLE,
                                            cnpj,
                                            null,
                                            null,
                                            null
                                    );
                                }


                                return new CnpjLookupResult(
                                        CnpjLookupStatus.VERIFIED,
                                        cnpj,
                                        body.razaoSocial(),
                                        body.nomeFantasia(),
                                        body.descricaoSituacaoCadastral()
                                );
                            }
                    );

        } catch (
                RestClientException exception
        ) {

            /*
             * Timeout, DNS, conexão recusada,
             * serviço fora do ar etc.
             *
             * Não impedimos o cadastro.
             */
            return new CnpjLookupResult(
                    CnpjLookupStatus.UNAVAILABLE,
                    cnpj,
                    null,
                    null,
                    null
            );
        }
    }


    /*
     * Só precisamos mapear os campos que
     * realmente interessam à OpenFree.
     *
     * Campos extras da API serão ignorados.
     */
    @JsonIgnoreProperties(
            ignoreUnknown = true
    )
    private record BrasilApiCnpjResponse(

            String cnpj,

            @JsonProperty(
                    "razao_social"
            )
            String razaoSocial,

            @JsonProperty(
                    "nome_fantasia"
            )
            String nomeFantasia,

            @JsonProperty(
                    "descricao_situacao_cadastral"
            )
            String descricaoSituacaoCadastral

    ) {
    }
}