package com.openfree_api.modules.companies.dto;

public class DashboardEmpresaResponse {

    private long vagasPublicadas;
    private long vagasFinalizadas;
    private long vagasAbertas;
    private long candidaturasRecebidas;
    private long profissionaisContratados;

    public long getVagasPublicadas() {
        return vagasPublicadas;
    }

    public void setVagasPublicadas(long vagasPublicadas) {
        this.vagasPublicadas = vagasPublicadas;
    }

    public long getVagasFinalizadas() {
        return vagasFinalizadas;
    }

    public void setVagasFinalizadas(long vagasFinalizadas) {
        this.vagasFinalizadas = vagasFinalizadas;
    }

    public long getVagasAbertas() {
        return vagasAbertas;
    }

    public void setVagasAbertas(long vagasAbertas) {
        this.vagasAbertas = vagasAbertas;
    }

    public long getCandidaturasRecebidas() {
        return candidaturasRecebidas;
    }

    public void setCandidaturasRecebidas(long candidaturasRecebidas) {
        this.candidaturasRecebidas = candidaturasRecebidas;
    }

    public long getProfissionaisContratados() {
        return profissionaisContratados;
    }

    public void setProfissionaisContratados(long profissionaisContratados) {
        this.profissionaisContratados = profissionaisContratados;
    }
}