package com.example.chatdesktop;

import com.example.chatdesktop.controller.ChatController;
import com.example.chatdesktop.view.ChatView;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends Application {

    private static final String CSS_ESCURO = "/com/example/chatdesktop/css/chat-dark.css";
    private static final String CSS_CLARO = "/com/example/chatdesktop/css/chat-light.css";

    @Override
    public void start(Stage stage) {

        ChatView view = new ChatView();
        ChatController controller = new ChatController(view);

        StackPane raizComOverlay = new StackPane();
        raizComOverlay.getChildren().add(view.getRaiz());

        Scene cena = new Scene(raizComOverlay, 1000, 700);

        cena.getStylesheets().add(
                getClass().getResource(CSS_ESCURO).toExternalForm()
        );

        configurarAlternanciaDeTema(cena, view);
        configurarAtalhosDeTeclado(cena, view, controller);

        // toca a introdução também sempre que "Nova conversa" for clicado,
        // além do clique já tratado pelo ChatController
        view.getBotaoNovaConversa().addEventHandler(
                ActionEvent.ACTION,
                event -> tocarIntroducao(raizComOverlay)
        );

        stage.setTitle("NeoChat");
        stage.setScene(cena);
        stage.show();

        tocarIntroducao(raizComOverlay);
    }

    /**
     * Cria a camada de introdução (cores + imagem do naverdachi),
     * adiciona por cima da tela atual e dispara a animação.
     */
    private void tocarIntroducao(StackPane container) {

        Region overlayCor = new Region();
        overlayCor.setStyle("-fx-background-color: #0a0a0a;");

        ImageView imagem = carregarImagemNaverdachi();
        imagem.setOpacity(0);

        StackPane camadaTransicao = new StackPane(overlayCor, imagem);
        container.getChildren().add(camadaTransicao);

        animarTransicaoInicial(container, camadaTransicao, overlayCor, imagem);
    }

    private ImageView carregarImagemNaverdachi() {

        ImageView imageView = new ImageView();
        imageView.setFitWidth(220);
        imageView.setPreserveRatio(true);

        var recurso = getClass().getResource("/com/example/chatdesktop/img/naverdachi.png");

        if (recurso != null) {
            imageView.setImage(new Image(recurso.toExternalForm()));
        }

        return imageView;
    }

    /**
     * Liga o botão de tema à troca entre o CSS escuro e o claro.
     */
    private void configurarAlternanciaDeTema(Scene cena, ChatView view) {

        AtomicBoolean temaEscuro = new AtomicBoolean(true);

        view.getBotaoTema().setOnAction(event -> {

            cena.getStylesheets().clear();

            if (temaEscuro.get()) {
                cena.getStylesheets().add(getClass().getResource(CSS_CLARO).toExternalForm());
                view.getBotaoTema().setText("☀ Tema");
            } else {
                cena.getStylesheets().add(getClass().getResource(CSS_ESCURO).toExternalForm());
                view.getBotaoTema().setText("🌙 Tema");
            }

            temaEscuro.set(!temaEscuro.get());
        });
    }

    /**
     * Atalhos de teclado:
     * Enter para enviar já é tratado no ChatView/ChatController.
     * Ctrl+N: nova conversa.
     * Ctrl+L: limpar o campo de mensagem.
     */
    private void configurarAtalhosDeTeclado(Scene cena, ChatView view, ChatController controller) {

        cena.getAccelerators().put(
                new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN),
                controller::iniciarNovaConversa
        );

        cena.getAccelerators().put(
                new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN),
                () -> view.getCampoMensagem().clear()
        );
    }

    /**
     * Animação: o fundo passa de PRETO para BRANCO e depois para
     * VERMELHO; no meio do caminho a imagem do "naverdachi" aparece
     * e some; por fim toda a camada some, revelando o app.
     */
    private void animarTransicaoInicial(
            StackPane container,
            StackPane camadaTransicao,
            Region overlayCor,
            ImageView imagem
    ) {

        Color preto = Color.web("#0a0a0a");
        Color branco = Color.web("#f5f5f5");
        Color vermelho = Color.web("#e0212b");

        ObjectProperty<Color> corAtual = new SimpleObjectProperty<>(preto);

        corAtual.addListener((obs, antiga, nova) -> {
            String hex = String.format(
                    "#%02x%02x%02x",
                    (int) (nova.getRed() * 255),
                    (int) (nova.getGreen() * 255),
                    (int) (nova.getBlue() * 255)
            );
            overlayCor.setStyle("-fx-background-color: " + hex + ";");
        });

        Timeline mudancaDeCor = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(corAtual, preto)),
                new KeyFrame(Duration.seconds(1.0), new KeyValue(corAtual, branco)),
                new KeyFrame(Duration.seconds(2.0), new KeyValue(corAtual, vermelho))
        );

        FadeTransition imagemAparece = new FadeTransition(Duration.seconds(0.5), imagem);
        imagemAparece.setFromValue(0.0);
        imagemAparece.setToValue(1.0);

        PauseTransition imagemFicaVisivel = new PauseTransition(Duration.seconds(1.0));

        FadeTransition imagemSome = new FadeTransition(Duration.seconds(0.5), imagem);
        imagemSome.setFromValue(1.0);
        imagemSome.setToValue(0.0);

        SequentialTransition sequenciaImagem = new SequentialTransition(
                imagemAparece,
                imagemFicaVisivel,
                imagemSome
        );

        PauseTransition pausa = new PauseTransition(Duration.seconds(0.4));

        FadeTransition camadaDesaparece = new FadeTransition(Duration.seconds(0.6), camadaTransicao);
        camadaDesaparece.setFromValue(1.0);
        camadaDesaparece.setToValue(0.0);
        camadaDesaparece.setOnFinished(event -> container.getChildren().remove(camadaTransicao));

        ParallelTransition corEImagemJuntas = new ParallelTransition(
                mudancaDeCor,
                sequenciaImagem
        );

        SequentialTransition sequenciaCompleta = new SequentialTransition(
                corEImagemJuntas,
                pausa,
                camadaDesaparece
        );

        sequenciaCompleta.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}