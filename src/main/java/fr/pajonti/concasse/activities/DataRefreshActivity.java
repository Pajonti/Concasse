package fr.pajonti.concasse.activities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.helper.technical.ExitHandlerHelper;
import fr.pajonti.concasse.helper.technical.StringHelper;
import fr.pajonti.concasse.helper.technical.UserInputHelper;
import fr.pajonti.concasse.provider.database.dao.GenericDAO;
import fr.pajonti.concasse.provider.database.dao.NiveauMetierDAO;
import fr.pajonti.concasse.provider.database.dao.TauxBrisageDAO;
import fr.pajonti.concasse.provider.database.dto.*;
import fr.pajonti.concasse.provider.external.dao.BrifusDAO;
import fr.pajonti.concasse.provider.external.dao.DofusDBDAO;
import fr.pajonti.concasse.provider.external.dao.ExternalItemDAO;
import fr.pajonti.concasse.provider.external.dao.VulbisDAO;
import fr.pajonti.concasse.provider.external.dto.ExternalItemDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataRefreshActivity {
    static Configuration activityConfiguration = null;
    static ServerDTO server = null;

    public static void showMenu(ServerDTO serverDTO, Configuration configuration){
        try{
            server = serverDTO;
            activityConfiguration = configuration;

            boolean menuEnded = false;

            while(!menuEnded){
                GenericDAO genericDAO = new GenericDAO(configuration);
                boolean initializedDB = genericDAO.serverInitialized(server);

                String dateDernierRefreshTaux = genericDAO.getDateDernierRefreshTaux(server);
                String dateDernierRefreshPrix = genericDAO.getDateDernierRefreshPrix(server);

                displayMenu(initializedDB, dateDernierRefreshTaux, dateDernierRefreshPrix);

                int choix = UserInputHelper.readUserInputAsInteger();

                if(!initializedDB){
                    switch (choix){
                        case 1:
                            DataRefreshActivity.refreshCompleteData(server, activityConfiguration);
                            break;
                        case 0:
                            menuEnded = true;
                            break;
                        default:
                            System.err.println("Valeur incorrecte saisie. Veuillez réessayer.");
                    }
                }
                else{
                    switch (choix){
                        case 1:
                            DataRefreshActivity.refreshCompleteData(server, activityConfiguration);
                            break;
                        case 2 :
                            DataRefreshActivity.refreshPrices(server, activityConfiguration);
                            break;
                        case 3:
                            DataRefreshActivity.refreshTauxBrisage(server, activityConfiguration);
                            break;
                        case 0:
                            menuEnded = true;
                            break;
                        default:
                            System.err.println("Valeur incorrecte saisie. Veuillez réessayer.");
                    }
                }
            }
        }
        catch (SQLException se){
            se.printStackTrace();
            ExitHandlerHelper.exit("Erreur lors de l'acces a la base de donnees : " + se.getMessage());
        }

    }

    public static void refreshCompleteData(ServerDTO serverDTO, Configuration configuration){
        System.out.println("(Ré)initialisation des données pour le serveur " + serverDTO.getNom() + ". Cette opération " +
                "va prendre une trentaine de minutes.");

        List<ExternalItemDTO> listFromVulbis = new VulbisDAO(server).pollDataFromVulbis(); //1mn
        List<ExternalItemDTO> listAfterDofusDB = new DofusDBDAO(listFromVulbis).pollDataFromDofusDB(); //20mn
        List<ExternalItemDTO> listAfterBrifus = new BrifusDAO(listAfterDofusDB, server).pollDataFromBrifus(); //4 mn

        new ExternalItemDAO(activityConfiguration, server).saveDataRefreshList(listAfterBrifus);

        System.out.println("Rafraichissement des données terminé");
    }

    public static void refreshPrices(ServerDTO serverDTO, Configuration configuration){
        List<ExternalItemDTO> listFromVulbis = new VulbisDAO(server).pollDataFromVulbis();

        new ExternalItemDAO(activityConfiguration, server).saveVulbisDataOnly(listFromVulbis);
    }

    public static void refreshTauxBrisage(ServerDTO serverDTO, Configuration configuration) throws SQLException {
        TauxBrisageDAO tauxBrisageDAO = new TauxBrisageDAO(configuration);
        List<TauxBrisageDTO> taux = tauxBrisageDAO.getTauxConnus(serverDTO);
        List<ExternalItemDTO> listFromDatabase = new ArrayList<>();

        for(TauxBrisageDTO brisage : taux){
            ExternalItemDTO external = new ExternalItemDTO();

            external.setEstConcassable(true);
            external.setItemID(brisage.getVulbisID());
            external.setTauxBrisage(brisage.getTauxConcassage());
            external.setItemName("###");
            external.setRecipeString("###");
            external.addStatItem(new StatItemDTO(10, 20, brisage.getVulbisID(), 2)); // Stat factice

            listFromDatabase.add(external);
        }

        List<ExternalItemDTO> listAfterBrifus = new BrifusDAO(listFromDatabase, server).pollDataFromBrifus();

        new ExternalItemDAO(activityConfiguration, server).saveBrifusDataOnly(listAfterBrifus);
    }

    private static void displayMenu(boolean initializedDB, String dateDernierRefreshTaux, String dateDernierRefreshPrix) {
        System.out.println(" ");
        System.out.println("|| ========================================================================= ||");
        System.out.println("||" + StringHelper.padWithCharacter("Rafraîchissement des données (" + server.getNom() + ")", 75, " ", 3) + "||");
        System.out.println("|| ========================================================================= ||");
        System.out.println("||" + StringHelper.padWithCharacter("   1. (Ré)initialisation des données", 75, " ", 2) + "||");

        if(initializedDB){
            System.out.println("||                                                                           ||");
            System.out.println("||" + StringHelper.padWithCharacter("   2. Rafraîchissement des prix (Dernier refresh : " + dateDernierRefreshPrix + ")", 75, " ", 2) + "||");
            System.out.println("||" + StringHelper.padWithCharacter("   3. Rafraîchissement des taux (Dernier refresh : " + dateDernierRefreshTaux + ")", 75, " ", 2) + "||");
            System.out.println("||" + StringHelper.padWithCharacter("   4. Rafraîchissement des stats", 75, " ", 2) + "||");
        }

        System.out.println("||                                                                           ||");
        System.out.println("|| ------------------------------------------------------------------------- ||");
        System.out.println("||" + StringHelper.padWithCharacter("   0. Revenir au menu", 75, " ", 2) + "||");
        System.out.println("|| ========================================================================= ||");
        System.out.println(" ");
    }
}
