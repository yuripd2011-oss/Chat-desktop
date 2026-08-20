package com.example.chatdesktop.view;

import com.example.chatdesktop.model.ChatMessage;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Duration;

/**
 * Responsável apenas por montar e atualizar a interface gráfica.
 *
 * Tema visual: preto e branco cromático.
 *
 * Não contém lógica de negócio.
 */
public class ChatView {

    private final BorderPane raiz = new BorderPane();

    private final VBox mensagens = new VBox(15);

    private final VBox listaHistorico = new VBox(8);

    private final TextField campoMensagem = new TextField();

    private final Button botaoEnviar = new Button("Enviar");

    private final Button botaoNovaConversa =
            new Button("+ Nova conversa");

    private final Label status =
            new Label("● Conectado à Groq");

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

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

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

        Label titulo = new Label("Neo");

        titulo.getStyleClass().add("titulo");

        Label subtitulo =
                new Label("sua IA sempre por perto");

        subtitulo.getStyleClass().add("subtitulo");

        status.getStyleClass().add("status-conectado");

        informacoes.getChildren().addAll(
                titulo,
                subtitulo,
                status
        );

        Region espacador = new Region();

        HBox.setHgrow(
                espacador,
                Priority.ALWAYS
        );

        botaoNovaConversa
                .getStyleClass()
                .add("botao-nova-conversa");

        topo.getChildren().addAll(
                informacoes,
                espacador,
                botaoNovaConversa
        );

