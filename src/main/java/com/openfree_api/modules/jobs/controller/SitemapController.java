package com.openfree_api.modules.jobs.controller;

import com.openfree_api.modules.jobs.service.SitemapService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/jobs")
public class SitemapController {

    private final SitemapService sitemapService;


    public SitemapController(
            SitemapService sitemapService
    ) {

        this.sitemapService =
                sitemapService;
    }


    /*
     * Endpoint interno que será exposto no
     * frontend como:
     *
     * https://openfree-front.vercel.app/sitemap.xml
     *
     * através de um rewrite da Vercel.
     *
     * Mantemos o endpoint sob /jobs porque
     * GET /jobs/* já é uma rota pública no
     * SecurityConfig.
     */
    @GetMapping(
            value = "/sitemap.xml",
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<String>
    sitemap() {

        return ResponseEntity
                .ok()
                .contentType(
                        MediaType.APPLICATION_XML
                )
                .body(
                        sitemapService
                                .gerarSitemap()
                );
    }
}