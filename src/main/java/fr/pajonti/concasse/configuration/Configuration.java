package fr.pajonti.concasse.configuration;

import fr.pajonti.concasse.configuration.fields.ConfigurationFieldsEnum;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Classe Wrapper de la configuration utilisee dans l'appli
 */
public class Configuration {

    /**
     * Chemin racine dans lequel les donnees vont etre crees
     */
    private String basePath;

    /**
     * Chemin dans lequel la database va etre positionnee
     */
    private String databasePath;

    /**
     * Username de la base de donnees
     */
    private String databaseUsername;

    /**
     * Password de la base de donnees
     */
    private String databasePassword;

    /**
     * Chaine JDBC de la base de donnees
     */
    private String databaseJDBCString;

    public Configuration(){

    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    public String getDatabaseJDBCString() {
        return databaseJDBCString;
    }

    public void setDatabaseJDBCString(String databaseJDBCString) {
        this.databaseJDBCString = databaseJDBCString;
    }

    public String toProperties(){
        StringBuilder builder = new StringBuilder();

        builder.append(ConfigurationFieldsEnum.CONFIG_BASE_PATH.getField() + "=" + this.basePath);
        builder.append("\r\n");
        builder.append(ConfigurationFieldsEnum.CONFIG_DATABASE_PATH.getField() + "=" + this.databasePath);

        return builder.toString();
    }

    /**
     * Genere le fichier de properties reutilisable par la suite
     */
    public void genererFichierProperties() throws IOException {
        System.out.println("Génération du fichier de properties...");

        String filePath = this.getBasePath() + System.getProperty("file.separator") + "concasse.properties";
        File propertiesObject = new File(filePath);

        if (propertiesObject.createNewFile()) {
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");
            writer.println(toProperties());
            writer.close();
        } else {
            throw new IOException("Fichier de properties deja existant");
        }
    }
}