        return topo;
    }

    private VBox construirBarraLateral() {

        VBox barra = new VBox(10);

        barra.getStyleClass().add("barra-lateral");

        barra.setPadding(
                new Insets(18, 12, 18, 12)
        );

        barra.setPrefWidth(220);

        Label titulo =
                new Label("Histórico");

        titulo.getStyleClass()
                .add("titulo-historico");

        listaHistorico.setFillWidth(true);

        ScrollPane scroll =
                new ScrollPane(listaHistorico);

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.getStyleClass()
                .add("area-historico");

        VBox.setVgrow(
                scroll,
                Priority.ALWAYS
        );

        barra.getChildren().addAll(
                titulo,
                scroll
        );

        return barra;
    }

    private HBox construirEntrada() {

        campoMensagem.setPromptText(
                "Digite sua mensagem..."
        );

        campoMensagem
                .getStyleClass()
                .add("campo-mensagem");

        botaoEnviar
                .getStyleClass()
                .add("botao-enviar");

        HBox entrada = new HBox(10);

        entrada.getStyleClass().add("rodape");

        entrada.setPadding(new Insets(15));

        entrada.setAlignment(Pos.CENTER);

        HBox.setHgrow(
                campoMensagem,
                Priority.ALWAYS
        );

        entrada.getChildren().addAll(
                campoMensagem,
                botaoEnviar
        );

        return entrada;
    }

    /**
     * Adiciona uma mensagem ao chat.
     *
     * Mensagens do usuário:
     * - Bolha normal.
     * - Sem botão de copiar.
     *
     * Mensagens do assistente:
     * - Botão de copiar dentro da própria bolha.
     */
    public void adicionarMensagem(ChatMessage mensagem) {

        Label avatar =
                new Label(
                        mensagem.isDeUsuario()
                                ? "🧑"
                                : "🤖"
                );

        avatar.getStyleClass().addAll(
                "avatar",
                mensagem.isDeUsuario()
                        ? "avatar-usuario"
                        : "avatar-assistente"
        );

        Label horario =
                new Label(
                        mensagem.getHorarioFormatado()
                );

        horario.getStyleClass().add("horario");

        VBox blocoTextoHorario =
                new VBox(2);

        blocoTextoHorario.setAlignment(
                mensagem.isDeUsuario()
                        ? Pos.CENTER_RIGHT
                        : Pos.CENTER_LEFT
        );

        /*
         * =====================================================
         * MENSAGEM DO USUÁRIO
         * =====================================================
         */
        if (mensagem.isDeUsuario()) {

            Label texto =
                    new Label(mensagem.getTexto());

            texto.setWrapText(true);

            texto.setMaxWidth(650);

            texto.setPadding(
                    new Insets(12, 16, 12, 16)
            );

            texto.getStyleClass().add(
                    "bolha-usuario"
            );

            blocoTextoHorario
                    .getChildren()
                    .add(texto);

        }

        /*
         * =====================================================
         * MENSAGEM DO ASSISTENTE
         * =====================================================
         *
         * A diferença importante é que agora usamos um
         * StackPane.
         *
         * O texto e o botão ficam dentro da mesma bolha.
         */
        else {

            StackPane bolhaAssistente =
                    new StackPane();

            bolhaAssistente
                    .getStyleClass()
                    .add("bolha-assistente");

            bolhaAssistente.setMaxWidth(650);

            /*
             * Texto da mensagem.
             */
            Label texto =
                    new Label(mensagem.getTexto());

            texto.setWrapText(true);

            texto.setMaxWidth(650);

            /*
             * Espaço extra no lado direito e embaixo
             * para o botão não ficar sobre o texto.
             */
            texto.setPadding(
                    new Insets(
                            12,
                            42,
                            36,
                            16
                    )
            );

            texto.getStyleClass()
                    .add("texto-assistente");

            /*
             * Alinha o texto no canto superior esquerdo.
             */
            StackPane.setAlignment(
                    texto,
                    Pos.TOP_LEFT
            );

            /*
             * Botão de copiar.
             */
            Button botaoCopiar =
                    criarBotaoCopiar(
                            mensagem.getTexto()
                    );

            /*
             * Coloca o botão dentro da bolha,
             * no canto inferior direito.
             */
            StackPane.setAlignment(
                    botaoCopiar,
                    Pos.BOTTOM_RIGHT
            );

            StackPane.setMargin(
                    botaoCopiar,
                    new Insets(
                            0,
                            6,
                            6,
                            0
                    )
            );

            /*
             * Os dois ficam dentro da mesma bolha.
             */
            bolhaAssistente
                    .getChildren()
                    .addAll(
                            texto,
                            botaoCopiar
                    );

            blocoTextoHorario
                    .getChildren()
                    .add(bolhaAssistente);
        }

        /*
         * Horário fica abaixo da bolha.
         */
        blocoTextoHorario
                .getChildren()
                .add(horario);

        /*
         * =====================================================
         * LINHA DA MENSAGEM
         * =====================================================
         */
        HBox linha = new HBox(8);

        linha.setAlignment(
                Pos.BOTTOM_CENTER
        );

        if (mensagem.isDeUsuario()) {

            linha.setAlignment(
                    Pos.CENTER_RIGHT
            );

            linha.getChildren().addAll(
                    blocoTextoHorario,
                    avatar
            );

        } else {

            linha.setAlignment(
                    Pos.CENTER_LEFT
            );

            linha.getChildren().addAll(
                    avatar,
                    blocoTextoHorario
            );
        }

        mensagens.getChildren().add(linha);
    }

    /**
     * Cria o botão de copiar.
     *
     * Tamanho:
     * - 24x24 pixels.
     *
     * Não possui texto.
     * Possui apenas o ícone.
     */
    private Button criarBotaoCopiar(String texto) {

        Button botao = new Button();

        /*
         * =====================================================
         * TAMANHO DO BOTÃO
         * =====================================================
         */
        botao.setMinSize(24, 24);

        botao.setPrefSize(24, 24);

        botao.setMaxSize(24, 24);

        /*
         * Não existe texto.
         */
        botao.setText("");

        /*
         * Ícone inicial.
         */
        botao.setGraphic(
                criarIconeCopiar(false)
        );

        botao.getStyleClass()
                .add("botao-copiar");

        botao.setTooltip(
                new Tooltip("Copiar")
        );

        /*
         * =====================================================
         * CLIQUE
         * =====================================================
         */
        botao.setOnAction(event -> {

            Clipboard clipboard =
                    Clipboard.getSystemClipboard();

            ClipboardContent content =
                    new ClipboardContent();

            content.putString(texto);

            clipboard.setContent(content);

            /*
             * Troca o ícone para "copiado".
             */
            Group iconeCopiado =
                    criarIconeCopiar(true);

            botao.setGraphic(
                    iconeCopiado
            );

            botao.getStyleClass()
                    .add("copiado");

            botao.setTooltip(
                    new Tooltip("Copiado")
            );

            /*
             * Animação de entrada.
             */
            iconeCopiado.setOpacity(0);

            FadeTransition fade =
                    new FadeTransition(
                            Duration.millis(140),
                            iconeCopiado
                    );

            fade.setFromValue(0);

            fade.setToValue(1);

            ScaleTransition escala =
                    new ScaleTransition(
                            Duration.millis(160),
                            iconeCopiado
                    );

            escala.setFromX(0.8);

            escala.setFromY(0.8);

            escala.setToX(1.0);

            escala.setToY(1.0);

            fade.play();

            escala.play();

            /*
             * Depois de 1,5 segundos,
             * volta para o ícone de copiar.
             */
            PauseTransition pausa =
                    new PauseTransition(
                            Duration.seconds(1.5)
                    );

            pausa.setOnFinished(e -> {

                botao.setGraphic(
                        criarIconeCopiar(false)
                );

                botao.getStyleClass()
                        .remove("copiado");

                botao.setTooltip(
                        new Tooltip("Copiar")
                );
            });

            pausa.play();
        });

        return botao;
    }

    /**
     * Cria o ícone de copiar.
     *
     * O desenho é feito com SVGPath.
     */
    private Group criarIconeCopiar(
            boolean mostrarCheck
    ) {

        /*
         * =====================================================
         * PAPEL DE TRÁS
         * =====================================================
         */
        SVGPath papelTras =
                new SVGPath();

        papelTras.setContent(
                "M 19 6 " +
                        "H 61 " +
                        "C 64 6 66 8 66 11"
        );

        SVGPath lateralTras =
                new SVGPath();

        lateralTras.setContent(
                "M 19 6 " +
                        "C 14 6 11 9 11 14 " +
                        "V 61"
        );

        /*
         * =====================================================
         * PAPEL DA FRENTE
         * =====================================================
         */
        SVGPath papelFrente =
                new SVGPath();

        papelFrente.setContent(
                "M 30 18 " +
                        "H 79 " +
                        "C 82 18 84 20 84 23 " +
                        "V 78 " +
                        "C 84 81 82 83 79 83 " +
                        "H 30 " +
                        "C 27 83 25 81 25 78 " +
                        "V 23 " +
                        "C 25 20 27 18 30 18 Z"
        );

        configurarLinha(
                papelTras
        );

        configurarLinha(
                lateralTras
        );

        configurarLinha(
                papelFrente
        );

        Group grupo =
                new Group(
                        papelTras,
                        lateralTras,
                        papelFrente
                );

        /*
         * Ícone menor.
         *
         * Antes:
         * 0.25
         *
         * Agora:
         * 0.20
         */
        grupo.setScaleX(0.20);

        grupo.setScaleY(0.20);

        /*
         * =====================================================
         * CHECK
         * =====================================================
         */
        if (mostrarCheck) {

            SVGPath check =
                    new SVGPath();

            check.setContent(
                    "M 42 51 " +
                            "L 50 59 " +
                            "L 68 39"
            );

            configurarLinha(check);

            check.setStrokeWidth(7);

            grupo.getChildren()
                    .add(check);
        }

        return grupo;
    }

    /**
     * Configuração visual dos traços.
     */
    private void configurarLinha(
            Shape forma
    ) {

        forma.setFill(
                Color.TRANSPARENT
        );

        forma.setStroke(
                Color.web("#8F8F8F")
        );

        forma.setStrokeWidth(6);

        forma.setStrokeLineCap(
                StrokeLineCap.ROUND
        );

        forma.setStrokeLineJoin(
                StrokeLineJoin.ROUND
        );
    }

    /**
     * Remove todas as mensagens.
     */
    public void limparMensagens() {

        mensagens
                .getChildren()
                .clear();
    }

    /**
     * Adiciona um item clicável
     * na barra lateral de histórico.
     */
    public Button adicionarItemHistorico(
            String titulo
    ) {

        Button item =
                new Button(titulo);

        item.getStyleClass()
                .add("item-historico");

        item.setMaxWidth(
                Double.MAX_VALUE
        );

        listaHistorico
                .getChildren()
                .add(item);

        return item;
    }

    public void definirStatusPensando() {

        status.setText(
                "● Tecendo a resposta..."
        );

        status.getStyleClass().setAll(
                "status-pensando"
        );
    }

    public void definirStatusConectado() {

        status.setText(
                "● Conectado à Groq"
        );

        status.getStyleClass().setAll(
                "status-conectado"
        );
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
}
