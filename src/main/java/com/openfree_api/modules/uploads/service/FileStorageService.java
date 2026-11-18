package com.openfree_api.modules.uploads.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.uploads.dto.UploadResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
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

    /*
     * A extensão salva vem do TIPO validado, nunca do
     * nome enviado pelo usuário. Assim ninguém consegue
     * publicar um ".html" se passando por imagem.
     */
    private static final Map<String, String> EXTENSAO_POR_TIPO =
            Map.of(
                    "image/jpeg", ".jpg",
                    "image/png", ".png",
                    "image/webp", ".webp",
                    "application/pdf", ".pdf"
            );

    /*
     * Mesma pasta usada pelo restante da API
     * (app.upload.dir / variável UPLOAD_DIR).
     * Em produção aponta para o Volume do Railway.
     */
    private final Path root;

    public FileStorageService(
            @Value("${app.upload.dir:uploads}")
            String uploadDirectory
    ) {

        this.root =
                Paths.get(uploadDirectory)
                        .toAbsolutePath()
                        .normalize();

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
                    extensaoPorTipo(
                            file.getContentType()
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

    private String extensaoPorTipo(
            String contentType
    ) {

        String extensao =
                contentType == null
                        ? null
                        : EXTENSAO_POR_TIPO.get(
                                contentType.toLowerCase()
                        );

        if (extensao == null) {
            throw new BusinessException(
                    "Tipo de arquivo não permitido."
            );
        }

        return extensao;
    }
}