package com.example.chatdesktop.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma única mensagem no chat, seja do usuário ou do assistente.
 * Mensagens do assistente podem carregar a origem da resposta (RAG,
 * chamada à internet/Groq, ou fallback local) e, quando vier de RAG,
 * o nome do arquivo-fonte usado.
 */
public class ChatMessage {

    public enum Origem { RAG, INTERNET, LOCAL }

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    private final String texto;
    private final boolean deUsuario;
    private final LocalTime horario;
    private final Origem origem;
    private final String fonte;

    public ChatMessage(String texto, boolean deUsuario) {
        this(texto, deUsuario, null, null);
    }

    public ChatMessage(String texto, boolean deUsuario, Origem origem, String fonte) {
        this.texto = texto;
        this.deUsuario = deUsuario;
        this.horario = LocalTime.now();
        this.origem = origem;
        this.fonte = fonte;
    }

    public String getTexto() {
        return texto;
    }

    public boolean isDeUsuario() {
        return deUsuario;
    }

    public String getHorarioFormatado() {
        return horario.format(FORMATO_HORA);
    }

    public Origem getOrigem() {
        return origem;
    }

    public String getFonte() {
        return fonte;
    }
}