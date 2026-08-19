package com.example.chatdesktop;

import com.example.chatdesktop.controller.ChatController;
import com.example.chatdesktop.view.ChatView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        ChatView view = new ChatView();
        new ChatController(view);

        Scene cena = new Scene(view.getRaiz(), 1000, 700);

        cena.getStylesheets().add(
                getClass().getResource("/com/example/chatdesktop/css/chat.css")
                        .toExternalForm()
        );

        stage.setTitle("Chat Desktop 🕸");
        stage.setScene(cena);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}