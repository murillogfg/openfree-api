package com.openfree_api.common.cnpj;

public interface CnpjLookupService {

    CnpjLookupResult consultar(
            String cnpj
    );
}