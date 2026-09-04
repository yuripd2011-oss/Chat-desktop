package com.example.chatdesktop.controller;

import com.example.chatdesktop.service.PasswordStrengthService;
import com.example.chatdesktop.service.UsuarioService;
import com.example.chatdesktop.view.CadastroView;

public class CadastroController {

    private final CadastroView view;
    private final UsuarioService usuarioService;
    private final Runnable aoVoltarLogin;

    public CadastroController(
            CadastroView view,
            UsuarioService usuarioService,
            Runnable aoVoltarLogin
    ) {

        this.view = view;
        this.usuarioService = usuarioService;
        this.aoVoltarLogin = aoVoltarLogin;

        configurarEventos();
    }

    private void configurarEventos() {

        view.getCampoSenha()
                .textProperty()
                .addListener(
                        (obs, antigo, novo) ->
                                atualizarForcaSenha(novo)
                );

        view.getBotaoCadastrar()
                .setOnAction(
                        event -> cadastrar()
                );

        view.getBotaoVoltar()
                .setOnAction(
                        event -> aoVoltarLogin.run()
                );

        view.getCampoConfirmarSenha()
                .setOnAction(
                        event -> cadastrar()
                );
    }

    private void atualizarForcaSenha(String senha) {

        PasswordStrengthService.Forca forca =
                PasswordStrengthService.avaliar(senha);

        /*
         * Remove as classes antigas antes de
         * adicionar a classe correspondente.
         */
        view.getForcaSenha()
                .getStyleClass()
                .setAll("forca-senha");

        switch (forca) {

            case VAZIA:

                view.getForcaSenha()
                        .setText("");

                break;

            case FRACA:

                view.getForcaSenha()
                        .setText("🔴 Fraca");

                view.getForcaSenha()
                        .getStyleClass()
                        .add("forca-fraca");

                break;

            case MEDIA:

                view.getForcaSenha()
                        .setText("🟠 Média");

                view.getForcaSenha()
                        .getStyleClass()
                        .add("forca-media");

                break;

            case FORTE:

                view.getForcaSenha()
                        .setText("🟢 Forte");

                view.getForcaSenha()
                        .getStyleClass()
                        .add("forca-forte");

                break;
        }

        atualizarRequisitos(senha);
    }

    private void atualizarRequisitos(String senha) {

        boolean tamanho =
                senha.length() >= 8;

        boolean maiuscula =
                senha.matches(".*[A-Z].*");

        boolean numero =
                senha.matches(".*\\d.*");

        boolean simbolo =
                senha.matches(".*[^a-zA-Z0-9].*");

        StringBuilder texto =
                new StringBuilder();

        texto.append(
                tamanho ? "✓ " : "○ "
        ).append("8 caracteres ou mais\n");

        texto.append(
                maiuscula ? "✓ " : "○ "
        ).append("Letra maiúscula\n");

        texto.append(
                numero ? "✓ " : "○ "
        ).append("Número\n");

        texto.append(
                simbolo ? "✓ " : "○ "
        ).append("Símbolo");

        view.getRequisitosSenha()
                .setText(
                        texto.toString()
                );
    }

    private void cadastrar() {

        view.limparErro();

        String nome =
                view.getCampoNome()
                        .getText()
                        .trim();

        String email =
                view.getCampoEmail()
                        .getText()
                        .trim();

        String senha =
                view.getCampoSenha()
                        .getText();

        String confirmar =
                view.getCampoConfirmarSenha()
                        .getText();

        // Nome
        if (nome.isEmpty()) {

            view.mostrarErro(
                    "Digite seu nome."
            );

            view.getCampoNome()
                    .requestFocus();

            return;
        }

        // E-mail vazio
        if (email.isEmpty()) {

            view.mostrarErro(
                    "Digite seu e-mail."
            );

            view.getCampoEmail()
                    .requestFocus();

            return;
        }

        // E-mail inválido
        if (!email.contains("@")
                || !email.contains(".")) {

            view.mostrarErro(
                    "Digite um e-mail válido."
            );

            view.getCampoEmail()
                    .requestFocus();

            return;
        }

        // Senha vazia
        if (senha.isEmpty()) {

            view.mostrarErro(
                    "Digite uma senha."
            );

            view.getCampoSenha()
                    .requestFocus();

            return;
        }

        // Senha precisa ser forte
        if (PasswordStrengthService.avaliar(senha)
                != PasswordStrengthService.Forca.FORTE) {

            view.mostrarErro(
                    "Sua senha precisa ser forte."
            );

            view.getCampoSenha()
                    .requestFocus();

            return;
        }

        // Confirmação vazia
        if (confirmar.isEmpty()) {

            view.mostrarErro(
                    "Confirme sua senha."
            );

            view.getCampoConfirmarSenha()
                    .requestFocus();

            return;
        }

        // Senhas diferentes
        if (!senha.equals(confirmar)) {

            view.mostrarErro(
                    "As senhas não coincidem."
            );

            view.getCampoConfirmarSenha()
                    .requestFocus();

            return;
        }

        // E-mail já cadastrado
        if (usuarioService.emailExiste(email)) {

            view.mostrarErro(
                    "Este e-mail já está cadastrado."
            );

            view.getCampoEmail()
                    .requestFocus();

            return;
        }

        // Cria usuário
        boolean cadastrado =
                usuarioService.cadastrar(
                        nome,
                        email,
                        senha
                );

        if (!cadastrado) {

            view.mostrarErro(
                    "Não foi possível criar a conta."
            );

            return;
        }

        System.out.println(
                "[NeoChat] Conta criada com sucesso: "
                        + email
        );

        /*
         * Depois de cadastrar,
         * volta para a tela de login.
         */
        aoVoltarLogin.run();
    }
}