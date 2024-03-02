package fr.pajonti.concasse;

import com.microsoft.playwright.*;

public class StorageMethods {
    private StorageMethods(){

    }

    public static void genererPrix(){
        System.out.println("Hello world!");

        try (Playwright playwright = Playwright.create()) {
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
            launchOptions.setHeadless(false);
            launchOptions.setSlowMo(300);
            Browser browser = playwright.firefox().launch(launchOptions);

            Page page = browser.newPage();
//            page.navigate("https://www.vulbis.com/?server=Draconiros&gids=&percent=0&craftableonly=false&select-type=-1&sellchoice=false&buyqty=1&sellqty=1&percentsell=0");
            page.navigate("https://www.vulbis.com/?server=Draconiros&gids=2462%2C747%2C304%2C273&percent=0&craftableonly=false&select-type=-1&sellchoice=false&buyqty=1&sellqty=1&percentsell=0");
            page.waitForSelector("#scanTable > tbody > tr:nth-child(1) > td:nth-child(2) > div:nth-child(1) > p");

            System.out.println("Dataset chargé, on va lire les elements");

            int pageNum = 1;
            boolean allFetched = false;

            while(!allFetched){

                System.out.println("Lecture de la page " + pageNum);

                //business logic happening here....

                //Collecte du tableau

                Locator tableauItems = page.locator("#scanTable > tbody");
                int nombreRows = tableauItems.locator("tr").count();

                System.out.println(nombreRows + " lignes trouvees ");

                for(int ligneNumero = 0; ligneNumero < nombreRows; ligneNumero++){
                    Locator line = tableauItems.locator("tr").nth(ligneNumero);
                    Locator nameAndIdLocator = line.locator("td").nth(1).locator("div").nth(0).locator("p");
                    Locator recipeLocator = line.locator("td").nth(1).locator("div").nth(1).locator("p").getByText("[Recette]");
                    Locator priceOneLocator = line.locator("td").nth(5).locator("p");
                    Locator priceTenLocator = line.locator("td").nth(6).locator("p");
                    Locator priceHundredLocator = line.locator("td").nth(7).locator("p");

                    System.out.println("Item : " + nameAndIdLocator.innerText());

                    String id = nameAndIdLocator.getAttribute("onclick").split(",")[1];
                    id = id.replace("'", "");
                    id = id.trim();
                    System.out.println("ID : " + id);

                    String recipeString = "";
                    if(recipeLocator.count() > 0){
                        recipeString = recipeLocator.getAttribute("onclick").split("'")[3];
                        recipeString = recipeString.replace("'", "");
                        recipeString = recipeString.trim();
                    }
                    System.out.println("RecipeString : " + recipeString);


                    String priceOne = priceOneLocator.innerText();
                    priceOne = priceOne.replace(" ", "");
                    priceOne = priceOne.replaceAll("[^\\d.]", "");
                    System.out.println("PriceOne : " + priceOne);

                    String priceTen = priceTenLocator.innerText();
                    priceTen = priceTen.replace(" ", "");
                    priceTen = priceTen.replaceAll("[^\\d.]", "");
                    System.out.println("PriceTen : " + priceTen);

                    String priceHundred = priceHundredLocator.innerText();
                    priceHundred = priceHundred.replace(" ", "");
                    priceHundred = priceHundred.replaceAll("[^\\d.]", "");
                    System.out.println("PriceHundred : " + priceHundred);

                    System.out.println("=======");
                }





                // On verifie si autre chose passe


                Locator nextItemLocator = page.locator("#scanTable_next");

                //isDisabled KO, c'est si il est de la classe disabled
                if(!nextItemLocator.getAttribute("class").contains("disabled")){
                    pageNum++;
                    System.out.println("passage a la page " + pageNum);
                    nextItemLocator.click();
                }
                else{
                    allFetched = true;
                }

            }

            System.out.println("hi");
        }
    }
}
