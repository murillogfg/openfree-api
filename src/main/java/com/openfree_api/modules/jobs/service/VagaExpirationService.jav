package com.openfree_api.modules.jobs.service;

import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.entity.Vaga;
import com.openfree_api.modules.jobs.repository.VagaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;


@Service
public class VagaExpirationService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    VagaExpirationService.class
            );

    private static final ZoneId ZONA_NEGOCIO =
            ZoneId.of(
                    "America/Sao_Paulo"
            );

    private final VagaRepository vagaRepository;


    public VagaExpirationService(
            VagaRepository vagaRepository
    ) {
        this.vagaRepository =
                vagaRepository;
    }


    @EventListener(
            ApplicationReadyEvent.class
    )
    @Transactional
    public void expirarAoIniciar() {

        int quantidade =
                expirarVagasVencidas();

        if (
                quantidade > 0
        ) {
            log.info(
                    "{} vaga(s) expirada(s) durante a inicialização.",
                    quantidade
            );
        }
    }


    @Scheduled(
            cron = "0 10 0 * * *",
            zone = "America/Sao_Paulo"
    )
    @Transactional
    public void expirarDiariamente() {

        int quantidade =
                expirarVagasVencidas();

        if (
                quantidade > 0
        ) {
            log.info(
                    "{} vaga(s) expirada(s) pelo agendador diário.",
                    quantidade
            );
        }
    }


    public int expirarVagasVencidas() {

        LocalDate hoje =
                LocalDate.now(
                        ZONA_NEGOCIO
                );

        List<Vaga> publicadas =
                vagaRepository
                        .findByStatus(
                                StatusVaga.PUBLICADA
                        );

        List<Vaga> expiradas =
                publicadas
                        .stream()
                        .filter(
                                vaga ->
                                        vaga.getDataServico()
                                                != null
                        )
                        .filter(
                                vaga ->
                                        vaga.getDataServico()
                                                .isBefore(
                                                        hoje
                                                )
                        )
                        .toList();

        if (
                expiradas.isEmpty()
        ) {
            return 0;
        }

        expiradas.forEach(
                vaga ->
                        vaga.setStatus(
                                StatusVaga.EXPIRADA
                        )
        );

        vagaRepository
                .saveAll(
                        expiradas
                );

        log.info(
                "{} vaga(s) vencida(s) marcada(s) como EXPIRADA.",
                expiradas.size()
        );

        return expiradas.size();
    }
}