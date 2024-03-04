package fr.pajonti.concasse.helper.technical;

import java.io.IOException;
import java.util.Scanner;

public class ExitHandlerHelper {
    private ExitHandlerHelper(){

    }

    public static void exit(String message){
        try{
            System.err.println(message);
            System.err.println("Appuyez sur Entrée pour fermer le programme...");
            System.in.read();
            System.exit(-55);
        }
        catch(IOException ioe){
            System.exit(-55);
        }
    }
}
