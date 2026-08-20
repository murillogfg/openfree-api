package com.openfree_api.modules.jobs.service;

import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.entity.Vaga;
import com.openfree_api.modules.jobs.repository.VagaRepository;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
public class SitemapService {

    private final VagaRepository vagaRepository;


    /*
     * Enquanto a OpenFree estiver usando
     * o domínio da Vercel, ele será o domínio
     * das URLs canônicas do sitemap.
     *
     * Quando houver domínio próprio, podemos
     * configurar APP_FRONTEND_URL no Railway
     * sem alterar o código.
     */
    @Value(
            "${app.frontend.url:https://openfree-front.vercel.app}"
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

        List<Vaga> vagas =
                vagaRepository.findByStatus(
                        StatusVaga.PUBLICADA
                );


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


        /*
         * Página pública de listagem.
         */
        adicionarUrl(
                xml,
                site + "/jobs",
                null
        );


        /*
         * Uma entrada para cada vaga que está
         * realmente PUBLICADA.
         */
        for (
                Vaga vaga
                : vagas
        ) {

            LocalDate ultimaAlteracao =
                    null;


            if (
                    vaga.getUpdatedAt()
                            != null
            ) {

                ultimaAlteracao =
                        vaga.getUpdatedAt()
                                .toLocalDate();
            }


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


        /*
         * lastmod só é enviado quando temos uma
         * data real da última alteração da vaga.
         */
        if (
                lastModified
                        != null
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