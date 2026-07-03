package com.openfree_api.controller;

import com.openfree_api.model.Vaga;
import com.openfree_api.repository.VagaRepository;
import org.springframework.web.bind.annotation.*;

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