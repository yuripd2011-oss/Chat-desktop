package com.example.chatdesktop;

import com.example.chatdesktop.controller.CadastroController;
import com.example.chatdesktop.controller.ChatController;
import com.example.chatdesktop.controller.LoginController;
import com.example.chatdesktop.service.UsuarioService;
import com.example.chatdesktop.view.CadastroView;
import com.example.chatdesktop.view.ChatView;
import com.example.chatdesktop.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    private Stage stage;

    /*
     * Uma única instância durante toda a execução.
     *
     * Assim, quando o usuário criar uma conta,
     * o LoginController conseguirá encontrar esse usuário.
     */
    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    public void start(Stage stage) {

        System.out.println("[NeoChat] Aplicação iniciando...");

        this.stage = stage;

        stage.setTitle("NeoChat");
        stage.setMinWidth(1000);
        stage.setMinHeight(650);

        mostrarLogin();

        stage.show();

        System.out.println("[NeoChat] Aplicação iniciada.");
    }

    private void mostrarLogin() {

        System.out.println("[NeoChat] Abrindo tela de login...");

        LoginView loginView = new LoginView();

        new LoginController(
                loginView,
                usuarioService,
                this::mostrarCadastro,
                this::mostrarChat
        );

        Scene cena = new Scene(
                loginView.getRaiz(),
                900,
                650
        );

        URL css = getClass().getResource(
                "/com/example/chatdesktop/css/login.css"
        );

        if (css == null) {

            System.err.println(
                    "[NeoChat][ERRO] login.css não encontrado!"
            );

            throw new IllegalStateException(
                    "Arquivo login.css não encontrado."
            );
        }

        cena.getStylesheets().add(
                css.toExternalForm()
        );

        stage.setScene(cena);

        stage.setWidth(900);
        stage.setHeight(650);

        stage.centerOnScreen();

        System.out.println(
                "[NeoChat] Tela de login carregada."
        );
    }

    private void mostrarCadastro() {

        System.out.println(
                "[NeoChat] Abrindo tela de cadastro..."
        );

        CadastroView cadastroView = new CadastroView();

        new CadastroController(
                cadastroView,
                usuarioService,
                this::mostrarLogin
        );

        Scene cena = new Scene(
                cadastroView.getRaiz(),
                900,
                700
        );

        URL css = getClass().getResource(
                "/com/example/chatdesktop/css/login.css"
        );

        if (css == null) {

            System.err.println(
                    "[NeoChat][ERRO] login.css não encontrado!"
            );

            throw new IllegalStateException(
                    "Arquivo login.css não encontrado."
            );
        }

        cena.getStylesheets().add(
                css.toExternalForm()
        );

        stage.setScene(cena);

        stage.setWidth(900);
        stage.setHeight(700);

        stage.centerOnScreen();

        System.out.println(
                "[NeoChat] Tela de cadastro carregada."
        );
    }

    private void mostrarChat() {

        System.out.println(
                "[NeoChat] Login realizado."
        );

        System.out.println(
                "[NeoChat] Abrindo ChatView..."
        );

        ChatView chatView = new ChatView();

        System.out.println(
                "[NeoChat] ChatView criada."
        );

        new ChatController(chatView);

        System.out.println(
                "[NeoChat] ChatController criado."
        );

        Scene cena = new Scene(
                chatView.getRaiz(),
                1200,
                750
        );

        URL css = getClass().getResource(
                "/com/example/chatdesktop/css/Chat.CSS"
        );

        if (css == null) {

            System.err.println(
                    "[NeoChat][ERRO] Chat.CSS não encontrado!"
            );

            throw new IllegalStateException(
                    "Arquivo Chat.CSS não encontrado."
            );
        }

        cena.getStylesheets().add(
                css.toExternalForm()
        );

        stage.setScene(cena);

        stage.setWidth(1200);
        stage.setHeight(750);

        stage.centerOnScreen();

        System.out.println(
                "[NeoChat] Chat aberto com sucesso."
        );
    }

    public static void main(String[] args) {

        System.out.println(
                "[NeoChat] Iniciando aplicação..."
        );

        launch(args);
    }
}