package fr.pajonti.concasse.configuration;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

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

    public Configuration(String propertiesFilePath) throws IOException {
        //On force la lecture du fichier de properties avant de le donner a l'objec pour gerer le cas des antislashs
        StringBuilder resultStringBuilder = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(propertiesFilePath))));
        String line;
        while ((line = br.readLine()) != null) {
            resultStringBuilder.append(line).append("\n");
        }

        Properties props = new Properties();
        props.load(new StringReader(resultStringBuilder.toString().replace("\\", "\\\\")));

        this.basePath = props.getProperty("config.path");
        this.databasePath = props.getProperty("config.database.path");
        this.databaseUsername = props.getProperty("config.database.user");
        this.databasePassword = props.getProperty("config.database.pass");
        this.databaseJDBCString = props.getProperty("config.database.jdbc");
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

    /**
     * Genere le fichier de properties reutilisable par la suite
     */
    public void genererFichierProperties() throws IOException {
        System.out.println("Génération du fichier de properties...");

        String filePath = this.getBasePath() + System.getProperty("file.separator") + "concasse.properties";
        File propertiesObject = new File(filePath);

        if (propertiesObject.createNewFile()) {
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");
            writer.println(hydraterProperties());
            writer.close();
        } else {
            throw new IOException("Fichier de properties deja existant");
        }
    }

    /**
     * Hydrate le fichier de configuration avec les informations de l'objet Configuration via le template Mustache fourni
     * en ressource du package
     * @return String du fichier de configuration a inscrire ensuite
     * @throws IOException Si le template Mustache n'est pas accessible
     */
    public String hydraterProperties() throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache m = mf.compile(
                new InputStreamReader(
                        Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("template/configuration_template.mustache"))
                ), "config"
        );

        HashMap<String, Object> dataWrapper = new HashMap<>();
        dataWrapper.put("config", this);

        StringWriter writer = new StringWriter();
        m.execute(writer, this).flush();

        return writer.toString();
    }
}
