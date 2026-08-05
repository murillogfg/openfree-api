package com.openfree_api.modules.profile.service;

import com.openfree_api.modules.auth.service.EmpresaAuthService;
import com.openfree_api.modules.auth.service.UsuarioAuthService;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.companies.repository.EmpresaRepository;
import com.openfree_api.modules.profile.dto.CompanyProfileResponse;
import com.openfree_api.modules.profile.dto.FreelancerProfileResponse;
import com.openfree_api.modules.profile.dto.UpdateCompanyProfileRequest;
import com.openfree_api.modules.profile.dto.UpdateFreelancerProfileRequest;
import com.openfree_api.modules.profile.storage.LocalFileStorageService;
import com.openfree_api.modules.reviews.entity.ReviewAuthorType;
import com.openfree_api.modules.reviews.repository.ReviewRepository;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.repository.UsuarioRepository;

import com.openfree_api.modules.profile.storage.FileCategory;
import com.openfree_api.modules.profile.storage.LocalFileStorageService;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final UsuarioAuthService usuarioAuthService;
    private final EmpresaAuthService empresaAuthService;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final ReviewRepository reviewRepository;
    private final LocalFileStorageService fileStorageService;

   public ProfileService(
        UsuarioAuthService usuarioAuthService,
        EmpresaAuthService empresaAuthService,
        UsuarioRepository usuarioRepository,
        EmpresaRepository empresaRepository,
        ReviewRepository reviewRepository,
        LocalFileStorageService fileStorageService
) {
    this.usuarioAuthService = usuarioAuthService;
    this.empresaAuthService = empresaAuthService;
    this.usuarioRepository = usuarioRepository;
    this.empresaRepository = empresaRepository;
    this.reviewRepository = reviewRepository;
    this.fileStorageService = fileStorageService;
}

    @Transactional(readOnly = true)
    public FreelancerProfileResponse buscarPerfilFreelancer(
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(
                        authentication
                );

        return montarPerfilFreelancer(usuario);
    }

    @Transactional
    public FreelancerProfileResponse atualizarPerfilFreelancer(
            UpdateFreelancerProfileRequest request,
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(
                        authentication
                );

        if (request.getNome() != null) {
            usuario.setNome(
                    request.getNome().trim()
            );
        }

        if (request.getTelefone() != null) {
            usuario.setTelefone(
                    limparTexto(request.getTelefone())
            );
        }

        if (request.getTituloProfissional() != null) {
            usuario.setTituloProfissional(
                    limparTexto(
                            request.getTituloProfissional()
                    )
            );
        }

        if (request.getBiografia() != null) {
            usuario.setBiografia(
                    limparTexto(request.getBiografia())
            );
        }

        if (request.getCidade() != null) {
            usuario.setCidade(
                    limparTexto(request.getCidade())
            );
        }

        if (request.getEstado() != null) {
            usuario.setEstado(
                    limparTexto(request.getEstado())
                            .toUpperCase()
            );
        }

        if (request.getHabilidades() != null) {
            usuario.setHabilidades(
                    limparTexto(request.getHabilidades())
            );
        }

        if (request.getPortfolioUrl() != null) {
            usuario.setPortfolioUrl(
                    limparTexto(request.getPortfolioUrl())
            );
        }

        Usuario usuarioAtualizado =
                usuarioRepository.save(usuario);

        return montarPerfilFreelancer(
                usuarioAtualizado
        );
    }

    @Transactional(readOnly = true)
    public CompanyProfileResponse buscarPerfilEmpresa(
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService.getEmpresaLogada(
                        authentication
                );

        return montarPerfilEmpresa(empresa);
    }

    @Transactional
    public CompanyProfileResponse atualizarPerfilEmpresa(
            UpdateCompanyProfileRequest request,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService.getEmpresaLogada(
                        authentication
                );

        if (request.getNomeFantasia() != null) {
            empresa.setNomeFantasia(
                    request.getNomeFantasia().trim()
            );
        }

        if (request.getTelefone() != null) {
            empresa.setTelefone(
                    limparTexto(request.getTelefone())
            );
        }

        if (request.getDescricao() != null) {
            empresa.setDescricao(
                    limparTexto(request.getDescricao())
            );
        }

        if (request.getCidade() != null) {
            empresa.setCidade(
                    limparTexto(request.getCidade())
            );
        }

        if (request.getEstado() != null) {
            empresa.setEstado(
                    limparTexto(request.getEstado())
                            .toUpperCase()
            );
        }

        if (request.getSite() != null) {
            empresa.setSite(
                    limparTexto(request.getSite())
            );
        }

        Empresa empresaAtualizada =
                empresaRepository.save(empresa);

        return montarPerfilEmpresa(
                empresaAtualizada
        );
    }

    @Transactional
