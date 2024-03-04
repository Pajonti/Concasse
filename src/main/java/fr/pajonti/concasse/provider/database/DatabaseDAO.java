package fr.pajonti.concasse.provider.database;

import fr.pajonti.concasse.configuration.Configuration;

import java.sql.*;

/**
 * Classe de support des DAO H2 permettant d'exposer les methodes de READ et d'UPDATE
 */
public class DatabaseDAO {

    protected final String jdbcURL;
    protected final String username;
    protected final String password;
    protected Connection connection;

    public DatabaseDAO(Configuration configuration) throws SQLException {
        this.jdbcURL = configuration.getDatabaseJDBCString();
        this.username = configuration.getDatabaseUsername();
        this.password = configuration.getDatabasePassword();

        this.connection = DriverManager.getConnection(this.jdbcURL, this.username, this.password);
    }
}
