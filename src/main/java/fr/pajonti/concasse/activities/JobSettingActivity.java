package fr.pajonti.concasse.activities;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.helper.technical.ExitHandlerHelper;
import fr.pajonti.concasse.helper.technical.StringHelper;
import fr.pajonti.concasse.helper.technical.UserInputHelper;
import fr.pajonti.concasse.provider.database.dao.MetierDAO;
import fr.pajonti.concasse.provider.database.dao.NiveauMetierDAO;
import fr.pajonti.concasse.provider.database.dto.MetierDTO;
import fr.pajonti.concasse.provider.database.dto.NiveauMetierDTO;
import fr.pajonti.concasse.provider.database.dto.ServerDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JobSettingActivity {
    static Configuration activityConfiguration = null;
    static List<MetierDTO> listeMetiers = null;
    static ServerDTO server = null;

    public static void setJobs(ServerDTO serverDTO, Configuration configuration) {
        try {
            //Initialisation des variables du menu
            boolean done = false;
            server = serverDTO;
            activityConfiguration = configuration;
            listeMetiers = new MetierDAO(activityConfiguration).getMetiersList();
            List<Integer> idsMetiers = new ArrayList<>();

            for(MetierDTO metierDTO : listeMetiers){
                idsMetiers.add(metierDTO.getId());
            }

            //Affichage du menu et captation de l'input utilisateur
            while(!done) {
                displayMenu();

                int choix = UserInputHelper.readUserInputAsInteger();

                if(choix == 99){
                    done = true;
                }
                else{
                    if(idsMetiers.contains(choix)){
                        setJobLevel(choix);
                    }
                    else{
                        System.err.println("Valeur incorrecte saisie. Veuillez réessayer.");
                    }
                }

            }
        }
        catch (SQLException se){
            se.printStackTrace();
            ExitHandlerHelper.exit("Erreur lors de la collecte des métiers : " + se.getMessage());
        }

    }


    private static void setJobLevel(int metierID) throws SQLException {
        System.out.println(" ");

        for(MetierDTO dtos : listeMetiers){
            if(dtos.getId() == metierID){
                System.out.println("Saisissez le niveau de votre métier " + dtos.getLabel() + " : ");

                int niveau = -1;

                while(!(niveau >= 1 && niveau <= 200)){
                    niveau = UserInputHelper.readUserInputAsInteger();

                    if(!(niveau >= 1 && niveau <= 200)){
                        System.err.println("Valeur incorrecte saisie. Veuillez réessayer.");
                    }
                }

                new NiveauMetierDAO(activityConfiguration).updateNiveauMetier(new NiveauMetierDTO(metierID, server.getServerID(), niveau));
            }
        }
    }

    private static void displayMenu() {
        try{
            System.out.println(" ");
            System.out.println("|| ========================================================================= ||");
            System.out.println("||" + StringHelper.padWithCharacter("Réglage des métiers (" + server.getNom() + ")", 75, " ", 3) + "||");
            System.out.println("|| ========================================================================= ||");
            System.out.println("||   ID     ||   Métier                           ||   Niveau                ||");
            System.out.println("|| ------------------------------------------------------------------------- ||");

            List<NiveauMetierDTO> listeNiveauxMetiers = new NiveauMetierDAO(activityConfiguration).getNiveauMetierListByServerId(server.getServerID());

            for(MetierDTO metierDTO : listeMetiers){
                for(NiveauMetierDTO niveauMetierDTO : listeNiveauxMetiers){
                    if(metierDTO.getId() == niveauMetierDTO.getMetierID()){
                        String idDisplay = "||" + StringHelper.padWithCharacter("   " + metierDTO.getId(), 10, " ", 2);
                        String metierNameDisplay = "||" + StringHelper.padWithCharacter("   " + metierDTO.getLabel(), 36, " ", 2);
                        String metierLevelDisplay = "||" + StringHelper.padWithCharacter("   " + niveauMetierDTO.getNiveauMetier(), 25, " ", 2);

                        System.out.println(idDisplay + metierNameDisplay + metierLevelDisplay + "||");
                    }
                }
            }
            System.out.println("|| ------------------------------------------------------------------------- ||");
            System.out.println("||  99      ||  Modifications terminées                                      ||");
            System.out.println("|| ========================================================================= ||");
            System.out.println(" ");

        }
        catch (SQLException se){
            se.printStackTrace();
            ExitHandlerHelper.exit("Erreur lors de la collecte des métiers : " + se.getMessage());
        }

    }
}
