package fr.pajonti.concasse;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.configuration.ConfigurationLoader;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
//        StorageMethods.genererPrix();

        //Initialise une configuration soit depuis zero, soit depuis une base H2 locale
        Configuration configuration = ConfigurationLoader.load(System.getProperty("config"));


    }


}