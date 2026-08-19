package com.example.chatdesktop.view;

import com.example.chatdesktop.model.ChatMessage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Responsável apenas por montar e atualizar a interface gráfica.
 * Tema visual: teia + vermelho/azul (inspirado no herói amigo da vizinhança).
 * Não contém nenhuma lógica de negócio.
 */
public class ChatView {

    private final BorderPane raiz = new BorderPane();
    private final VBox mensagens = new VBox(15);
    private final TextField campoMensagem = new TextField();
    private final Button botaoEnviar = new Button("Enviar");
    private final Label status = new Label("● Conectado à Groq");

    public ChatView() {
        montarInterface();
    }

    private void montarInterface() {

        raiz.getStyleClass().add("root");

        raiz.setTop(construirTopo());

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

        Label icone = new Label("🕸");
        icone.getStyleClass().add("icone-topo");

        VBox informacoes = new VBox(3);

        Label titulo = new Label("Chat Desktop");
        titulo.getStyleClass().add("titulo");

        Label subtitulo = new Label("seu assistente amigo da vizinhança");
        subtitulo.getStyleClass().add("subtitulo");

        status.getStyleClass().add("status-conectado");

        informacoes.getChildren().addAll(titulo, subtitulo, status);
        topo.getChildren().addAll(icone, informacoes);

        return topo;
    }

    private HBox construirEntrada() {

        campoMensagem.setPromptText("Digite sua mensagem...");
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

        Label avatar = new Label(mensagem.isDeUsuario() ? "🧑" : "🕷");
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
}