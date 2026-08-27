package com.example.chatdesktop.view;

import com.example.chatdesktop.model.ChatMessage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Responsável apenas por montar e atualizar a interface gráfica.
 * Não contém nenhuma lógica de negócio.
 */
public class ChatView {

    private final BorderPane raiz = new BorderPane();
    private final VBox mensagens = new VBox(15);
    private final VBox listaHistorico = new VBox(8);
    private final TextField campoMensagem = new TextField();
    private final Button botaoEnviar = new Button("Enviar");
    private final Button botaoRegenerar = new Button("🔄");
    private final Button botaoNovaConversa = new Button("+ Nova conversa");
    private final Button botaoTema = new Button("🌙 Tema");
    private final Label status = new Label("● Conectado à Groq");
    private final Label tituloConversaAtual = new Label("Nova conversa");

    public ChatView() {
        montarInterface();
    }

    private void montarInterface() {

        raiz.getStyleClass().add("root");

        raiz.setTop(construirTopo());
        raiz.setLeft(construirBarraLateral());

        mensagens.setPadding(new Insets(20));

        ScrollPane scroll = new ScrollPane(mensagens);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("area-mensagens");

        raiz.setCenter(scroll);

        raiz.setBottom(construirEntrada());
    }

    private HBox construirTopo() {

        HBox topo = new HBox();
        topo.getStyleClass().add("topo");
        topo.setPadding(new Insets(18));
        topo.setAlignment(Pos.CENTER_LEFT);
        topo.setSpacing(12);

        VBox informacoes = new VBox(3);

        Label titulo = new Label("NeoChat");
        titulo.getStyleClass().add("titulo");

        tituloConversaAtual.getStyleClass().add("subtitulo");

        status.getStyleClass().add("status-conectado");

        informacoes.getChildren().addAll(titulo, tituloConversaAtual, status);

        topo.getChildren().add(informacoes);

        return topo;
    }

    private VBox construirBarraLateral() {

        VBox barra = new VBox(10);
        barra.getStyleClass().add("barra-lateral");
        barra.setPadding(new Insets(18, 12, 18, 12));
        barra.setPrefWidth(240);

        botaoNovaConversa.setMaxWidth(Double.MAX_VALUE);
        botaoNovaConversa.getStyleClass().add("botao-nova-conversa");

        botaoTema.setMaxWidth(Double.MAX_VALUE);
        botaoTema.getStyleClass().add("botao-tema");

        Separator separador = new Separator();

        Label tituloHistorico = new Label("Histórico");
        tituloHistorico.getStyleClass().add("titulo-historico");

        listaHistorico.setFillWidth(true);

        ScrollPane scroll = new ScrollPane(listaHistorico);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("area-historico");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        barra.getChildren().addAll(
                botaoNovaConversa,
                botaoTema,
                separador,
                tituloHistorico,
                scroll
        );

        return barra;
    }

    private HBox construirEntrada() {

        campoMensagem.setPromptText("Digite sua mensagem... (Enter para enviar)");
        campoMensagem.getStyleClass().add("campo-mensagem");

        botaoEnviar.getStyleClass().add("botao-enviar");

        botaoRegenerar.getStyleClass().add("botao-regenerar");
        botaoRegenerar.setTooltip(new javafx.scene.control.Tooltip("Regenerar última resposta"));

        HBox entrada = new HBox(10);
        entrada.getStyleClass().add("rodape");
        entrada.setPadding(new Insets(15));
        entrada.setAlignment(Pos.CENTER);

        HBox.setHgrow(campoMensagem, Priority.ALWAYS);

        entrada.getChildren().addAll(campoMensagem, botaoRegenerar, botaoEnviar);

        return entrada;
    }

    public void adicionarMensagem(ChatMessage mensagem) {

        Label texto = new Label(mensagem.getTexto());
        texto.setWrapText(true);
        texto.setMaxWidth(650);
        texto.setPadding(new Insets(12, 16, 12, 16));
        texto.getStyleClass().add(
                mensagem.isDeUsuario() ? "bolha-usuario" : "bolha-assistente"
        );

        Label avatar = new Label(mensagem.isDeUsuario() ? "🧑" : "🤖");
        avatar.getStyleClass().addAll(
                "avatar",
                mensagem.isDeUsuario() ? "avatar-usuario" : "avatar-assistente"
        );

        Label horario = new Label(mensagem.getHorarioFormatado());
        horario.getStyleClass().add("horario");

        VBox blocoTextoHorario = new VBox(2, texto, horario);
        blocoTextoHorario.setAlignment(
                mensagem.isDeUsuario() ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT
        );

        // indicador de origem + fonte (só em respostas do assistente)
        if (!mensagem.isDeUsuario() && mensagem.getOrigem() != null) {

            Label badgeOrigem = new Label(rotuloOrigem(mensagem.getOrigem()));
            badgeOrigem.getStyleClass().addAll(
                    "badge-origem",
                    "badge-origem-" + mensagem.getOrigem().name().toLowerCase()
            );

            blocoTextoHorario.getChildren().add(badgeOrigem);

            if (mensagem.getFonte() != null) {
                Label labelFonte = new Label("Fonte: " + mensagem.getFonte());
                labelFonte.getStyleClass().add("label-fonte");
                blocoTextoHorario.getChildren().add(labelFonte);
            }
        }

        HBox linha = new HBox(8);
        linha.setAlignment(Pos.BOTTOM_CENTER);

        if (mensagem.isDeUsuario()) {
            linha.setAlignment(Pos.CENTER_RIGHT);
            linha.getChildren().addAll(blocoTextoHorario, avatar);
        } else {
            linha.setAlignment(Pos.CENTER_LEFT);
            linha.getChildren().addAll(avatar, blocoTextoHorario);
        }

        mensagens.getChildren().add(linha);
    }

