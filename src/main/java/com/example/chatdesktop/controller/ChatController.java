package com.example.chatdesktop.controller;

import com.example.chatdesktop.model.ChatMessage;
import com.example.chatdesktop.service.GroqService;
import com.example.chatdesktop.view.ChatView;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

/**
 * Faz a ponte entre a View (interface) e o Service (Groq).
 * Contém a lógica de interação do chat, incluindo o histórico de conversas.
 */
public class ChatController {

    private static final String MENSAGEM_BOAS_VINDAS =
            "Fala! 🔥\n\n" +
                    "Sou seu assistente de IA, pronto pra ajudar no que precisar.\n" +
                    "Manda sua primeira pergunta aí embaixo.";

    private final ChatView view;
    private final GroqService groqService;

    private List<ChatMessage> conversaAtual = new ArrayList<>();
    private int contadorConversas = 1;

    public ChatController(ChatView view) {
        this.view = view;
        this.groqService = new GroqService();

        inicializar();
    }

    private void inicializar() {

        mostrarBoasVindas();

        view.getCampoMensagem().setOnAction(event -> enviarMensagem());
        view.getBotaoEnviar().setOnAction(event -> enviarMensagem());
        view.getBotaoNovaConversa().setOnAction(event -> iniciarNovaConversa());
    }

    private void mostrarBoasVindas() {
        ChatMessage boasVindas = new ChatMessage(MENSAGEM_BOAS_VINDAS, false);
        view.adicionarMensagem(boasVindas);
        conversaAtual.add(boasVindas);
    }

    private void enviarMensagem() {

        String texto = view.getCampoMensagem().getText().trim();

        if (texto.isEmpty()) {
            return;
        }

        ChatMessage mensagemUsuario = new ChatMessage(texto, true);
        view.adicionarMensagem(mensagemUsuario);
        conversaAtual.add(mensagemUsuario);

        view.getCampoMensagem().clear();

        view.getBotaoEnviar().setDisable(true);
        view.definirStatusPensando();

        Thread thread = new Thread(() -> {

            String resposta = groqService.perguntar(texto);

            Platform.runLater(() -> {
                ChatMessage mensagemResposta = new ChatMessage(resposta, false);
                view.adicionarMensagem(mensagemResposta);
                conversaAtual.add(mensagemResposta);

                view.getBotaoEnviar().setDisable(false);
                view.definirStatusConectado();
            });
        });

        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Salva a conversa atual no histórico (se tiver mensagens do usuário),
     * limpa a tela e começa uma conversa nova.
     */
    private void iniciarNovaConversa() {

        salvarConversaAtualNoHistorico();

        view.limparMensagens();
        view.getCampoMensagem().clear();
        view.definirStatusConectado();

        conversaAtual = new ArrayList<>();
        mostrarBoasVindas();
    }

    private void salvarConversaAtualNoHistorico() {

        boolean temMensagemDoUsuario = conversaAtual.stream()
                .anyMatch(ChatMessage::isDeUsuario);

        if (!temMensagemDoUsuario) {
            return;
        }

        List<ChatMessage> conversaSalva = new ArrayList<>(conversaAtual);

        String tituloItem = conversaSalva.stream()
                .filter(ChatMessage::isDeUsuario)
                .findFirst()
                .map(ChatController::resumir)
                .orElse("Conversa " + contadorConversas);

        contadorConversas++;

        var botaoItem = view.adicionarItemHistorico(tituloItem);
        botaoItem.setOnAction(event -> carregarConversa(conversaSalva));
    }

    private void carregarConversa(List<ChatMessage> conversa) {

        salvarConversaAtualNoHistorico();

        view.limparMensagens();

        conversaAtual = new ArrayList<>(conversa);

        for (ChatMessage mensagem : conversaAtual) {
            view.adicionarMensagem(mensagem);
        }

        view.definirStatusConectado();
    }

    private static String resumir(ChatMessage mensagem) {
        String texto = mensagem.getTexto().replace("\n", " ").trim();
        return texto.length() > 28 ? texto.substring(0, 28) + "..." : texto;
    }
}