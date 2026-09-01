package com.example.chatdesktop.service;

import com.example.chatdesktop.model.ConversaSalva;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Salva e carrega o histórico de conversas em um arquivo JSON local,
 * dentro da pasta do usuário (não depende de onde o projeto está).
 */
public class HistoricoService {

    private static final Path ARQUIVO = Paths.get(
            System.getProperty("user.home"), ".neochat", "historico.json"
    );

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<ConversaSalva> carregar() {

        try {
            if (!Files.exists(ARQUIVO)) {
                return new ArrayList<>();
            }

            return objectMapper.readValue(
                    ARQUIVO.toFile(),
                    new TypeReference<List<ConversaSalva>>() {}
            );

        } catch (IOException e) {
            System.err.println("Erro ao carregar histórico salvo: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void salvar(List<ConversaSalva> conversas) {

        try {
            File pasta = ARQUIVO.getParent().toFile();
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(ARQUIVO.toFile(), conversas);

        } catch (IOException e) {
            System.err.println("Erro ao salvar histórico: " + e.getMessage());
        }
    }
}