package com.example.chatdesktop.service;

public class PasswordStrengthService {

    public enum Forca {
        VAZIA,
        FRACA,
        MEDIA,
        FORTE
    }

    public static Forca avaliar(String senha) {

        if (senha == null || senha.isEmpty()) {
            return Forca.VAZIA;
        }

        int pontos = 0;

        // 8 caracteres ou mais
        if (senha.length() >= 8) {
            pontos++;
        }

        // Letra maiúscula
        if (senha.matches(".*[A-Z].*")) {
            pontos++;
        }

        // Número
        if (senha.matches(".*\\d.*")) {
            pontos++;
        }

        // Símbolo
        if (senha.matches(".*[^a-zA-Z0-9].*")) {
            pontos++;
        }

        // Senhas muito previsíveis continuam sendo fracas
        if (senhaPrevisivel(senha)) {
            return Forca.FRACA;
        }

        if (pontos <= 1) {
            return Forca.FRACA;
        }

        if (pontos <= 3) {
            return Forca.MEDIA;
        }

        return Forca.FORTE;
    }

    private static boolean senhaPrevisivel(String senha) {

        String valor = senha.toLowerCase();

        String[] comuns = {
                "password",
                "passw0rd",
                "qwerty",
                "letmein",
                "welcome",
                "admin",
                "iloveyou",
                "monkey",
                "dragon",
                "abc123",
                "111111",
                "123123",
                "123456",
                "senha123"
        };

        for (String comum : comuns) {

            if (valor.contains(comum)) {
                return true;
            }
        }

        // Detecta caracteres repetidos
        if (valor.matches("(.)\\1{3,}")) {
            return true;
        }

        // Sequências muito simples
        String[] sequencias = {
                "012345",
                "123456",
                "234567",
                "345678",
                "456789",
                "abcdef",
                "qwerty",
                "asdfgh"
        };

        for (String sequencia : sequencias) {

            if (valor.contains(sequencia)) {
                return true;
            }
        }

        return false;
    }
}