package com.openfree_api.modules.reviews.dto;

public class RatingSummaryResponse {

    private Double media;
    private Long totalAvaliacoes;

    public RatingSummaryResponse() {
    }

    public RatingSummaryResponse(
            Double media,
            Long totalAvaliacoes
    ) {
        this.media = media;
        this.totalAvaliacoes = totalAvaliacoes;
    }

    public Double getMedia() {
        return media;
    }

    public void setMedia(Double media) {
        this.media = media;
    }

    public Long getTotalAvaliacoes() {
        return totalAvaliacoes;
    }

    public void setTotalAvaliacoes(Long totalAvaliacoes) {
        this.totalAvaliacoes = totalAvaliacoes;
    }
}