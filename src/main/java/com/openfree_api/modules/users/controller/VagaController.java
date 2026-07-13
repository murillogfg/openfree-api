package com.openfree_api.modules.users.controller;

import org.springframework.web.bind.annotation.*;

import com.openfree_api.modules.users.model.Vaga;
import com.openfree_api.modules.users.repository.VagaRepository;

import java.util.List;

@RestController
@RequestMapping("/vagas")
public class VagaController {

    private final VagaRepository vagaRepository;

    public VagaController(VagaRepository vagaRepository) {
        this.vagaRepository = vagaRepository;
    }

    @GetMapping
    public List<Vaga> listarVagas() {
        return vagaRepository.findAll();
    }

    @PostMapping
    public Vaga criarVaga(@RequestBody Vaga vaga) {
        return vagaRepository.save(vaga);
    }
}