package com.example.chatdesktop.controller;

import com.example.chatdesktop.model.ChatMessage;
import com.example.chatdesktop.service.GroqService;
import com.example.chatdesktop.view.ChatView;
import javafx.application.Platform;

/**
 * Faz a ponte entre a View (interface) e o Service (Groq).
 * Contém a lógica de interação do chat.
 */
public class ChatController {

    private final ChatView view;
    private final GroqService groqService;

    public ChatController(ChatView view) {
        this.view = view;
        this.groqService = new GroqService();

        inicializar();
    }

    private void inicializar() {

        view.adicionarMensagem(new ChatMessage(
                "Olá! 👋\n\n" +
                        "Eu sou seu assistente de IA.\n" +
                        "Digite uma mensagem abaixo para começar.",
                false
        ));

        view.getCampoMensagem().setOnAction(event -> enviarMensagem());
        view.getBotaoEnviar().setOnAction(event -> enviarMensagem());
    }

    private void enviarMensagem() {

        String texto = view.getCampoMensagem().getText().trim();

        if (texto.isEmpty()) {
            return;
        }

        view.adicionarMensagem(new ChatMessage(texto, true));
        view.getCampoMensagem().clear();

        view.getBotaoEnviar().setDisable(true);
        view.definirStatusPensando();

        Thread thread = new Thread(() -> {

            String resposta = groqService.perguntar(texto);

            Platform.runLater(() -> {
                view.adicionarMensagem(new ChatMessage(resposta, false));
                view.getBotaoEnviar().setDisable(false);
                view.definirStatusConectado();
            });
        });

        thread.setDaemon(true);
        thread.start();
    }
}