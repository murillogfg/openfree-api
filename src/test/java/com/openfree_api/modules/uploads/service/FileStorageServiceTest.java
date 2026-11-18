package com.openfree_api.modules.uploads.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.uploads.dto.UploadResponse;

class FileStorageServiceTest {

    @TempDir
    Path pastaUploads;

    @Test
    void gravaNaPastaConfiguradaEnaoNaPastaFixa() {

        FileStorageService service =
                new FileStorageService(pastaUploads.toString());

        UploadResponse resposta = service.salvarImagem(
                new MockMultipartFile("file", "foto.png", "image/png", new byte[] {1, 2, 3}),
                "avatars"
        );

        Path salvo = pastaUploads.resolve("avatars").resolve(resposta.getFileName());

        assertTrue(Files.exists(salvo));
        assertTrue(resposta.getUrl().startsWith("/uploads/avatars/"));
    }

    @Test
    void extensaoVemDoTipoENaoDoNomeEnviado() {

        FileStorageService service =
                new FileStorageService(pastaUploads.toString());

        UploadResponse resposta = service.salvarImagem(
                new MockMultipartFile("file", "pagina.html", "image/png", new byte[] {1}),
                "avatars"
        );

        assertTrue(resposta.getFileName().endsWith(".png"));
    }

    @Test
    void pdfRecebeExtensaoPdf() {

        FileStorageService service =
                new FileStorageService(pastaUploads.toString());

        UploadResponse resposta = service.salvarPdf(
                new MockMultipartFile("file", "curriculo", "application/pdf", new byte[] {1}),
                "curriculos"
        );

        assertTrue(resposta.getFileName().endsWith(".pdf"));
    }

    @Test
    void recusaTipoNaoPermitido() {

        FileStorageService service =
                new FileStorageService(pastaUploads.toString());

        BusinessException erro = assertThrows(
                BusinessException.class,
                () -> service.salvarImagem(
                        new MockMultipartFile("file", "x.html", "text/html", new byte[] {1}),
                        "avatars"
                )
        );

        assertEquals(
                "Formato de imagem não permitido. Use JPG, PNG ou WEBP.",
                erro.getMessage()
        );
    }
}
