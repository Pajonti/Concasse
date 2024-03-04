package fr.pajonti.concasse.activities;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.helper.technical.StringHelper;
import fr.pajonti.concasse.helper.technical.UserInputHelper;
import fr.pajonti.concasse.provider.database.dto.ServerDTO;

public class AccueilUserActivity {
    private static ServerDTO serverDTO;
    private AccueilUserActivity(){
        serverDTO = null;
    }

    public static void run(ServerDTO chosenServer, Configuration configuration) {
        serverDTO = chosenServer;

        while(true){
            displayMenu();

            int choix = UserInputHelper.readUserInputAsInteger();

            switch (choix){
                case 1:
                    DataRefreshActivity.refreshData(serverDTO, configuration);
                    break;
                case 2 :
                    JobSettingActivity.setJobs(serverDTO, configuration);
                    break;
                case 3:
                    System.out.println("TODO : Calcul brisage");
                    break;
                case 9 :
                    serverDTO = ServerChooserActivity.chooseServer(configuration);
                    break;
                case 0:
                    System.exit(-55);
                    break;
                default:
                    System.err.println("Valeur incorrecte saisie. Veuillez réessayer.");
            }
        }
    }


    public static void greetUser() {
        System.out.println("");
        System.out.println("");
        System.out.println("  /$$$$$$   /$$$$$$  /$$   /$$  /$$$$$$   /$$$$$$   /$$$$$$  /$$$$$$  /$$$$$$$$      ");
        System.out.println(" /$$__  $$ /$$__  $$| $$$ | $$ /$$__  $$ /$$__  $$ /$$__  $$/$$__  $$| $$_____/      ");
        System.out.println("| $$  \\__/| $$  \\ $$| $$$$| $$| $$  \\__/| $$  \\ $$| $$  \\__/ $$  \\__/| $$      ");
        System.out.println("| $$      | $$  | $$| $$ $$ $$| $$      | $$$$$$$$|  $$$$$$|  $$$$$$ | $$$$$         ");
        System.out.println("| $$      | $$  | $$| $$  $$$$| $$      | $$__  $$ \\____  $$\\____  $$| $$__/       ");
        System.out.println("| $$    $$| $$  | $$| $$\\  $$$| $$    $$| $$  | $$ /$$  \\ $$/$$  \\ $$| $$         ");
        System.out.println("|  $$$$$$/|  $$$$$$/| $$ \\  $$|  $$$$$$/| $$  | $$|  $$$$$$/  $$$$$$/| $$$$$$$$     ");
        System.out.println(" \\______/  \\______/ |__/  \\__/ \\______/ |__/  |__/ \\______/ \\______/ |________/");
        System.out.println("");
        System.out.println("");
        System.out.println("                           A Pajonti's Business Tool                                 ");
        System.out.println("                                     ---                                             ");
        System.out.println(" ");
    }

    private static void displayMenu() {
        System.out.println(" ");
        System.out.println("|| ========================================================================= ||");
        System.out.println("||                            Menu des opérations                            ||");
        System.out.println("|| ========================================================================= ||");
        System.out.println("||                                 Paramètres                                ||");
        System.out.println("|| ------------------------------------------------------------------------- ||");
        System.out.println("||   1. Rafraîchissement des prix et taux (Dernière exécution : " + StringHelper.padWithCharacter(getLastPollingDate(serverDTO), 10, " ", 2) + ")  ||");
        System.out.println("||   2. Réglage des métiers exercés                                          ||");
        System.out.println("||                                                                           ||");
        System.out.println("||                                                                           ||");
        System.out.println("||                                 Exécutions                                ||");
        System.out.println("|| ------------------------------------------------------------------------- ||");
        System.out.println("||   3. Calcul de rentabilité brisage                                        ||");
        System.out.println("||                                                                           ||");
        System.out.println("||                                                                           ||");
        System.out.println("||                                   Autres                                  ||");
        System.out.println("|| ------------------------------------------------------------------------- ||");
        System.out.println("||   9. Changement de serveur                                                ||");
        System.out.println("||   0. Quitter Concasse                                                     ||");
        System.out.println("|| ========================================================================= ||");
        System.out.println(" ");
    }

    private static String getLastPollingDate(ServerDTO serverDTO) {
        //TODO : Implementer la derniere date de collecte des donnees via un DTO

        return "21/12/2006";
    }


}
