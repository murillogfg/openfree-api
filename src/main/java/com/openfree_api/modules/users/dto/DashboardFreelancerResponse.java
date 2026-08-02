package com.openfree_api.modules.users.dto;

public class DashboardFreelancerResponse {

    private long candidaturasEnviadas;
    private long pendentes;
    private long aceitas;
    private long recusadas;
    private long trabalhosConcluidos;

    public DashboardFreelancerResponse() {
    }

    public long getCandidaturasEnviadas() {
        return candidaturasEnviadas;
    }

    public void setCandidaturasEnviadas(long candidaturasEnviadas) {
        this.candidaturasEnviadas = candidaturasEnviadas;
    }

    public long getPendentes() {
        return pendentes;
    }

    public void setPendentes(long pendentes) {
        this.pendentes = pendentes;
    }

    public long getAceitas() {
        return aceitas;
    }

    public void setAceitas(long aceitas) {
        this.aceitas = aceitas;
    }

    public long getRecusadas() {
        return recusadas;
    }

    public void setRecusadas(long recusadas) {
        this.recusadas = recusadas;
    }

    public long getTrabalhosConcluidos() {
        return trabalhosConcluidos;
    }

    public void setTrabalhosConcluidos(long trabalhosConcluidos) {
        this.trabalhosConcluidos = trabalhosConcluidos;
    }
}