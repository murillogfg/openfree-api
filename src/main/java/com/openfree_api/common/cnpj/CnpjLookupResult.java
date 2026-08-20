package com.openfree_api.common.cnpj;

public record CnpjLookupResult(

        CnpjLookupStatus status,

        String cnpj,

        String razaoSocial,

        String nomeFantasia,

        String situacao

) {

    public boolean verificado() {

        return status
                == CnpjLookupStatus.VERIFIED;
    }


    public boolean inexistente() {

        return status
                == CnpjLookupStatus.NOT_FOUND;
    }


    public boolean indisponivel() {

        return status
                == CnpjLookupStatus.UNAVAILABLE;
    }
}