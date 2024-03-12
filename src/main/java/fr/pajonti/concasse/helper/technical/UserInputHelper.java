package fr.pajonti.concasse.helper.technical;

import fr.pajonti.concasse.provider.database.dto.ServerDTO;

import java.util.Scanner;

public class UserInputHelper {
    private UserInputHelper(){

    }

    public static int readUserInputAsInteger(){
        while(true){
            String userInput = UserInputHelper.readUserInput();

            try{
                return Integer.parseInt(userInput);
            }
            catch(NumberFormatException nfe){
                System.err.println("Valeur incorrecte saisie. Veuillez réessayer.");
            }
        }
    }

    private static String readUserInput(){
        System.out.println(" >>> Votre choix ? : ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
