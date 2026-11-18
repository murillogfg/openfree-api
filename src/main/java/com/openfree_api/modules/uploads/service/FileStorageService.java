package com.openfree_api.modules.uploads.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.uploads.dto.UploadResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final long MAX_IMAGE_SIZE =
            5 * 1024 * 1024;

    private static final long MAX_PDF_SIZE =
            10 * 1024 * 1024;

    private static final Set<String> IMAGE_TYPES =
            Set.of(
                    "image/jpeg",
                    "image/png",
                    "image/webp"
            );

    private static final String PDF_TYPE =
            "application/pdf";

    private final Path root =
            Paths.get("uploads")
                    .toAbsolutePath()
                    .normalize();

    public FileStorageService() {

        try {
            Files.createDirectories(root);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Não foi possível criar o diretório de uploads.",
                    exception
            );
        }
    }

    public UploadResponse salvarImagem(
            MultipartFile file,
            String pasta
    ) {
        validarArquivo(file);

        if (!IMAGE_TYPES.contains(file.getContentType())) {
            throw new BusinessException(
                    "Formato de imagem não permitido. Use JPG, PNG ou WEBP."
            );
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BusinessException(
                    "A imagem deve possuir no máximo 5 MB."
            );
        }

        return salvar(file, pasta);
    }

    public UploadResponse salvarPdf(
            MultipartFile file,
            String pasta
    ) {
        validarArquivo(file);

        if (!PDF_TYPE.equals(file.getContentType())) {
            throw new BusinessException(
                    "O currículo deve estar no formato PDF."
            );
        }

        if (file.getSize() > MAX_PDF_SIZE) {
            throw new BusinessException(
                    "O PDF deve possuir no máximo 10 MB."
            );
        }

        return salvar(file, pasta);
    }

    private void validarArquivo(
            MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(
                    "Nenhum arquivo foi enviado."
            );
        }
    }

    private UploadResponse salvar(
            MultipartFile file,
            String pasta
    ) {

        try {
            Path diretorio =
                    root.resolve(pasta)
                            .normalize();

            if (!diretorio.startsWith(root)) {
                throw new BusinessException(
                        "Diretório de upload inválido."
                );
            }

            Files.createDirectories(diretorio);

            String extensao =
                    obterExtensao(
                            file.getOriginalFilename()
                    );

            String nome =
                    UUID.randomUUID()
                            + extensao;

            Path destino =
                    diretorio.resolve(nome)
                            .normalize();

            if (!destino.startsWith(diretorio)) {
                throw new BusinessException(
                        "Caminho de arquivo inválido."
                );
            }

            Files.copy(
                    file.getInputStream(),
                    destino,
                    StandardCopyOption.REPLACE_EXISTING
            );

            String url =
                    "/uploads/"
                    + pasta
                    + "/"
                    + nome;

            return new UploadResponse(
                    nome,
                    url,
                    file.getContentType(),
                    file.getSize()
            );

        } catch (IOException exception) {
            throw new BusinessException(
                    "Não foi possível salvar o arquivo."
            );
        }
    }

    private String obterExtensao(
            String nomeOriginal
    ) {

        if (
                nomeOriginal == null
                || !nomeOriginal.contains(".")
        ) {
            return "";
        }

        String extensao =
                nomeOriginal.substring(
                        nomeOriginal.lastIndexOf(".")
                ).toLowerCase();

        if (extensao.length() > 10) {
            throw new BusinessException(
                    "Extensão de arquivo inválida."
            );
        }

        return extensao;
    }
}