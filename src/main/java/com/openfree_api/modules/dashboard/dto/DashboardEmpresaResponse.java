package com.openfree_api.modules.dashboard.dto;

public class DashboardEmpresaResponse {

    private long vagasPublicadas;
    private long vagasFinalizadas;
    private long vagasAbertas;
    private long candidaturasRecebidas;
    private long profissionaisContratados;
    private String nomeEmpresa;

private long candidaturasPendentes;

private long conversasAtivas;

private long mensagensNaoLidas;

private double avaliacaoMedia;

private long totalAvaliacoes;

private double taxaContratacao;




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
    public String getNomeEmpresa() {
    return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public long getCandidaturasPendentes() {
        return candidaturasPendentes;
    }

    public void setCandidaturasPendentes(long candidaturasPendentes) {
        this.candidaturasPendentes = candidaturasPendentes;
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

    public double getTaxaContratacao() {
        return taxaContratacao;
    }

    public void setTaxaContratacao(double taxaContratacao) {
        this.taxaContratacao = taxaContratacao;
    }


}