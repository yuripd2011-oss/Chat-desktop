package com.example.chatdesktop.controller;

import com.example.chatdesktop.model.ChatMessage;
import com.example.chatdesktop.model.ConversaSalva;
import com.example.chatdesktop.model.MensagemSalva;
import com.example.chatdesktop.service.DataHoraService;
import com.example.chatdesktop.service.GroqService;
import com.example.chatdesktop.service.HistoricoService;
import com.example.chatdesktop.service.RagService;
import com.example.chatdesktop.view.ChatView;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChatController {

    private static final String MENSAGEM_BOAS_VINDAS =
            "Fala! 🔥\n\n" +
                    "Sou seu assistente de IA, pronto pra ajudar no que precisar.\n" +
                    "Manda sua primeira pergunta aí embaixo.";

    private final ChatView view;
    private final GroqService groqService;
    private final RagService ragService;
    private final DataHoraService dataHoraService;
    private final HistoricoService historicoService;

    private final List<ConversaSalva> historico;

    private List<ChatMessage> conversaAtual =
            new ArrayList<>();

    private boolean tituloJaDefinido = false;
    private String ultimaPerguntaUsuario = null;

    private boolean temaEscuro = true;

    public ChatController(ChatView view) {

        this.view = view;

        log("==========================================");
        log("INICIANDO CHAT CONTROLLER");
        log("==========================================");

        this.groqService = new GroqService();
        this.ragService = new RagService();
        this.dataHoraService = new DataHoraService();
        this.historicoService = new HistoricoService();

        this.historico = historicoService.carregar();

        log("Histórico carregado: " + historico.size() + " conversa(s).");

        inicializar();
    }

    private void log(String mensagem) {
        System.out.println("[NeoChat] " + mensagem);
    }

    private void logErro(String mensagem, Exception erro) {
        System.err.println("[NeoChat][ERRO] " + mensagem);
        System.err.println("[NeoChat][ERRO] " + erro.getMessage());
        erro.printStackTrace();
    }

    private void inicializar() {

        try {

            /*
             * HISTÓRICO
             */
            for (ConversaSalva conversaSalva : historico) {
                criarItemNaTela(conversaSalva);
            }

            /*
             * BOAS-VINDAS
             */
            mostrarBoasVindas();

            /*
             * ENTER
             */
            view.getCampoMensagem().setOnAction(event ->
                    enviarMensagem()
            );

            /*
             * ENVIAR
             */
            view.getBotaoEnviar().setOnAction(event ->
                    enviarMensagem()
            );

            /*
             * REGENERAR
             */
            view.getBotaoRegenerar().setOnAction(event ->
                    regenerarResposta()
            );

            /*
             * NOVA CONVERSA
             */
            view.getBotaoNovaConversa().setOnAction(event ->
                    iniciarNovaConversa()
            );

            /*
             * TEMA
             */
            view.getBotaoTema().setOnAction(event ->
                    alternarTema()
            );

            /*
             * Configuração inicial do botão.
             */
            view.getBotaoTema().setText("☀ Tema claro");

            /*
             * Aplica o tema escuro depois que a Scene existir.
             */
            Platform.runLater(this::aplicarTemaEscuro);

            log("ChatController inicializado com sucesso.");

        } catch (Exception erro) {
            logErro("Erro durante a inicialização.", erro);
        }
    }

    /*
     * =========================================================
     * TEMA
     * =========================================================
     */

    private void alternarTema() {

        try {

            Scene cena = view.getRaiz().getScene();

            if (cena == null) {
                log("Scene ainda não disponível.");
                return;
            }

            if (temaEscuro) {
                aplicarTemaClaro();
            } else {
                aplicarTemaEscuro();
            }

        } catch (Exception erro) {
            logErro("Erro ao alternar tema.", erro);
        }
    }

    private void aplicarTemaEscuro() {

        Scene cena = view.getRaiz().getScene();

        if (cena == null) {
            return;
        }

        removerTemas(cena);

        URL css = getClass().getResource(
                "/com/example/chatdesktop/css/chat-dark.css"
        );

        if (css == null) {
            log("chat-dark.css não encontrado.");
            return;
        }

        cena.getStylesheets().add(
                css.toExternalForm()
        );

        temaEscuro = true;

        view.getBotaoTema().setText("☀ Tema claro");

        log("Tema escuro ativado.");
    }

    private void aplicarTemaClaro() {

        Scene cena = view.getRaiz().getScene();

        if (cena == null) {
            return;
        }

        removerTemas(cena);

        URL css = getClass().getResource(
                "/com/example/chatdesktop/css/chat-light.css"
        );

        if (css == null) {
            log("chat-light.css não encontrado.");
            return;
        }

        cena.getStylesheets().add(
                css.toExternalForm()
        );

        temaEscuro = false;

        view.getBotaoTema().setText("🌙 Tema escuro");

        log("Tema claro ativado.");
    }

    private void removerTemas(Scene cena) {

        cena.getStylesheets().removeIf(
                folha ->
                        folha.contains("chat-dark.css")
                                ||
                                folha.contains("chat-light.css")
        );
    }

    /*
     * =========================================================
     * BOAS-VINDAS
     * =========================================================
     */

    private void mostrarBoasVindas() {

        ChatMessage boasVindas =
                new ChatMessage(
                        MENSAGEM_BOAS_VINDAS,
                        false
                );

        view.adicionarMensagem(boasVindas);

        conversaAtual.add(boasVindas);

        view.definirTituloConversaAtual(
                "Nova conversa"
        );

        tituloJaDefinido = false;
        ultimaPerguntaUsuario = null;
    }

    /*
     * =========================================================
     * ENVIAR MENSAGEM
     * =========================================================
     */

    private void enviarMensagem() {

        try {

            if (view.getBotaoEnviar().isDisabled()) {
                return;
            }

            String texto =
                    view.getCampoMensagem()
                            .getText()
                            .trim();

            if (texto.isEmpty()) {
                return;
            }

            ChatMessage mensagemUsuario =
                    new ChatMessage(
                            texto,
                            true
                    );

            view.adicionarMensagem(
                    mensagemUsuario
            );

            conversaAtual.add(
                    mensagemUsuario
            );

            if (!tituloJaDefinido) {

                String titulo =
                        resumir(mensagemUsuario);

                view.definirTituloConversaAtual(
                        titulo
                );

                tituloJaDefinido = true;
            }

            view.getCampoMensagem().clear();

            ultimaPerguntaUsuario = texto;

            processarPergunta(texto);

        } catch (Exception erro) {

            logErro(
                    "Erro ao enviar mensagem.",
                    erro
            );
        }
    }

    /*
     * =========================================================
     * REGENERAR
     * =========================================================
     */

    private void regenerarResposta() {

        try {

            if (ultimaPerguntaUsuario == null) {
                return;
            }

            if (view.getBotaoEnviar().isDisabled()) {
                return;
            }

            if (!conversaAtual.isEmpty()) {

                ChatMessage ultimaMensagem =
                        conversaAtual.get(
                                conversaAtual.size() - 1
                        );

                if (!ultimaMensagem.isDeUsuario()) {

                    conversaAtual.remove(
                            conversaAtual.size() - 1
                    );

                    view.removerUltimaMensagem();
                }
            }

            processarPergunta(
                    ultimaPerguntaUsuario
            );

        } catch (Exception erro) {

            logErro(
                    "Erro ao regenerar resposta.",
                    erro
            );
        }
    }

    /*
     * =========================================================
     * PROCESSAMENTO
     * =========================================================
     */

    private void processarPergunta(
            String texto
    ) {

        view.getBotaoEnviar()
                .setDisable(true);

        view.getBotaoRegenerar()
                .setDisable(true);

        view.definirStatusPensando();

        Thread thread =
                new Thread(() -> {

                    try {

                        ChatMessage mensagemResposta;

                        /*
                         * DATA / HORA
                         */
                        String respostaDataHora =
                                dataHoraService
                                        .responderSeForPerguntaDeDataHora(
                                                texto
                                        );

                        if (respostaDataHora != null
                                && !respostaDataHora.isBlank()) {

                            mensagemResposta =
                                    new ChatMessage(
                                            respostaDataHora,
                                            false,
                                            ChatMessage.Origem.LOCAL,
                                            null
                                    );

                        } else {

                            /*
                             * RAG
                             */
                            String contextoRag =
                                    ragService.buscarContexto(
                                            texto
                                    );

                            if (contextoRag != null
                                    && !contextoRag.isBlank()) {

                                mensagemResposta =
                                        new ChatMessage(
                                                contextoRag,
                                                false,
                                                ChatMessage.Origem.RAG,
                                                ragService.getNomeArquivo()
                                        );

                            } else {

                                /*
                                 * GROQ
                                 */
                                String respostaGroq =
                                        groqService.perguntar(
                                                texto
                                        );

                                if (respostaGroq == null
                                        || respostaGroq.isBlank()) {

                                    respostaGroq =
                                            "Não consegui obter uma resposta agora.";
                                }

                                mensagemResposta =
                                        new ChatMessage(
                                                respostaGroq,
                                                false,
                                                ChatMessage.Origem.INTERNET,
                                                null
                                        );
                            }
                        }

                        ChatMessage respostaFinal =
                                mensagemResposta;

                        Platform.runLater(() -> {

                            view.adicionarMensagem(
                                    respostaFinal
                            );

                            conversaAtual.add(
                                    respostaFinal
                            );

                            view.getBotaoEnviar()
                                    .setDisable(false);

                            view.getBotaoRegenerar()
                                    .setDisable(false);

                            view.definirStatusConectado();

                            view.getCampoMensagem()
                                    .requestFocus();
                        });

                    } catch (Exception erro) {

                        logErro(
                                "Erro ao processar pergunta.",
                                erro
                        );

                        String mensagemErro =
                                obterMensagemErro(erro);

                        Platform.runLater(() -> {

                            ChatMessage respostaErro =
                                    new ChatMessage(
                                            "Não consegui processar sua mensagem.\n\n"
                                                    + mensagemErro,
                                            false,
                                            ChatMessage.Origem.LOCAL,
                                            null
                                    );

                            view.adicionarMensagem(
                                    respostaErro
                            );

                            conversaAtual.add(
                                    respostaErro
                            );

                            view.getBotaoEnviar()
                                    .setDisable(false);

                            view.getBotaoRegenerar()
                                    .setDisable(false);

                            view.definirStatusConectado();

                            view.getCampoMensagem()
                                    .requestFocus();
                        });
                    }

                });

        thread.setDaemon(true);
        thread.setName("NeoChat-Resposta");
        thread.start();
    }

    private String obterMensagemErro(
            Exception erro
    ) {

        if (erro.getMessage() == null
                || erro.getMessage().isBlank()) {

            return "Verifique sua conexão e a configuração da API.";
        }

        return erro.getMessage();
    }

    /*
     * =========================================================
     * NOVA CONVERSA
     * =========================================================
     */

    public void iniciarNovaConversa() {

        try {

            salvarConversaAtualNoHistorico();

            view.limparMensagens();

            view.getCampoMensagem().clear();

            view.getBotaoEnviar()
                    .setDisable(false);

            view.getBotaoRegenerar()
                    .setDisable(false);

            view.definirStatusConectado();

            conversaAtual =
                    new ArrayList<>();

            tituloJaDefinido = false;
            ultimaPerguntaUsuario = null;

            mostrarBoasVindas();

            view.getCampoMensagem()
                    .requestFocus();

        } catch (Exception erro) {

            logErro(
                    "Erro ao iniciar nova conversa.",
                    erro
            );
        }
    }

    /*
     * =========================================================
     * HISTÓRICO
     * =========================================================
     */

    private void salvarConversaAtualNoHistorico() {

        try {

            boolean temMensagemDoUsuario =
                    conversaAtual.stream()
                            .anyMatch(
                                    ChatMessage::isDeUsuario
                            );

            if (!temMensagemDoUsuario) {
                return;
            }

            String tituloItem =
                    conversaAtual.stream()
                            .filter(
                                    ChatMessage::isDeUsuario
                            )
                            .findFirst()
                            .map(
                                    ChatController::resumir
                            )
                            .orElse(
                                    "Conversa sem título"
                            );

            ConversaSalva conversaSalva =
                    new ConversaSalva();

            conversaSalva.setTitulo(
                    tituloItem
            );

            conversaSalva.setMensagens(
                    paraMensagensSalvas(
                            conversaAtual
                    )
            );

            historico.add(
                    conversaSalva
            );

            persistirHistorico();

            criarItemNaTela(
                    conversaSalva
            );

        } catch (Exception erro) {

            logErro(
                    "Erro ao salvar conversa.",
                    erro
            );
        }
    }

    private void criarItemNaTela(
            ConversaSalva conversaSalva
    ) {

        try {

            ChatView.ItemHistorico item =
                    view.adicionarItemHistorico(
                            conversaSalva.getTitulo()
                    );

            /*
             * CARREGAR
             */
            item.getBotaoCarregar()
                    .setOnAction(event ->
                            carregarConversa(
                                    conversaSalva
                            )
                    );

            /*
             * RENOMEAR
             */
            item.getBotaoRenomear()
                    .setOnAction(event -> {

                        try {

                            TextInputDialog dialog =
                                    new TextInputDialog(
                                            conversaSalva.getTitulo()
                                    );

                            dialog.setTitle(
                                    "Renomear conversa"
                            );

                            dialog.setHeaderText(null);

                            dialog.setContentText(
                                    "Novo nome:"
                            );

                            dialog.showAndWait()
                                    .ifPresent(
                                            novoTitulo -> {

                                                String limpo =
                                                        novoTitulo.trim();

                                                if (!limpo.isEmpty()) {

                                                    conversaSalva.setTitulo(
                                                            limpo
                                                    );

                                                    item.definirTitulo(
                                                            limpo
                                                    );

                                                    persistirHistorico();
                                                }
                                            }
                                    );

                        } catch (Exception erro) {

                            logErro(
                                    "Erro ao renomear conversa.",
                                    erro
                            );
                        }
                    });

            /*
             * EXCLUIR
             */
            item.getBotaoExcluir()
                    .setOnAction(event -> {

                        try {

                            Alert alerta =
                                    new Alert(
                                            Alert.AlertType.CONFIRMATION
                                    );

                            alerta.setTitle(
                                    "Excluir conversa"
                            );

                            alerta.setHeaderText(null);

                            alerta.setContentText(
                                    "Tem certeza que deseja excluir esta conversa do histórico?"
                            );

                            alerta.showAndWait()
                                    .ifPresent(botao -> {

                                        if (botao ==
                                                ButtonType.OK) {

                                            historico.remove(
                                                    conversaSalva
                                            );

                                            persistirHistorico();

                                            item.removerDaLista();
                                        }
                                    });

                        } catch (Exception erro) {

                            logErro(
                                    "Erro ao excluir conversa.",
                                    erro
                            );
                        }
                    });

        } catch (Exception erro) {

            logErro(
                    "Erro ao criar item do histórico.",
                    erro
            );
        }
    }

    private void carregarConversa(
            ConversaSalva conversaSalva
    ) {

        try {

            view.limparMensagens();

            conversaAtual =
                    paraChatMessages(
                            conversaSalva.getMensagens()
                    );

            tituloJaDefinido = true;

            view.definirTituloConversaAtual(
                    conversaSalva.getTitulo()
            );

            ultimaPerguntaUsuario =
                    conversaAtual.stream()
                            .filter(
                                    ChatMessage::isDeUsuario
                            )
                            .reduce(
                                    (primeira, segunda) ->
                                            segunda
                            )
                            .map(
                                    ChatMessage::getTexto
                            )
                            .orElse(null);

            for (ChatMessage mensagem :
                    conversaAtual) {

                view.adicionarMensagem(
                        mensagem
                );
            }

            view.getBotaoEnviar()
                    .setDisable(false);

            view.getBotaoRegenerar()
                    .setDisable(false);

            view.definirStatusConectado();

            view.getCampoMensagem()
                    .requestFocus();

        } catch (Exception erro) {

            logErro(
                    "Erro ao carregar conversa.",
                    erro
            );
        }
    }

    private void persistirHistorico() {

        try {

            historicoService.salvar(
                    historico
            );

        } catch (Exception erro) {

            logErro(
                    "Erro ao salvar histórico.",
                    erro
            );
        }
    }

    /*
     * =========================================================
     * CONVERSÃO DO HISTÓRICO
     * =========================================================
     */

    private static List<MensagemSalva>
    paraMensagensSalvas(
            List<ChatMessage> mensagens
    ) {

        List<MensagemSalva> resultado =
                new ArrayList<>();

        for (ChatMessage m : mensagens) {

            MensagemSalva ms =
                    new MensagemSalva();

            ms.setTexto(
                    m.getTexto()
            );

            ms.setDeUsuario(
                    m.isDeUsuario()
            );

            ms.setHorario(
                    m.getHorarioFormatado()
            );

            ms.setOrigem(
                    m.getOrigem() != null
                            ? m.getOrigem().name()
                            : null
            );

            ms.setFonte(
                    m.getFonte()
            );

            resultado.add(ms);
        }

        return resultado;
    }

    private static List<ChatMessage>
    paraChatMessages(
            List<MensagemSalva> mensagens
    ) {

        List<ChatMessage> resultado =
                new ArrayList<>();

        for (MensagemSalva ms : mensagens) {

            ChatMessage.Origem origem =
                    ms.getOrigem() != null
                            ? ChatMessage.Origem.valueOf(
                            ms.getOrigem()
                    )
                            : null;

            resultado.add(
                    new ChatMessage(
                            ms.getTexto(),
                            ms.isDeUsuario(),
                            origem,
                            ms.getFonte(),
                            ms.getHorario()
                    )
            );
        }

        return resultado;
    }

    /*
     * =========================================================
     * TÍTULO
     * =========================================================
     */

    private static String resumir(
            ChatMessage mensagem
    ) {

        String texto =
                mensagem.getTexto()
                        .replace("\n", " ")
                        .trim();

        return texto.length() > 28
                ? texto.substring(0, 28) + "..."
                : texto;
    }
}