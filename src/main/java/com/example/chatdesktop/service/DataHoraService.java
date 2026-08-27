package com.example.chatdesktop.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Responde perguntas sobre data e hora atuais direto pelo relógio
 * do computador, sem precisar chamar a Groq (mais rápido e sempre certo).
 */
public class DataHoraService {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    /**
     * @return a resposta pronta se a pergunta for sobre data/hora,
     *         ou null se não for (para o controller seguir o fluxo normal).
     */
    public String responderSeForPerguntaDeDataHora(String pergunta) {

        String texto = pergunta.toLowerCase();

        boolean perguntouHoras = texto.contains("que horas")
                || texto.contains("horas são")
                || texto.contains("hora atual")
                || texto.contains("horário atual");

        boolean perguntouData = texto.contains("que dia")
                || texto.contains("dia é hoje")
                || texto.contains("hoje é")
                || texto.contains("data de hoje")
                || texto.contains("data atual");

        if (!perguntouHoras && !perguntouData) {
            return null;
        }

        LocalDateTime agora = LocalDateTime.now();
        StringBuilder resposta = new StringBuilder();

        if (perguntouData) {
            resposta.append("📅 Hoje é ").append(agora.format(FORMATO_DATA)).append(".\n");
        }

        if (perguntouHoras) {
            resposta.append("🕒 Agora são ").append(agora.format(FORMATO_HORA)).append(".");
        }

        return resposta.toString().trim();
    }
}