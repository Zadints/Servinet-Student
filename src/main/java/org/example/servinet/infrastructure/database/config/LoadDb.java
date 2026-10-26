package org.example.servinet.infrastructure.database.config;

import java.sql.*;
import java.util.Properties;

public class LoadDb {

    private static Connection conn;

    public static void startConnection(){

        try {
            String host = ConfigLoad.getYmlHost();
            int port = ConfigLoad.getYmlPort();
            String database = ConfigLoad.getYmlName();
            String username = ConfigLoad.getYmlUsername();
            String password = ConfigLoad.getYmlPassword();

            String url = String.format(
                    "jdbc:sqlserver://%s:%d;" +
                            "databaseName=%s;" +
                            "encrypt=true;" +
                            "trustServerCertificate=true",
                    host,
                    port,
                    database
            );
            // Agrega estos prints para verificar ambos valores en la consola
            System.out.println("Usuario: " + username);
            System.out.println("Contraseña es nula?: " + (password == null));

            java.util.Properties props = new java.util.Properties();
            props.setProperty("user", username);
// Si la contraseña es nula, le pasamos una cadena vacía en lugar de nulo
            props.setProperty("password", password != null ? password : "");
            props.setProperty("encrypt", "true");
            props.setProperty("trustServerCertificate", "true");
            conn = DriverManager.getConnection(
                    url,
                    username,
                    password
            );
        }catch (SQLException e) {
            return;
        }


    }
    public static void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    public static Connection getConnection(){

        try {
            if (conn == null || conn.isClosed()) {
                startConnection();
            }
        }catch (SQLException e) {

            return null;
        }
        return conn;
    }
}
