package com.example.chatdesktop.service;

import com.example.chatdesktop.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioService {

    private final List<Usuario> usuarios = new ArrayList<>();

    public UsuarioService() {

        // Usuário inicial para teste
        usuarios.add(
                new Usuario(
                        "Administrador",
                        "admin@neochat.com",
                        "NeoChat@2026"
                )
        );
    }

    public boolean cadastrar(
            String nome,
            String email,
            String senha
    ) {

        if (emailExiste(email)) {
            return false;
        }

        usuarios.add(
                new Usuario(nome, email, senha)
        );

        return true;
    }

    public boolean autenticar(
            String email,
            String senha
    ) {

        for (Usuario usuario : usuarios) {

            if (
                    usuario.getEmail().equalsIgnoreCase(email)
                            &&
                            usuario.getSenha().equals(senha)
            ) {
                return true;
            }
        }

        return false;
    }

    public boolean emailExiste(String email) {

        for (Usuario usuario : usuarios) {

            if (
                    usuario.getEmail()
                            .equalsIgnoreCase(email)
            ) {
                return true;
            }
        }

        return false;
    }
}