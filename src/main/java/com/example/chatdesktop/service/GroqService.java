package com.example.chatdesktop.service;

import com.example.chatdesktop.config.ConfigLoader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Encapsula toda a comunicação com a API da Groq, com mensagens de
 * erro amigáveis para os problemas mais comuns (sem internet, chave
 * inválida, limite de uso, falha de comunicação).
 */
public class GroqService {

    private static final String GROQ_URL =
            "https://api.groq.com/openai/v1/chat/completions";

    private static final String MODELO = "openai/gpt-oss-20b";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

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
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            return tratarResposta(response);

        } catch (UnknownHostException | ConnectException e) {

            return """
                    📡 Sem conexão com a internet.

                    Verifique sua rede e tente novamente.
                    """;

        } catch (HttpTimeoutException e) {

            return """
                    ⏱️ A Groq demorou demais para responder.

                    Tente novamente em alguns instantes.
                    """;

        } catch (Exception e) {

            return """
                    ❌ Falha de comunicação com a Groq.

                    Detalhe técnico: %s
                    """.formatted(e.getMessage());
        }
    }

    private String tratarResposta(HttpResponse<String> response) throws Exception {

        int status = response.statusCode();

        if (status == 401) {
            return """
                    🔑 Chave da API inválida.

                    Confira o valor de "groq.api.key" no config.properties
                    e gere uma nova chave em console.groq.com, se necessário.
                    """;
        }

        if (status == 429) {
            return """
                    🚦 Limite de uso da API atingido.

                    Você fez requisições demais em pouco tempo.
                    Aguarde um instante antes de tentar de novo.
                    """;
        }

        if (status >= 500) {
            return """
                    🛠️ A Groq está com instabilidade no momento (erro %d).

                    Tente novamente em alguns minutos.
                    """.formatted(status);
        }

        if (status != 200) {
            return """
                    ❌ Erro inesperado da Groq (código %d).

                    %s
                    """.formatted(status, response.body());
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
    }
}