public FreelancerProfileResponse atualizarAvatar(
        MultipartFile file,
        Authentication authentication
) {

    Usuario usuario =
            usuarioAuthService.getUsuarioLogado(
                    authentication
            );

    String urlAnterior =
            usuario.getAvatarUrl();

    String novaUrl =
            fileStorageService.salvarImagem(
                    file,
                    FileCategory.AVATAR
            );

    usuario.setAvatarUrl(novaUrl);

    Usuario atualizado =
            usuarioRepository.save(usuario);

    fileStorageService.removerPorUrl(
            urlAnterior
    );

    return montarPerfilFreelancer(atualizado);
}

@Transactional
public FreelancerProfileResponse atualizarCurriculo(
        MultipartFile file,
        Authentication authentication
) {

    Usuario usuario =
            usuarioAuthService.getUsuarioLogado(
                    authentication
            );

    String urlAnterior =
            usuario.getCurriculoUrl();

    String novaUrl =
            fileStorageService.salvarCurriculo(
                    file
            );

    usuario.setCurriculoUrl(novaUrl);

    Usuario atualizado =
            usuarioRepository.save(usuario);

    fileStorageService.removerPorUrl(
            urlAnterior
    );

    return montarPerfilFreelancer(atualizado);
}

@Transactional
public CompanyProfileResponse atualizarLogo(
        MultipartFile file,
        Authentication authentication
) {

    Empresa empresa =
            empresaAuthService.getEmpresaLogada(
                    authentication
            );

    String urlAnterior =
            empresa.getLogo();

    String novaUrl =
            fileStorageService.salvarImagem(
                    file,
                    FileCategory.LOGO
            );

    empresa.setLogo(novaUrl);

    Empresa atualizada =
            empresaRepository.save(empresa);

    fileStorageService.removerPorUrl(
            urlAnterior
    );

    return montarPerfilEmpresa(atualizada);
}

    private FreelancerProfileResponse montarPerfilFreelancer(
            Usuario usuario
    ) {

        Long usuarioId = usuario.getId();

        Double mediaCalculada =
                reviewRepository.calcularMediaDoUsuario(
                        usuarioId,
                        ReviewAuthorType.EMPRESA
                );

        long totalAvaliacoes =
                reviewRepository
                        .countByUsuarioAvaliadoIdAndTipoAutor(
                                usuarioId,
                                ReviewAuthorType.EMPRESA
                        );

        FreelancerProfileResponse response =
                new FreelancerProfileResponse();

        response.setId(usuario.getId());
        response.setNome(usuario.getNome());
        response.setEmail(usuario.getEmail());
        response.setTelefone(usuario.getTelefone());
        response.setTituloProfissional(
                usuario.getTituloProfissional()
        );
        response.setBiografia(usuario.getBiografia());
        response.setCidade(usuario.getCidade());
        response.setEstado(usuario.getEstado());
        response.setHabilidades(
                usuario.getHabilidades()
        );
        response.setAvatarUrl(usuario.getAvatarUrl());
        response.setCurriculoUrl(
                usuario.getCurriculoUrl()
        );
        response.setPortfolioUrl(
                usuario.getPortfolioUrl()
        );
        response.setAvaliacaoMedia(
                mediaCalculada != null
                        ? mediaCalculada
                        : 0.0
        );
        response.setTotalAvaliacoes(
                totalAvaliacoes
        );

        return response;
    }

    private CompanyProfileResponse montarPerfilEmpresa(
            Empresa empresa
    ) {

        Long empresaId = empresa.getId();

        Double mediaCalculada =
                reviewRepository.calcularMediaDaEmpresa(
                        empresaId,
                        ReviewAuthorType.FREELANCER
                );

        long totalAvaliacoes =
                reviewRepository
                        .countByEmpresaAvaliadaIdAndTipoAutor(
                                empresaId,
                                ReviewAuthorType.FREELANCER
                        );

        CompanyProfileResponse response =
                new CompanyProfileResponse();

        response.setId(empresa.getId());
        response.setRazaoSocial(
                empresa.getRazaoSocial()
        );
        response.setNomeFantasia(
                empresa.getNomeFantasia()
        );
        response.setCnpj(empresa.getCnpj());
        response.setEmail(empresa.getEmail());
        response.setTelefone(empresa.getTelefone());
        response.setDescricao(empresa.getDescricao());
        response.setCidade(empresa.getCidade());
        response.setEstado(empresa.getEstado());
        response.setSite(empresa.getSite());
        response.setLogo(empresa.getLogo());
        response.setVerificada(
                empresa.getVerificada()
        );
        response.setAtiva(empresa.getAtiva());
        response.setAvaliacaoMedia(
                mediaCalculada != null
                        ? mediaCalculada
                        : 0.0
        );
        response.setTotalAvaliacoes(
                totalAvaliacoes
        );

        return response;
    }

    private String limparTexto(String valor) {

        String texto = valor.trim();

        return texto.isBlank()
                ? null
                : texto;
    }
}