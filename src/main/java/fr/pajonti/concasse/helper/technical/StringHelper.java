package fr.pajonti.concasse.helper.technical;

public class StringHelper {
    private StringHelper(){

    }

    /**
     * Padde une chaine de caractères avec un caractère donné sur une longueur donnée, selon le mode choisi
     * (Pad gauche, pad droite, ou pad centre)
     * @param mainString Chaine a padder
     * @param length Longueur de chaine finale a obtenir
     * @param padChar Caractère de padding
     * @param mode Mode de padding : 1 pour pad Gauche, 2 pour pad Droite, 3 pour pad Centre
     * @return String paddée
     */
    public static String padWithCharacter(String mainString, int length, String padChar, int mode){
        if(mainString == null){
            return null;
        }

        switch (mode){
            case 1:
                while (mainString.length() < length){
                    mainString = padLeft(mainString, padChar);
                }
                return mainString;
            case 2:
                while (mainString.length() < length){
                    mainString = padRight(mainString, padChar);
                }
                return mainString;
            case 3:
                while (mainString.length() < length){
                    mainString = (mainString.length() % 2 == 0 ?  padLeft(mainString, padChar) :  padRight(mainString, padChar));
                }
                return mainString;
            default:
                return mainString;
        }
    }

    private static String padRight(String mainString, String padChar) {
        return mainString + padChar;
    }

    private static String padLeft(String mainString, String padChar) {
        return padChar + mainString;
    }

    /**
     * Renvoie une chaine de caracteres avec un padding
     * @param str
     * @param width
     * @param isErr
     */
    public static void printStringWithFixedWidth(String str, int width, boolean isErr){
        String[] splitString = str.split(" ");

        String display = "";
        for(int i = 0; i < splitString.length; i++){
            String word = splitString[i];

            //Si le cumul de la string deja existante et du mot depasse la limite, alors on print la string sans
            //concatener, et on met le mot comme valeur de la nouvelle string avant de passer a la suite. Sinon, on
            //append.
            if(display.length() + word.length() + 1 > width){
                if (isErr) {
                    System.err.println(display);
                } else {
                    System.out.println(display);
                }
                display = word.concat(" ");
            }
            else{
                display = display.concat(word).concat(" ");
            }
        }

        if (isErr) {
            System.err.println(display);
        } else {
            System.out.println(display);
        }
    }

    public static String cleanAccents(String str) {
        return str.replace("É", "E")
                .replace("È", "E")
                .replace("Ê", "E")
                .replace("Ë", "E")
                .replace("é", "e")
                .replace("è", "e")
                .replace("ê", "e")
                .replace("ë", "e")
                .replace("à", "a")
                .replace("ä", "a")
                .replace("î", "i")
                .replace("ï", "i")
                .replace("Î", "i")
                .replace("Ï", "i")
                .replace("Ö", "O")
                .replace("Ô", "O")
                .replace("ô", "o")
                .replace("ö", "o")
                .replace("û", "u")
                .replace("ü", "u")
                .replace("Ü", "U")
                .replace("Û", "U")
                .replace("Œ", "oe")
                .replace("œ", "oe");
    }
}
