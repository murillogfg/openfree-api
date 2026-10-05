package com.openfree_api.modules.jobs.service;

import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.entity.Vaga;
import com.openfree_api.modules.jobs.repository.VagaRepository;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;


@Service
public class SitemapService {

    private static final ZoneId
            ZONA_NEGOCIO =
            ZoneId.of(
                    "America/Sao_Paulo"
            );


    private final VagaRepository
            vagaRepository;


    @Value(
            "${app.frontend.url:https://openfree.genovaforge.com.br}"
    )
    private String frontendUrl;


    public SitemapService(
            VagaRepository vagaRepository
    ) {

        this.vagaRepository =
                vagaRepository;
    }


    @Transactional(readOnly = true)
    public String gerarSitemap() {

        LocalDate hoje =
                LocalDate.now(
                        ZONA_NEGOCIO
                );


        List<Vaga> vagas =
                vagaRepository
                        .findByStatus(
                                StatusVaga.PUBLICADA
                        )
                        .stream()

                        .filter(
                                vaga ->
                                        vaga.getDataServico()
                                                != null
                        )

                        .filter(
                                vaga ->
                                        !vaga.getDataServico()
                                                .isBefore(
                                                        hoje
                                                )
                        )

                        .toList();


        String site =
                normalizarSiteUrl(
                        frontendUrl
                );


        StringBuilder xml =
                new StringBuilder();


        xml.append(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        );


        xml.append(
                "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n"
        );


        adicionarUrl(
                xml,
                site + "/jobs",
                null
        );


        for (
                Vaga vaga
                : vagas
        ) {

            LocalDate ultimaAlteracao =
                    vaga.getUpdatedAt() == null
                            ? null
                            : vaga.getUpdatedAt()
                            .toLocalDate();


            adicionarUrl(
                    xml,
                    site
                            + "/jobs/"
                            + vaga.getId(),
                    ultimaAlteracao
            );
        }


        xml.append(
                "</urlset>"
        );


        return xml.toString();
    }


    private void adicionarUrl(
            StringBuilder xml,
            String url,
            LocalDate lastModified
    ) {

        xml.append(
                "  <url>\n"
        );

        xml.append(
                "    <loc>"
        );

        xml.append(
                escapeXml(
                        url
                )
        );

        xml.append(
                "</loc>\n"
        );


        if (
                lastModified != null
        ) {

            xml.append(
                    "    <lastmod>"
            );

            xml.append(
                    lastModified
            );

            xml.append(
                    "</lastmod>\n"
            );
        }


        xml.append(
                "  </url>\n"
        );
    }


    private String normalizarSiteUrl(
            String value
    ) {

        String site =
                value == null
                        ? ""
                        : value.trim();


        while (
                site.endsWith("/")
        ) {

            site =
                    site.substring(
                            0,
                            site.length() - 1
                    );
        }


        return site;
    }


    private String escapeXml(
            String value
    ) {

        return value
                .replace(
                        "&",
                        "&amp;"
                )
                .replace(
                        "<",
                        "&lt;"
                )
                .replace(
                        ">",
                        "&gt;"
                )
                .replace(
                        "\"",
                        "&quot;"
                )
                .replace(
                        "'",
                        "&apos;"
                );
    }
}