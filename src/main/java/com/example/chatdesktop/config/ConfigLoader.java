package com.example.chatdesktop.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Responsável por carregar configurações sensíveis (como a chave da API)
 * a partir do arquivo "config.properties", que não é versionado no Git.
 */
public class ConfigLoader {

    private static final String ARQUIVO_CONFIG = "config.properties";
    private static final String CHAVE_API = "groq.api.key";

    public static String carregarApiKey() {
        Properties props = new Properties();

        try (InputStream input = new FileInputStream(ARQUIVO_CONFIG)) {
            props.load(input);
            return props.getProperty(CHAVE_API);
        } catch (Exception e) {
            return null;
        }
    }
}