package org.hei.school.fifa_foot_api.dao;

import java.rmi.ServerException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private final static int defaultPort = 5432;
    private final String host = System.getenv("DATABASE_HOST");
    private final String user = System.getenv("DATABASE_USER");
    private final String password = System.getenv("DATABASE_PASSWORD");
    private final String database = System.getenv("DATABASE_NAME");
    private final String jdbcUrl;

    public DataSource() {
        jdbcUrl = "jdbc:postgresql://" + host + ":" + defaultPort + "/" + database;
    }

    public Connection getConnection() {
        try {
            System.out.println("✅ Connection with database successfully");
            return DriverManager.getConnection(jdbcUrl, user, password);
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect with database");
            throw new RuntimeException(e);
        }
    }
}
