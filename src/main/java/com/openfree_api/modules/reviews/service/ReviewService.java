package com.openfree_api.modules.reviews.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.auth.service.EmpresaAuthService;
import com.openfree_api.modules.auth.service.UsuarioAuthService;
import com.openfree_api.modules.candidaturas.entity.Candidatura;
import com.openfree_api.modules.candidaturas.entity.StatusCandidatura;
import com.openfree_api.modules.candidaturas.repository.CandidaturaRepository;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.reviews.dto.CreateReviewRequest;
import com.openfree_api.modules.reviews.dto.RatingSummaryResponse;
import com.openfree_api.modules.reviews.dto.ReviewResponse;
import com.openfree_api.modules.reviews.entity.Review;
import com.openfree_api.modules.reviews.entity.ReviewAuthorType;
import com.openfree_api.modules.reviews.mapper.ReviewMapper;
import com.openfree_api.modules.reviews.repository.ReviewRepository;
import com.openfree_api.modules.users.entity.Usuario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private static final Logger log =
            LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final CandidaturaRepository candidaturaRepository;
    private final ReviewMapper reviewMapper;
    private final EmpresaAuthService empresaAuthService;
    private final UsuarioAuthService usuarioAuthService;

    public ReviewService(
            ReviewRepository reviewRepository,
            CandidaturaRepository candidaturaRepository,
            ReviewMapper reviewMapper,
            EmpresaAuthService empresaAuthService,
            UsuarioAuthService usuarioAuthService
    ) {
        this.reviewRepository = reviewRepository;
        this.candidaturaRepository = candidaturaRepository;
        this.reviewMapper = reviewMapper;
        this.empresaAuthService = empresaAuthService;
        this.usuarioAuthService = usuarioAuthService;
    }

    /*
     * Empresa avalia o freelancer contratado.
     */
    @Transactional
    public ReviewResponse avaliarFreelancer(
            Long candidaturaId,
            CreateReviewRequest request,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService.getEmpresaLogada(
                        authentication
                );

        Candidatura candidatura =
                candidaturaRepository
                        .findByIdAndVagaEmpresaId(
                                candidaturaId,
                                empresa.getId()
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Candidatura não encontrada ou não pertence à empresa autenticada."
                                )
                        );

        validarCandidaturaAceita(
                candidatura
        );

        validarAvaliacaoDuplicada(
                candidaturaId,
                ReviewAuthorType.EMPRESA
        );

        Review review =
                criarReviewBase(
                        candidatura,
                        request
                );

        review.setTipoAutor(
                ReviewAuthorType.EMPRESA
        );

        review.setUsuarioAvaliado(
                candidatura.getUsuario()
        );

        review.setEmpresaAvaliada(
                empresa
        );

        Review reviewSalva =
                reviewRepository.save(review);

        log.info(
                "Empresa '{}' avaliou o freelancer '{}'. candidaturaId={}, reviewId={}, nota={}",
                empresa.getNomeFantasia(),
                candidatura.getUsuario().getEmail(),
                candidaturaId,
                reviewSalva.getId(),
                reviewSalva.getNota()
        );

        return reviewMapper.toResponse(
                reviewSalva
        );
    }

    /*
     * Freelancer avalia a empresa responsável pela vaga.
     */
    @Transactional
    public ReviewResponse avaliarEmpresa(
            Long candidaturaId,
            CreateReviewRequest request,
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(
                        authentication
                );

        Candidatura candidatura =
                candidaturaRepository
                        .findById(candidaturaId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Candidatura não encontrada."
                                )
                        );

        if (!candidatura.getUsuario()
                .getId()
                .equals(usuario.getId())) {

            log.warn(
                    "Usuário '{}' tentou avaliar empresa por candidatura de outro usuário. candidaturaId={}",
                    usuario.getEmail(),
                    candidaturaId
            );

            throw new BusinessException(
                    "Esta candidatura não pertence ao usuário autenticado."
            );
        }

        validarCandidaturaAceita(
                candidatura
        );

        validarAvaliacaoDuplicada(
                candidaturaId,
                ReviewAuthorType.FREELANCER
        );

        Empresa empresa =
                candidatura
                        .getVaga()
                        .getEmpresa();

        Review review =
                criarReviewBase(
                        candidatura,
                        request
                );

        review.setTipoAutor(
                ReviewAuthorType.FREELANCER
        );

        /*
         * Mantemos o freelancer relacionado ao registro
         * para preservar o contexto da contratação.
         */
        review.setUsuarioAvaliado(
                usuario
        );

        review.setEmpresaAvaliada(
                empresa
        );

        Review reviewSalva =
                reviewRepository.save(review);

        log.info(
                "Freelancer '{}' avaliou a empresa '{}'. candidaturaId={}, reviewId={}, nota={}",
                usuario.getEmail(),
                empresa.getNomeFantasia(),
                candidaturaId,
                reviewSalva.getId(),
                reviewSalva.getNota()
        );

        return reviewMapper.toResponse(
                reviewSalva
        );
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> listarAvaliacoesDoUsuario(
            Long usuarioId
    ) {

        return reviewRepository
                .findByUsuarioAvaliadoIdAndTipoAutorOrderByCreatedAtDesc(
                        usuarioId,
                        ReviewAuthorType.EMPRESA
                )
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> listarAvaliacoesDaEmpresa(
            Long empresaId
    ) {

        return reviewRepository
                .findByEmpresaAvaliadaIdAndTipoAutorOrderByCreatedAtDesc(
                        empresaId,
                        ReviewAuthorType.FREELANCER
                )
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RatingSummaryResponse resumoDoUsuario(
            Long usuarioId
    ) {

        Double media =
                reviewRepository.calcularMediaDoUsuario(
                        usuarioId,
                        ReviewAuthorType.EMPRESA
                );

        long total =
                reviewRepository
                        .countByUsuarioAvaliadoIdAndTipoAutor(
                                usuarioId,
                                ReviewAuthorType.EMPRESA
                        );

        return new RatingSummaryResponse(
                media != null ? media : 0.0,
                total
        );
    }

    @Transactional(readOnly = true)
    public RatingSummaryResponse resumoDaEmpresa(
            Long empresaId
    ) {

        Double media =
                reviewRepository.calcularMediaDaEmpresa(
                        empresaId,
                        ReviewAuthorType.FREELANCER
                );

        long total =
                reviewRepository
                        .countByEmpresaAvaliadaIdAndTipoAutor(
                                empresaId,
                                ReviewAuthorType.FREELANCER
                        );

        return new RatingSummaryResponse(
                media != null ? media : 0.0,
                total
        );
    }

    private Review criarReviewBase(
            Candidatura candidatura,
            CreateReviewRequest request
    ) {

        Review review =
                new Review();

        review.setCandidatura(
                candidatura
        );

        review.setNota(
                request.getNota()
        );

        if (request.getComentario() != null) {

            String comentario =
                    request.getComentario().trim();

            review.setComentario(
                    comentario.isBlank()
                            ? null
                            : comentario
            );
        }

        return review;
    }

    private void validarCandidaturaAceita(
            Candidatura candidatura
    ) {

        if (candidatura.getStatus()
                != StatusCandidatura.ACEITA) {

            throw new BusinessException(
                    "Somente candidaturas aceitas podem receber avaliações."
            );
        }
    }

    private void validarAvaliacaoDuplicada(
            Long candidaturaId,
            ReviewAuthorType tipoAutor
    ) {

        if (reviewRepository
                .existsByCandidaturaIdAndTipoAutor(
                        candidaturaId,
                        tipoAutor
                )) {

            throw new BusinessException(
                    "Você já realizou uma avaliação para esta contratação."
            );
        }
    }
}