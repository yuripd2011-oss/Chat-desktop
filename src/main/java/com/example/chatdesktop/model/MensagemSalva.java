package com.example.chatdesktop.model;

/**
 * Versão "achatada" de ChatMessage, própria para ser salva em JSON
 * (precisa de getters/setters e construtor vazio para o Jackson).
 */
public class MensagemSalva {

    private String texto;
    private boolean deUsuario;
    private String horario;
    private String origem; // nome do enum ChatMessage.Origem, ou null
    private String fonte;

    public MensagemSalva() {
        // construtor vazio exigido pelo Jackson
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public boolean isDeUsuario() {
        return deUsuario;
    }

    public void setDeUsuario(boolean deUsuario) {
        this.deUsuario = deUsuario;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }
}