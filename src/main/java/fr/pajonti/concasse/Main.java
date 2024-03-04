package fr.pajonti.concasse;

import fr.pajonti.concasse.activities.ServerChooserActivity;
import fr.pajonti.concasse.activities.AccueilUserActivity;
import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.configuration.ConfigurationLoader;
import fr.pajonti.concasse.provider.database.dto.ServerDTO;

import java.io.IOException;
import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        //Affiche un message d'accueil a l'utilisateur
        AccueilUserActivity.greetUser();

        //Initialise une configuration soit depuis zero, soit depuis une base H2 locale
        Configuration configuration = ConfigurationLoader.load(System.getProperty("config"));

        //Collecte le serveur sur lequel on va travailler initialement
        ServerDTO chosenServer = ServerChooserActivity.chooseServer(configuration);

        //Démarre la charge business
        AccueilUserActivity.run(chosenServer, configuration);
    }
}