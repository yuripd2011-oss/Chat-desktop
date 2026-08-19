package com.example.chatdesktop.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma única mensagem no chat, seja do usuário ou do assistente.
 */
public class ChatMessage {

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    private final String texto;
    private final boolean deUsuario;
    private final LocalTime horario;

    public ChatMessage(String texto, boolean deUsuario) {
        this.texto = texto;
        this.deUsuario = deUsuario;
        this.horario = LocalTime.now();
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
}