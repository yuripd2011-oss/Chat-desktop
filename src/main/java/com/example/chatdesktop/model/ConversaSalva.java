package com.example.chatdesktop.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Uma conversa completa salva no histórico em disco: título + mensagens.
 */
public class ConversaSalva {

    private String titulo;
    private List<MensagemSalva> mensagens = new ArrayList<>();

    public ConversaSalva() {
        // construtor vazio exigido pelo Jackson
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<MensagemSalva> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<MensagemSalva> mensagens) {
        this.mensagens = mensagens;
    }
}