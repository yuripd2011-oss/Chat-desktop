package com.example.chatdesktop.controller;

import com.example.chatdesktop.service.UsuarioService;
import com.example.chatdesktop.view.LoginView;

public class LoginController {

    private final LoginView view;
    private final UsuarioService usuarioService;

    private final Runnable aoCriarConta;
    private final Runnable aoLogar;

    public LoginController(
            LoginView view,
            UsuarioService usuarioService,
            Runnable aoCriarConta,
            Runnable aoLogar
    ) {

        this.view = view;
        this.usuarioService = usuarioService;
        this.aoCriarConta = aoCriarConta;
        this.aoLogar = aoLogar;

        configurarEventos();
    }

    private void configurarEventos() {

        view.getBotaoEntrar()
                .setOnAction(
                        event -> realizarLogin()
                );

        view.getBotaoCriarConta()
                .setOnAction(
                        event -> aoCriarConta.run()
                );

        view.getCampoEmail()
                .setOnAction(
                        event ->
                                view.getCampoSenha()
                                        .requestFocus()
                );

        view.getCampoSenha()
                .setOnAction(
                        event -> realizarLogin()
                );
    }

    private void realizarLogin() {

        String email =
                view.getCampoEmail()
                        .getText()
                        .trim();

        String senha =
                view.getCampoSenha()
                        .getText();

        view.limparErro();

        if (email.isEmpty()) {

            view.mostrarErro(
                    "Digite seu e-mail."
            );

            view.getCampoEmail()
                    .requestFocus();

            return;
        }

        if (!email.contains("@")
                || !email.contains(".")) {

            view.mostrarErro(
                    "Digite um e-mail válido."
            );

            view.getCampoEmail()
                    .requestFocus();

            return;
        }

        if (senha.isEmpty()) {

            view.mostrarErro(
                    "Digite sua senha."
            );

            view.getCampoSenha()
                    .requestFocus();

            return;
        }

        boolean autenticado =
                usuarioService.autenticar(
                        email,
                        senha
                );

        if (!autenticado) {

            view.mostrarErro(
                    "E-mail ou senha incorretos."
            );

            view.getCampoSenha().clear();
            view.getCampoSenha()
                    .requestFocus();

            return;
        }

        System.out.println(
                "[NeoChat] Usuário autenticado: "
                        + email
        );

        aoLogar.run();
    }
}