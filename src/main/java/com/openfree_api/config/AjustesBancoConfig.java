package com.openfree_api.config;

import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/*
 * Ajustes de banco que o "ddl-auto=update" do Hibernate
 * não faz sozinho (ele cria colunas novas, mas nunca
 * remove um NOT NULL de uma coluna que já existe).
 *
 * Todos os comandos podem rodar várias vezes sem efeito
 * colateral. Se algum falhar, a API sobe mesmo assim e
 * o erro aparece no log.
 */
@Component
public class AjustesBancoConfig implements ApplicationRunner {

    private static final Logger log =
            LoggerFactory.getLogger(AjustesBancoConfig.class);

    private static final String[] COMANDOS = {

            /*
             * Contratante pessoa física não tem CNPJ.
             */
            "ALTER TABLE empresas ALTER COLUMN cnpj DROP NOT NULL",

            /*
             * Empresas criadas antes do campo tipo_pessoa
             * são todas PJ.
             */
            "UPDATE empresas SET tipo_pessoa = 'PJ' WHERE tipo_pessoa IS NULL"
    };

    private final DataSource dataSource;

    public AjustesBancoConfig(
            DataSource dataSource
    ) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(
            ApplicationArguments args
    ) {

        for (String comando : COMANDOS) {

            try (
                    Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()
            ) {

                statement.execute(comando);

            } catch (Exception exception) {

                log.warn(
                        "Ajuste de banco não aplicado: '{}'. Motivo: {}",
                        comando,
                        exception.getMessage()
                );
            }
        }
    }
}
