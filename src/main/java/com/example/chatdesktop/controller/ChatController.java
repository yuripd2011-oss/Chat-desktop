package com.example.chatdesktop.controller;

import com.example.chatdesktop.model.ChatMessage;
import com.example.chatdesktop.service.DataHoraService;
import com.example.chatdesktop.service.GroqService;
import com.example.chatdesktop.service.RagService;
import com.example.chatdesktop.view.ChatView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Faz a ponte entre a View (interface) e os Services (Groq, RAG, data/hora).
 * Contém a lógica de interação do chat: histórico de conversas, título
 * automático, indicador de origem da resposta e regeneração de resposta.
 */
public class ChatController {

    private static final String MENSAGEM_BOAS_VINDAS =
            "Fala! 🔥\n\n" +
                    "Sou seu assistente de IA, pronto pra ajudar no que precisar.\n" +
                    "Manda sua primeira pergunta aí embaixo.";

    private final ChatView view;
    private final GroqService groqService;
    private final RagService ragService;
    private final DataHoraService dataHoraService;

    private List<ChatMessage> conversaAtual = new ArrayList<>();
    private boolean tituloJaDefinido = false;
    private String ultimaPerguntaUsuario = null;

    public ChatController(ChatView view) {
        this.view = view;
        this.groqService = new GroqService();
        this.ragService = new RagService();
        this.dataHoraService = new DataHoraService();

        inicializar();
    }

    private void inicializar() {

        mostrarBoasVindas();

        view.getCampoMensagem().setOnAction(event -> enviarMensagem());
        view.getBotaoEnviar().setOnAction(event -> enviarMensagem());
        view.getBotaoRegenerar().setOnAction(event -> regenerarResposta());
        view.getBotaoNovaConversa().setOnAction(event -> iniciarNovaConversa());
    }

    private void mostrarBoasVindas() {
        ChatMessage boasVindas = new ChatMessage(MENSAGEM_BOAS_VINDAS, false);
        view.adicionarMensagem(boasVindas);
        conversaAtual.add(boasVindas);
        view.definirTituloConversaAtual("Nova conversa");
        tituloJaDefinido = false;
        ultimaPerguntaUsuario = null;
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

        ultimaPerguntaUsuario = texto;
        processarPergunta(texto);
    }

    /**
     * Refaz a última pergunta, removendo a resposta anterior e
     * gerando uma nova no lugar.
     */
    private void regenerarResposta() {

        if (ultimaPerguntaUsuario == null) {
            return;
        }

        if (!conversaAtual.isEmpty() && !conversaAtual.get(conversaAtual.size() - 1).isDeUsuario()) {
            conversaAtual.remove(conversaAtual.size() - 1);
            view.removerUltimaMensagem();
        }

        processarPergunta(ultimaPerguntaUsuario);
    }

    /**
     * Busca a resposta (data/hora local, RAG, ou Groq pela internet),
     * já marcando a origem e a fonte da mensagem.
     */
    private void processarPergunta(String texto) {

        view.getBotaoEnviar().setDisable(true);
        view.getBotaoRegenerar().setDisable(true);
        view.definirStatusPensando();

        Thread thread = new Thread(() -> {

            ChatMessage mensagemResposta;

            String respostaDataHora = dataHoraService.responderSeForPerguntaDeDataHora(texto);

            if (respostaDataHora != null) {
                mensagemResposta = new ChatMessage(
                        respostaDataHora, false, ChatMessage.Origem.LOCAL, null
                );
            } else {
                String contextoRag = ragService.buscarContexto(texto);

                if (!contextoRag.isBlank()) {
                    mensagemResposta = new ChatMessage(
                            contextoRag, false, ChatMessage.Origem.RAG, ragService.getNomeArquivo()
                    );
                } else {
                    String respostaGroq = groqService.perguntar(texto);
                    mensagemResposta = new ChatMessage(
                            respostaGroq, false, ChatMessage.Origem.INTERNET, null
                    );
                }
            }

            ChatMessage respostaFinal = mensagemResposta;

            Platform.runLater(() -> {
                view.adicionarMensagem(respostaFinal);
                conversaAtual.add(respostaFinal);

                view.getBotaoEnviar().setDisable(false);
                view.getBotaoRegenerar().setDisable(false);
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

        ChatView.ItemHistorico item = view.adicionarItemHistorico(tituloItem);

        item.getBotaoCarregar().setOnAction(event ->
                carregarConversa(conversaSalva, item.getTituloAtual()));

        item.getBotaoRenomear().setOnAction(event -> renomearItem(item));

        item.getBotaoExcluir().setOnAction(event -> excluirItem(item));
    }

    private void renomearItem(ChatView.ItemHistorico item) {

        TextInputDialog dialog = new TextInputDialog(item.getTituloAtual());
        dialog.setTitle("Renomear conversa");
        dialog.setHeaderText(null);
        dialog.setContentText("Novo nome:");

        dialog.showAndWait().ifPresent(novoTitulo -> {
            String limpo = novoTitulo.trim();
            if (!limpo.isEmpty()) {
                item.definirTitulo(limpo);
            }
        });
    }

    private void excluirItem(ChatView.ItemHistorico item) {

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Excluir conversa");
        alerta.setHeaderText(null);
        alerta.setContentText("Tem certeza que deseja excluir esta conversa do histórico?");

        alerta.showAndWait().ifPresent(botao -> {
            if (botao == ButtonType.OK) {
                item.removerDaLista();
            }
        });
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