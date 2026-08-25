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
        barra.setPrefWidth(230);

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

        HBox entrada = new HBox(10);
        entrada.getStyleClass().add("rodape");
        entrada.setPadding(new Insets(15));
        entrada.setAlignment(Pos.CENTER);

        HBox.setHgrow(campoMensagem, Priority.ALWAYS);

        entrada.getChildren().addAll(campoMensagem, botaoEnviar);

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

    /**
     * Remove todas as mensagens da tela, deixando o chat vazio
     * para começar uma nova conversa.
     */
    public void limparMensagens() {
        mensagens.getChildren().clear();
    }

    /**
     * Adiciona um item clicável na barra lateral de histórico.
     * Retorna o botão para o controller ligar a ação de clique.
     */
    public Button adicionarItemHistorico(String titulo) {

        Button item = new Button(titulo);
        item.getStyleClass().add("item-historico");
        item.setMaxWidth(Double.MAX_VALUE);

        listaHistorico.getChildren().add(item);

        return item;
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

    public Button getBotaoNovaConversa() {
        return botaoNovaConversa;
    }

    public Button getBotaoTema() {
        return botaoTema;
    }
}