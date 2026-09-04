package com.example.chatdesktop.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class CadastroView {

    private final BorderPane raiz = new BorderPane();

    private final TextField campoNome = new TextField();
    private final TextField campoEmail = new TextField();

    private final PasswordField campoSenha =
            new PasswordField();

    private final PasswordField campoConfirmarSenha =
            new PasswordField();

    private final Button botaoCadastrar =
            new Button("Criar minha conta");

    private final Button botaoVoltar =
            new Button("Já tenho uma conta");

    private final Label mensagemErro =
            new Label();

    private final Label forcaSenha =
            new Label();

    private final Label requisitosSenha =
            new Label();

    public CadastroView() {
        montarInterface();
    }

    private void montarInterface() {

        raiz.getStyleClass().add("login-root");

        VBox container = new VBox(18);

        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(430);
        container.setPadding(new Insets(35));

        Label simbolo = new Label("✦");
        simbolo.getStyleClass().add("logo-simbolo");

        Label titulo = new Label("Criar sua conta");
        titulo.getStyleClass().add("login-titulo");

        Label descricao = new Label(
                "Crie sua conta para começar a usar o NeoChat."
        );

        descricao.getStyleClass().add(
                "login-descricao"
        );

        campoNome.setPromptText("Seu nome");

        campoEmail.setPromptText(
                "seu@email.com"
        );

        campoSenha.setPromptText(
                "Crie uma senha forte"
        );

        campoConfirmarSenha.setPromptText(
                "Digite a senha novamente"
        );

        configurarCampo(campoNome);
        configurarCampo(campoEmail);
        configurarCampo(campoSenha);
        configurarCampo(campoConfirmarSenha);

        VBox nomeBox =
                criarCampo(
                        "Nome",
                        campoNome
                );

        VBox emailBox =
                criarCampo(
                        "E-mail",
                        campoEmail
                );

        VBox senhaBox =
                criarCampo(
                        "Senha",
                        campoSenha
                );

        VBox confirmarBox =
                criarCampo(
                        "Confirmar senha",
                        campoConfirmarSenha
                );

        forcaSenha.getStyleClass().add(
                "forca-senha"
        );

        requisitosSenha.getStyleClass().add(
                "requisitos-senha"
        );

        mensagemErro.getStyleClass().add(
                "mensagem-erro"
        );

        mensagemErro.setVisible(false);
        mensagemErro.setManaged(false);

        botaoCadastrar.getStyleClass().add(
                "botao-entrar"
        );

        botaoCadastrar.setMaxWidth(
                Double.MAX_VALUE
        );

        botaoVoltar.getStyleClass().add(
                "botao-voltar"
        );

        botaoVoltar.setMaxWidth(
                Double.MAX_VALUE
        );

        container.getChildren().addAll(
                simbolo,
                titulo,
                descricao,
                nomeBox,
                emailBox,
                senhaBox,
                forcaSenha,
                requisitosSenha,
                confirmarBox,
                mensagemErro,
                botaoCadastrar,
                botaoVoltar
        );

        raiz.setCenter(container);
    }

    private VBox criarCampo(
            String nome,
            TextField campo
    ) {

        VBox box = new VBox(7);

        Label label = new Label(nome);

        label.getStyleClass().add(
                "campo-label"
        );

        box.getChildren().addAll(
                label,
                campo
        );

        return box;
    }

    private void configurarCampo(
            TextField campo
    ) {

        campo.getStyleClass().add(
                "campo-login"
        );

        campo.setMaxWidth(
                Double.MAX_VALUE
        );
    }

    public BorderPane getRaiz() {
        return raiz;
    }

    public TextField getCampoNome() {
        return campoNome;
    }

    public TextField getCampoEmail() {
        return campoEmail;
    }

    public PasswordField getCampoSenha() {
        return campoSenha;
    }

    public PasswordField getCampoConfirmarSenha() {
        return campoConfirmarSenha;
    }

    public Button getBotaoCadastrar() {
        return botaoCadastrar;
    }

    public Button getBotaoVoltar() {
        return botaoVoltar;
    }

    public Label getForcaSenha() {
        return forcaSenha;
    }

    public Label getRequisitosSenha() {
        return requisitosSenha;
    }

    public void mostrarErro(String mensagem) {

        mensagemErro.setText(mensagem);

        mensagemErro.setVisible(true);
        mensagemErro.setManaged(true);
    }

    public void limparErro() {

        mensagemErro.setText("");

        mensagemErro.setVisible(false);
        mensagemErro.setManaged(false);
    }
}