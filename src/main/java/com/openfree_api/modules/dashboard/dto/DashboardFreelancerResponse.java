package com.openfree_api.modules.dashboard.dto;

public class DashboardFreelancerResponse {

    private String nomeUsuario;

    private long candidaturasEnviadas;

    private long pendentes;

    private long aceitas;

    private long recusadas;

    private long trabalhosConcluidos;

    private long favoritos;

    private long notificacoesNaoLidas;

    private long conversasAtivas;

    private long mensagensNaoLidas;

    private double avaliacaoMedia;

    private long totalAvaliacoes;



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
    public String getNomeUsuario() {
    return nomeUsuario;
}

public void setNomeUsuario(String nomeUsuario) {
    this.nomeUsuario = nomeUsuario;
}

public long getFavoritos() {
    return favoritos;
}

public void setFavoritos(long favoritos) {
    this.favoritos = favoritos;
}

public long getNotificacoesNaoLidas() {
    return notificacoesNaoLidas;
}

public void setNotificacoesNaoLidas(long notificacoesNaoLidas) {
    this.notificacoesNaoLidas = notificacoesNaoLidas;
}

public long getConversasAtivas() {
    return conversasAtivas;
}

public void setConversasAtivas(long conversasAtivas) {
    this.conversasAtivas = conversasAtivas;
}

public long getMensagensNaoLidas() {
    return mensagensNaoLidas;
}

public void setMensagensNaoLidas(long mensagensNaoLidas) {
    this.mensagensNaoLidas = mensagensNaoLidas;
}

public double getAvaliacaoMedia() {
    return avaliacaoMedia;
}

public void setAvaliacaoMedia(double avaliacaoMedia) {
    this.avaliacaoMedia = avaliacaoMedia;
}

public long getTotalAvaliacoes() {
    return totalAvaliacoes;
}

public void setTotalAvaliacoes(long totalAvaliacoes) {
    this.totalAvaliacoes = totalAvaliacoes;
}


}