package com.openfree_api.modules.companies.dto;

public class CompanyDashboardResponse {

    private long vagasPublicadas;

    private long vagasAbertas;

    private long vagasFinalizadas;

    private long candidaturasRecebidas;

    private long candidaturasPendentes;

    private long candidaturasAceitas;

    private long candidaturasRecusadas;

    public CompanyDashboardResponse() {
    }

    public long getVagasPublicadas() {
        return vagasPublicadas;
    }

    public void setVagasPublicadas(long vagasPublicadas) {
        this.vagasPublicadas = vagasPublicadas;
    }

    public long getVagasAbertas() {
        return vagasAbertas;
    }

    public void setVagasAbertas(long vagasAbertas) {
        this.vagasAbertas = vagasAbertas;
    }

    public long getVagasFinalizadas() {
        return vagasFinalizadas;
    }

    public void setVagasFinalizadas(long vagasFinalizadas) {
        this.vagasFinalizadas = vagasFinalizadas;
    }

    public long getCandidaturasRecebidas() {
        return candidaturasRecebidas;
    }

    public void setCandidaturasRecebidas(long candidaturasRecebidas) {
        this.candidaturasRecebidas = candidaturasRecebidas;
    }

    public long getCandidaturasPendentes() {
        return candidaturasPendentes;
    }

    public void setCandidaturasPendentes(long candidaturasPendentes) {
        this.candidaturasPendentes = candidaturasPendentes;
    }

    public long getCandidaturasAceitas() {
        return candidaturasAceitas;
    }

    public void setCandidaturasAceitas(long candidaturasAceitas) {
        this.candidaturasAceitas = candidaturasAceitas;
    }

    public long getCandidaturasRecusadas() {
        return candidaturasRecusadas;
    }

    public void setCandidaturasRecusadas(long candidaturasRecusadas) {
        this.candidaturasRecusadas = candidaturasRecusadas;
    }
}