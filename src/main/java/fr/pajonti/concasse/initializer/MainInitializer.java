package fr.pajonti.concasse.initializer;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.initializer.subinitializers.ConfigurationInitializer;
import fr.pajonti.concasse.initializer.subinitializers.DatabaseInitializer;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Classe principale d'initialisation des donnees necessaires au bon fonctionnement de Concasse.
 */
public class MainInitializer {
    private Configuration configuration;


    public MainInitializer(){

    }

    public void initialize() throws IOException, SQLException {
        System.out.println("Il semblerait que ce soit la première fois que vous lanciez Concasse. (Ou bien " +
                "avez-vous oublié de lancer l'appli avec le paramètre -Dconfig ?) Avant de pouvoir utiliser Concasse, " +
                "vous allez devoir paramétrer l'application une première fois. Ce processus va prendre une vingtaine de minutes.");

        System.out.println(" ");


        this.configuration = new ConfigurationInitializer().generateConfig();
        this.configuration = new DatabaseInitializer(this.configuration).initializeDatabase();

        this.configuration.genererFichierProperties();

        System.out.println(" ");
        System.out.println(" ");
        System.out.println("La génération de la configuration est terminée. Pensez à modifier la valeur de -Dconfig dans le fichier concasse.bat pour la renseigner à -Dconfig=" + this.getConfiguration().getBasePath());
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
