package com.example.chatdesktop.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LoginView {

    private final BorderPane raiz = new BorderPane();

    private final TextField campoEmail = new TextField();
    private final PasswordField campoSenha = new PasswordField();

    private final Button botaoEntrar = new Button("Entrar");
    private final Button botaoCriarConta = new Button("Criar uma conta");

    private final Label mensagemErro = new Label();

    public LoginView() {
        montarInterface();
    }

    private void montarInterface() {

        raiz.getStyleClass().add("login-root");

        VBox container = new VBox(0);
        container.getStyleClass().add("login-container");

        VBox logo = construirLogo();
        VBox formulario = construirFormulario();

        container.getChildren().addAll(
                logo,
                formulario
        );

        BorderPane.setAlignment(
                container,
                Pos.CENTER
        );

        raiz.setCenter(container);
    }

    private VBox construirLogo() {

        VBox logo = new VBox(8);

        logo.setAlignment(Pos.CENTER);
        logo.getStyleClass().add("logo-area");

        Label simbolo = new Label("✦");
        simbolo.getStyleClass().add("logo-simbolo");

        Label titulo = new Label("NeoChat");
        titulo.getStyleClass().add("logo-titulo");

        Label subtitulo = new Label(
                "Seu assistente inteligente"
        );

        subtitulo.getStyleClass().add(
                "logo-subtitulo"
        );

        logo.getChildren().addAll(
                simbolo,
                titulo,
                subtitulo
        );

        return logo;
    }

    private VBox construirFormulario() {

        VBox formulario = new VBox(14);

        formulario.getStyleClass().add(
                "login-card"
        );

        Label titulo = new Label(
                "Bem-vindo de volta"
        );

        titulo.getStyleClass().add(
                "login-titulo"
        );

        Label descricao = new Label(
                "Entre na sua conta para continuar."
        );

        descricao.getStyleClass().add(
                "login-descricao"
        );

        VBox campoEmailBox = construirCampo(
                "E-mail",
                campoEmail
        );

        VBox campoSenhaBox = construirCampo(
                "Senha",
                campoSenha
        );

        campoEmail.setPromptText(
                "seu@email.com"
        );

        campoSenha.setPromptText(
                "Digite sua senha"
        );

        campoEmail.setOnAction(event ->
                campoSenha.requestFocus()
        );

        campoSenha.setOnAction(event ->
                botaoEntrar.fire()
        );

        botaoEntrar.getStyleClass().add(
                "botao-entrar"
        );

        botaoEntrar.setMaxWidth(
                Double.MAX_VALUE
        );

        botaoCriarConta.getStyleClass().add(
                "botao-criar-conta"
        );

        botaoCriarConta.setMaxWidth(
                Double.MAX_VALUE
        );

        mensagemErro.getStyleClass().add(
                "mensagem-erro"
        );

        mensagemErro.setVisible(false);
        mensagemErro.setManaged(false);

        Label separadorOu = new Label("ou");

        separadorOu.getStyleClass().add(
                "separador-texto"
        );

        HBox linha = new HBox(10);

        linha.setAlignment(Pos.CENTER);

        Label linhaEsquerda = new Label();

        linhaEsquerda.getStyleClass().add(
                "linha-separador"
        );

        Label linhaDireita = new Label();

        linhaDireita.getStyleClass().add(
                "linha-separador"
        );

        HBox.setHgrow(
                linhaEsquerda,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                linhaDireita,
                Priority.ALWAYS
        );

        linha.getChildren().addAll(
                linhaEsquerda,
                separadorOu,
                linhaDireita
        );

        Label rodape = new Label(
                "NeoChat • Desktop AI"
        );

        rodape.getStyleClass().add(
                "login-rodape"
        );

        formulario.setPadding(
                new Insets(28)
        );

        formulario.getChildren().addAll(
                titulo,
                descricao,
                campoEmailBox,
                campoSenhaBox,
                mensagemErro,
                botaoEntrar,
                linha,
                botaoCriarConta,
                rodape
        );

        return formulario;
    }

    private VBox construirCampo(
            String nome,
            javafx.scene.control.Control campo
    ) {

        VBox box = new VBox(7);

        Label label = new Label(nome);

        label.getStyleClass().add(
                "campo-label"
        );

        campo.getStyleClass().add(
                "campo-login"
        );

        box.getChildren().addAll(
                label,
                campo
        );

        return box;
    }

    public BorderPane getRaiz() {
        return raiz;
    }

    public TextField getCampoEmail() {
        return campoEmail;
    }

    public PasswordField getCampoSenha() {
        return campoSenha;
    }

    public Button getBotaoEntrar() {
        return botaoEntrar;
    }

    public Button getBotaoCriarConta() {
        return botaoCriarConta;
    }

    public void mostrarErro(String mensagem) {

        mensagemErro.setText(
                mensagem
        );

        mensagemErro.setVisible(true);
        mensagemErro.setManaged(true);
    }

    public void limparErro() {

        mensagemErro.setText("");

        mensagemErro.setVisible(false);
        mensagemErro.setManaged(false);
    }
}