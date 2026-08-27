package com.example.chatdesktop.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RagService {

    // caminho confirmado via "Copy Reference" no IntelliJ:
    // o arquivo fica direto em resources/knowledge/, não em resources/com/.../knowledge/
    private static final String ARQUIVO =
            "/knowledge/40+40= naverdathi.txt";

    /**
     * Compara a pergunta linha por linha com o arquivo e retorna
     * SÓ as linhas que têm palavra em comum com a pergunta — não o
     * arquivo inteiro, mesmo que ele tenha várias "piadas" diferentes.
     */
    public String buscarContexto(String pergunta) {

        String documento = carregarDocumento();

        if (documento == null || documento.isBlank()) {
            return "";
        }

        Set<String> palavrasPergunta = extrairPalavras(pergunta);

        return Arrays.stream(documento.split("\n"))
                .map(String::trim)
                .filter(linha -> !linha.isBlank())
                .filter(linha -> extrairPalavras(linha).stream()
                        .anyMatch(palavrasPergunta::contains))
                .collect(Collectors.joining("\n"));
    }

    private String carregarDocumento() {

        try (InputStream inputStream =
                     RagService.class.getResourceAsStream(ARQUIVO)) {

            if (inputStream == null) {
                System.err.println(
                        "⚠️ Arquivo RAG não encontrado: " + ARQUIVO
                );
                return "";
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            inputStream,
                            StandardCharsets.UTF_8
                    )
            )) {

                return reader.lines()
                        .collect(Collectors.joining("\n"));
            }

        } catch (Exception e) {

            System.err.println(
                    "❌ Erro ao carregar documento RAG: "
                            + e.getMessage()
            );

            return "";
        }
    }

    private Set<String> extrairPalavras(String texto) {

        return new HashSet<>(
                Arrays.stream(
                                texto
                                        .toLowerCase()
                                        .replaceAll("[^\\p{L}\\p{N}]+", " ")
                                        .trim()
                                        .split("\\s+")
                        )
                        .filter(palavra -> !palavra.isBlank())
                        .collect(Collectors.toSet())
        );
    }
}