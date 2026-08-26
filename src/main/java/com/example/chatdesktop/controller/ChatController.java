package com.example.chatdesktop.controller;

import com.example.chatdesktop.model.ChatMessage;
import com.example.chatdesktop.service.GroqService;
import com.example.chatdesktop.service.RagService;
import com.example.chatdesktop.view.ChatView;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

/**
 * Faz a ponte entre a View (interface) e o Service (Groq).
 * Contém a lógica de interação do chat, incluindo o histórico de conversas,
 * o título automático de cada conversa e a busca de contexto (RAG).
 */
public class ChatController {

    private static final String MENSAGEM_BOAS_VINDAS =
            "Fala! 🔥\n\n" +
                    "Sou seu assistente de IA, pronto pra ajudar no que precisar.\n" +
                    "Manda sua primeira pergunta aí embaixo.";

    private final ChatView view;
    private final GroqService groqService;
    private final RagService ragService;

    private List<ChatMessage> conversaAtual = new ArrayList<>();
    private boolean tituloJaDefinido = false;

    public ChatController(ChatView view) {
        this.view = view;
        this.groqService = new GroqService();
        this.ragService = new RagService();

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
        view.definirTituloConversaAtual("Nova conversa");
        tituloJaDefinido = false;
    }

    private void enviarMensagem() {

        String texto = view.getCampoMensagem().getText().trim();

        if (texto.isEmpty()) {
            return;
        }

        ChatMessage mensagemUsuario = new ChatMessage(texto, true);
        view.adicionarMensagem(mensagemUsuario);
        conversaAtual.add(mensagemUsuario);

        // título automático: gerado a partir da primeira mensagem do usuário
        if (!tituloJaDefinido) {
            view.definirTituloConversaAtual(resumir(mensagemUsuario));
            tituloJaDefinido = true;
        }

        view.getCampoMensagem().clear();

        view.getBotaoEnviar().setDisable(true);
        view.definirStatusPensando();

        Thread thread = new Thread(() -> {

            String contexto = ragService.buscarContexto(texto);

            String resposta = !contexto.isBlank()
                    ? contexto
                    : groqService.perguntar(texto);

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
    public void iniciarNovaConversa() {

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
                .orElse("Conversa sem título");

        var botaoItem = view.adicionarItemHistorico(tituloItem);
        botaoItem.setOnAction(event -> carregarConversa(conversaSalva, tituloItem));
    }

    private void carregarConversa(List<ChatMessage> conversa, String titulo) {

        salvarConversaAtualNoHistorico();

        view.limparMensagens();

        conversaAtual = new ArrayList<>(conversa);
        tituloJaDefinido = true;
        view.definirTituloConversaAtual(titulo);

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