package com.openfree_api.modules.profile.storage;

import com.openfree_api.common.exception.BusinessException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class LocalFileStorageService {

    private static final long MAX_IMAGE_SIZE =
            5 * 1024 * 1024L;

    private static final long MAX_PDF_SIZE =
            10 * 1024 * 1024L;

    private static final Set<String> IMAGE_TYPES =
            Set.of(
                    "image/jpeg",
                    "image/png",
                    "image/webp"
            );

    private static final Set<String> IMAGE_EXTENSIONS =
            Set.of(
                    "jpg",
                    "jpeg",
                    "png",
                    "webp"
            );

    private static final Set<String> PDF_TYPES =
            Set.of(
                    "application/pdf"
            );

    private static final Set<String> PDF_EXTENSIONS =
            Set.of("pdf");

    private final Path rootDirectory;

    public LocalFileStorageService(
            @Value("${app.upload.dir:uploads}")
            String uploadDirectory
    ) {
        this.rootDirectory =
                Path.of(uploadDirectory)
                        .toAbsolutePath()
                        .normalize();

        criarDiretorios();
    }

    public String salvarImagem(
            MultipartFile file,
            FileCategory category
    ) {

        if (category == FileCategory.RESUME) {
            throw new BusinessException(
                    "Categoria inválida para arquivo de imagem."
            );
        }

        validarArquivo(
                file,
                IMAGE_TYPES,
                IMAGE_EXTENSIONS,
                MAX_IMAGE_SIZE,
                "Envie uma imagem JPG, PNG ou WEBP de até 5 MB."
        );

        return salvar(file, category);
    }

    public String salvarCurriculo(
            MultipartFile file
    ) {

        validarArquivo(
                file,
                PDF_TYPES,
                PDF_EXTENSIONS,
                MAX_PDF_SIZE,
                "Envie um currículo em PDF de até 10 MB."
        );

        return salvar(
                file,
                FileCategory.RESUME
        );
    }

    public void removerPorUrl(String fileUrl) {

        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        String prefix = "/uploads/";

        if (!fileUrl.startsWith(prefix)) {
            return;
        }

        String relativePath =
                fileUrl.substring(prefix.length());

        Path target =
                rootDirectory
                        .resolve(relativePath)
                        .normalize();

        /*
         * Impede que uma URL malformada saia da pasta uploads.
         */
        if (!target.startsWith(rootDirectory)) {
            return;
        }

        try {
            Files.deleteIfExists(target);
        } catch (IOException exception) {
            throw new BusinessException(
                    "Não foi possível remover o arquivo anterior."
            );
        }
    }

    private String salvar(
            MultipartFile file,
            FileCategory category
    ) {

        String extension =
                extrairExtensao(
                        file.getOriginalFilename()
                );

        String generatedName =
                UUID.randomUUID()
                        + "."
                        + extension;

        Path categoryDirectory =
                rootDirectory.resolve(
                        category.getDirectory()
                );

        Path destination =
                categoryDirectory
                        .resolve(generatedName)
                        .normalize();

        if (!destination.startsWith(categoryDirectory)) {
            throw new BusinessException(
                    "Nome de arquivo inválido."
            );
        }

        try {
            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException exception) {
            throw new BusinessException(
                    "Não foi possível armazenar o arquivo."
            );
        }

        return "/uploads/"
                + category.getDirectory()
                + "/"
                + generatedName;
    }

    private void validarArquivo(
            MultipartFile file,
            Set<String> allowedTypes,
            Set<String> allowedExtensions,
            long maxSize,
            String errorMessage
    ) {

        if (file == null || file.isEmpty()) {
            throw new BusinessException(
                    "O arquivo é obrigatório."
            );
        }

        if (file.getSize() > maxSize) {
            throw new BusinessException(errorMessage);
        }

        String contentType =
                file.getContentType();

        String extension =
                extrairExtensao(
                        file.getOriginalFilename()
                );

        if (contentType == null
                || !allowedTypes.contains(
                        contentType.toLowerCase(
                                Locale.ROOT
                        )
                )
                || !allowedExtensions.contains(extension)) {

            throw new BusinessException(errorMessage);
        }
    }

    private String extrairExtensao(
            String originalFilename
    ) {

        if (originalFilename == null
                || originalFilename.isBlank()) {

            throw new BusinessException(
                    "Nome do arquivo inválido."
            );
        }

        String safeName =
                Path.of(originalFilename)
                        .getFileName()
                        .toString();

        int separator =
                safeName.lastIndexOf('.');

        if (separator < 0
                || separator == safeName.length() - 1) {

            throw new BusinessException(
                    "O arquivo não possui uma extensão válida."
            );
        }

        return safeName
                .substring(separator + 1)
                .toLowerCase(Locale.ROOT);
    }

    private void criarDiretorios() {

        try {
            Files.createDirectories(rootDirectory);

            for (FileCategory category :
                    FileCategory.values()) {

                Files.createDirectories(
                        rootDirectory.resolve(
                                category.getDirectory()
                        )
                );
            }
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Não foi possível criar os diretórios de upload.",
                    exception
            );
        }
    }
}