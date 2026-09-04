package com.example.chatdesktop.database;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void inicializar() {

        File pasta = new File("data");

        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        String sql = """
                CREATE TABLE IF NOT EXISTS usuarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    senha TEXT NOT NULL
                )
                """;

        try (
                Connection conexao =
                        DatabaseConnection.conectar();

                Statement statement =
                        conexao.createStatement()
        ) {

            statement.execute(sql);

            System.out.println(
                    "[NeoChat] Banco SQLite inicializado."
            );

        } catch (SQLException erro) {

            System.err.println(
                    "[NeoChat][ERRO] Não foi possível inicializar o banco."
            );

            erro.printStackTrace();
        }
    }
}