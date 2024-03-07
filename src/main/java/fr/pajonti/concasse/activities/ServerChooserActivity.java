package fr.pajonti.concasse.activities;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.helper.technical.ExitHandlerHelper;
import fr.pajonti.concasse.helper.technical.StringHelper;
import fr.pajonti.concasse.helper.technical.UserInputHelper;
import fr.pajonti.concasse.provider.database.dao.ServerDAO;
import fr.pajonti.concasse.provider.database.dto.ServerDTO;

import java.sql.SQLException;
import java.util.List;

public class ServerChooserActivity {
    private ServerChooserActivity(){

    }

    public static ServerDTO chooseServer(Configuration configuration){
        try{
            System.out.println("|| ========================================================================= ||");
            System.out.println("||                             Choix du serveur                              ||");
            System.out.println("|| ========================================================================= ||");

            List<ServerDTO> serverDTOList = new ServerDAO(configuration).getServerList();

            for(ServerDTO server : serverDTOList){
                String serverStr = "   ".concat(server.getServerID().toString()).concat(". ").concat(server.getNom());
                System.out.println("||" + StringHelper.padWithCharacter(serverStr, 75, " ", 2) + "||");
            }

            System.out.println("|| ========================================================================= ||");
            System.out.println(" ");

            while(true){
                int userInput = UserInputHelper.readUserInputAsInteger();
                for(ServerDTO server : serverDTOList){
                    if(server.getServerID() == userInput){
                        return server;
                    }
                }

                //Si on n'est tombé sur aucune valeur valide, on reboucle
                System.err.println("Valeur incorrecte saisie. Veuillez réessayer.");
            }
        } catch (SQLException se){
            se.printStackTrace();
            ExitHandlerHelper.exit("Erreur lors de la collecte des serveurs : " + se.getMessage());
        }
        return null;
    }
}