    private String rotuloOrigem(ChatMessage.Origem origem) {
        return switch (origem) {
            case RAG -> "📄 RAG";
            case INTERNET -> "🌐 Internet";
            case LOCAL -> "🖥 Fallback local";
        };
    }

    /**
     * Remove todas as mensagens da tela, deixando o chat vazio
     * para começar uma nova conversa.
     */
    public void limparMensagens() {
        mensagens.getChildren().clear();
    }

    /**
     * Remove a última mensagem exibida (usado para "Regenerar resposta").
     */
    public void removerUltimaMensagem() {
        if (!mensagens.getChildren().isEmpty()) {
            mensagens.getChildren().remove(mensagens.getChildren().size() - 1);
        }
    }

    /**
     * Adiciona um item na barra lateral de histórico, com botão de
     * carregar, renomear e excluir. O controller liga as ações.
     */
    public ItemHistorico adicionarItemHistorico(String titulo) {

        Button botaoCarregar = new Button(titulo);
        botaoCarregar.getStyleClass().add("item-historico");
        botaoCarregar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(botaoCarregar, Priority.ALWAYS);

        Button botaoRenomear = new Button("✎");
        botaoRenomear.getStyleClass().add("botao-icone-historico");

        Button botaoExcluir = new Button("🗑");
        botaoExcluir.getStyleClass().add("botao-icone-historico");

        HBox linha = new HBox(4, botaoCarregar, botaoRenomear, botaoExcluir);
        linha.setAlignment(Pos.CENTER_LEFT);

        listaHistorico.getChildren().add(linha);

        return new ItemHistorico(linha, botaoCarregar, botaoRenomear, botaoExcluir);
    }

    public void definirTituloConversaAtual(String titulo) {
        tituloConversaAtual.setText(titulo);
    }

    public void definirStatusPensando() {
        status.setText("● Tecendo a resposta...");
        status.getStyleClass().setAll("status-pensando");
    }

    public void definirStatusConectado() {
        status.setText("● Conectado à Groq");
        status.getStyleClass().setAll("status-conectado");
    }

    public BorderPane getRaiz() {
        return raiz;
    }

    public TextField getCampoMensagem() {
        return campoMensagem;
    }

    public Button getBotaoEnviar() {
        return botaoEnviar;
    }

    public Button getBotaoRegenerar() {
        return botaoRegenerar;
    }

    public Button getBotaoNovaConversa() {
        return botaoNovaConversa;
    }

    public Button getBotaoTema() {
        return botaoTema;
    }

    /**
     * Representa uma linha do histórico, com os três botões que o
     * controller pode ligar (carregar, renomear, excluir) e um jeito
     * de atualizar o título ou remover a linha da lista.
     */
    public class ItemHistorico {

        private final HBox linha;
        private final Button botaoCarregar;
        private final Button botaoRenomear;
        private final Button botaoExcluir;
        private String tituloAtual;

        private ItemHistorico(HBox linha, Button botaoCarregar, Button botaoRenomear, Button botaoExcluir) {
            this.linha = linha;
            this.botaoCarregar = botaoCarregar;
            this.botaoRenomear = botaoRenomear;
            this.botaoExcluir = botaoExcluir;
            this.tituloAtual = botaoCarregar.getText();
        }

        public Button getBotaoCarregar() {
            return botaoCarregar;
        }

        public Button getBotaoRenomear() {
            return botaoRenomear;
        }

        public Button getBotaoExcluir() {
            return botaoExcluir;
        }

        public String getTituloAtual() {
            return tituloAtual;
        }

        public void definirTitulo(String novoTitulo) {
            this.tituloAtual = novoTitulo;
            botaoCarregar.setText(novoTitulo);
        }

        public void removerDaLista() {
            listaHistorico.getChildren().remove(linha);
        }
    }
}