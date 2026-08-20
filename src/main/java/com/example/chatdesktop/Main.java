package com.example.chatdesktop;

import com.example.chatdesktop.controller.ChatController;
import com.example.chatdesktop.view.ChatView;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        ChatView view = new ChatView();
        new ChatController(view);

        // camada que cobre tudo no início, para a animação de cores
        Region overlayCor = new Region();
        overlayCor.setStyle("-fx-background-color: #0a0a0a;");

        // imagem do meme, centralizada por cima da cor
        ImageView imagem = carregarImagemNaverdachi();
        imagem.setOpacity(0);

        StackPane camadaTransicao = new StackPane(overlayCor, imagem);

        StackPane raizComOverlay = new StackPane();
        raizComOverlay.getChildren().addAll(view.getRaiz(), camadaTransicao);

        Scene cena = new Scene(raizComOverlay, 1000, 700);

        cena.getStylesheets().add(
                getClass().getResource("/com/example/chatdesktop/css/chat.css")
                        .toExternalForm()
        );

        stage.setTitle("NeoChat");
        stage.setScene(cena);
        stage.show();

        animarTransicaoInicial(raizComOverlay, camadaTransicao, overlayCor, imagem);
    }

    /**
     * Tenta carregar a imagem do "naverdachi" salva pelo usuário em
     * /com/example/chatdesktop/img/naverdachi.png. Se o arquivo não
     * existir ainda, a animação continua normalmente sem a imagem.
     */
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
     * Animação de abertura: o fundo passa de PRETO para BRANCO e
     * depois para VERMELHO; no meio do caminho a imagem do
     * "naverdachi" aparece e some; por fim toda a camada some,
     * revelando o app.
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

        // a imagem aparece já no início (sobre o preto/branco) e
        // some pouco antes da cor terminar de virar vermelho
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

        // cor de fundo e imagem tocam ao mesmo tempo,
        // depois pausa e a camada inteira desaparece
        var corEImagemJuntas = new javafx.animation.ParallelTransition(
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