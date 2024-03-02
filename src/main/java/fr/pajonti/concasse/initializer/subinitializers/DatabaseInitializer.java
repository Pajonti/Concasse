package fr.pajonti.concasse.initializer.subinitializers;

import fr.pajonti.concasse.configuration.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    /**
     * Configuration collectee et alimentee par la suite avec les infos de la BDD
     */
    private Configuration configuration;

    public DatabaseInitializer(Configuration configuration){
        this.configuration = configuration;
    }

    /**
     * Processus de creation de la base de donnees en local.
     * @return Configuration alimentee avec les parametres de la base
     */
    public Configuration initializeDatabase() throws SQLException, IOException {

        createDatabaseObject();
        createDatabaseStructure();
        injectStaticDataInDB();

        return this.configuration;
    }

    /**
     * Crée la base de donnees H2 en passant directement par un connecteur JDBC
     */
    private void createDatabaseObject() throws SQLException {
        System.out.println("Initialisation de la base de donnees...");

        configuration.setDatabaseUsername("concasse");
        configuration.setDatabasePassword("default");
        configuration.setDatabaseJDBCString("jdbc:h2:" + this.configuration.getDatabasePath());

        //Monte une premiere connexion pour construire le fichier
        Connection connection = DriverManager.getConnection(this.configuration.getDatabaseJDBCString(), this.configuration.getDatabaseUsername(), this.configuration.getDatabasePassword());
        connection.close();
    }

    /**
     * Insere la structure de base dans la BDD H2
     */
    private void createDatabaseStructure() throws IOException, SQLException {
        System.out.println("Injection de la structure de base...");
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("sql/initialize_struct.sql")) {
            if(is == null){
                throw new IOException("Impossible de lire le fichier d'initialisation de la structure de la BDD");
            }

            Connection connection = DriverManager.getConnection(this.configuration.getDatabaseJDBCString(), this.configuration.getDatabaseUsername(), this.configuration.getDatabasePassword());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String query;
            while((query = reader.readLine()) != null) {
                Statement statement = connection.createStatement();
                statement.executeUpdate(query);
                statement.close();
            }

            connection.close();
        }
    }

    /**
     * Insere les donnees de base dans la BDD H2
     */
    private void injectStaticDataInDB() throws IOException, SQLException {
        System.out.println("Injection de la structure de base...");
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("sql/initialize_data.sql")) {
            if(is == null){
                throw new IOException("Impossible de lire le fichier d'initialisation des données de la BDD");
            }

            Connection connection = DriverManager.getConnection(this.configuration.getDatabaseJDBCString(), this.configuration.getDatabaseUsername(), this.configuration.getDatabasePassword());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String query;
            while((query = reader.readLine()) != null) {
                Statement statement = connection.createStatement();
                statement.executeUpdate(query);
                statement.close();
            }

            connection.close();
        }
    }
}
