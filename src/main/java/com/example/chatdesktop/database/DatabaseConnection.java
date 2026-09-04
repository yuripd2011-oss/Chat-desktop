package com.example.chatdesktop.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:sqlite:data/neochat.db";

    public static Connection conectar()
            throws SQLException {

        return DriverManager.getConnection(URL);
    }
}