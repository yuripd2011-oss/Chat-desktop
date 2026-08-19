package com.example.chatdesktop.service;

import com.example.chatdesktop.config.ConfigLoader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Encapsula toda a comunicação com a API da Groq.
 */
public class GroqService {

    private static final String GROQ_URL =
            "https://api.groq.com/openai/v1/chat/completions";

    private static final String MODELO = "openai/gpt-oss-20b";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String apiKey;

    public GroqService() {
        this.apiKey = ConfigLoader.carregarApiKey();
    }

    public boolean chaveConfigurada() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String perguntar(String mensagem) {

        if (!chaveConfigurada()) {
            return """
                    ⚠️ Chave da Groq não configurada.

                    Crie um arquivo chamado "config.properties"
                    na raiz do projeto (mesma pasta do pom.xml)
                    com o seguinte conteúdo:

                    groq.api.key=gsk_sua_chave_real_aqui
                    """;
        }

        try {

            String json = """
                    {
                      "model": "%s",
                      "messages": [
                        {
                          "role": "system",
                          "content": "Você é um assistente útil, inteligente e amigável. Responda em português do Brasil."
                        },
                        {
                          "role": "user",
                          "content": %s
                        }
                      ],
                      "temperature": 0.7,
                      "max_completion_tokens": 2048
                    }
                    """.formatted(
                    MODELO,
                    objectMapper.writeValueAsString(mensagem)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GROQ_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            if (response.statusCode() != 200) {
                return "❌ Erro da Groq.\n\n" +
                        "Código HTTP: " + response.statusCode() +
                        "\n\n" + response.body();
            }

            JsonNode raiz = objectMapper.readTree(response.body());
            JsonNode choices = raiz.path("choices");

            if (choices.isEmpty()) {
                return "❌ A Groq não retornou uma resposta.";
            }

            return choices
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (Exception e) {

            return """
                    ❌ Não foi possível conectar à Groq.

                    Verifique:

                    • Sua conexão com a Internet
                    • Sua GROQ_API_KEY
                    • Se a API da Groq está disponível

                    Erro:

                    %s
                    """.formatted(e.getMessage());
        }
    }
}