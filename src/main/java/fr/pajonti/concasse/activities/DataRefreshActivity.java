package fr.pajonti.concasse.activities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.helper.technical.ExitHandlerHelper;
import fr.pajonti.concasse.helper.technical.StringHelper;
import fr.pajonti.concasse.provider.database.dto.ServerDTO;
import fr.pajonti.concasse.provider.database.dto.StatItemDTO;
import fr.pajonti.concasse.provider.external.dto.ExternalItemDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class DataRefreshActivity {
    static Configuration activityConfiguration = null;
    static ServerDTO server = null;

    public static void refreshData(ServerDTO serverDTO, Configuration configuration){
        activityConfiguration = configuration;
        server = serverDTO;

        System.out.println("Rafraîchissement des données pour le serveur " + serverDTO.getNom() + ". Cette opération " +
                "va prendre une vingtaine de minutes.");

        List<ExternalItemDTO> externalItemList = pollExternalDataset();

        //TODO : Write data to DB
        //TODO : Split class in external DAOs ?
    }

    private static List<ExternalItemDTO> pollExternalDataset() {
        List<ExternalItemDTO> listFromVulbis = pollDataFromVulbis();
        List<ExternalItemDTO> enrichedListWithDofusDB = pollDataFromDofusDB(listFromVulbis);
        List<ExternalItemDTO> enrichedListWithBrifus = pollDataFromBrifus(enrichedListWithDofusDB);

        return enrichedListWithBrifus;
    }

    /**
     * Récupération des informations depuis Vulbis
     * @return
     */
    private static List<ExternalItemDTO> pollDataFromVulbis() {
        System.out.println("Recherche des données d'objet dans Vulbis pour le serveur " + server.getNom());

        List<ExternalItemDTO> listeItems = new ArrayList<>();

        try (Playwright playwright = Playwright.create()) {

            System.out.println("Démarrage du browser vers Vulbis");

            //Initialisation du browser de collecte
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
            launchOptions.setHeadless(false);
            launchOptions.setSlowMo(300);
            launchOptions.setTimeout(120000);
            Browser browser = playwright.firefox().launch(launchOptions);
            Page page = browser.newPage();

            //Page avec 3 recettes complete
//            page.navigate("https://www.vulbis.com/?server=" + server.getNom().replace(" ", "%20") + "&gids=44,49,20463,16512,303,1673,377,19973,19399,19968,19969,19068,15451,12740,20452&percent=0&craftableonly=false&select-type=-1&sellchoice=false&buyqty=1&sellqty=1&percentsell=0");

            //Page avec plusieurs items
            page.navigate("https://www.vulbis.com/?server=" + server.getNom().replace(" ", "%20") + "&gids=&percent=0&craftableonly=false&select-type=2&sellchoice=false&buyqty=1&sellqty=1&percentsell=0");

            //Toutes donnees confondues
//            page.navigate("https://www.vulbis.com/?server=" + server.getNom().replace(" ", "%20") + "&gids=&percent=0&craftableonly=false&select-type=2&sellchoice=false&buyqty=1&sellqty=1&percentsell=0");

            page.waitForSelector("#scanTable > tbody > tr:nth-child(1) > td:nth-child(2) > div:nth-child(1) > p");

            System.out.println("Browser Vulbis chargé, lecture des éléments");
            boolean allFetched = false;

            while(!allFetched){
                Locator tableauItems = page.locator("#scanTable > tbody");
                int nombreRows = tableauItems.locator("tr").count();

                for(int ligneNumero = 0; ligneNumero < nombreRows; ligneNumero++){
                    Locator line = tableauItems.locator("tr").nth(ligneNumero);
                    Locator nameAndIdLocator = line.locator("td").nth(1).locator("div").nth(0).locator("p");
                    Locator recipeLocator = line.locator("td").nth(1).locator("div").nth(1).locator("p").getByText("[Recette]");
                    Locator priceOneLocator = line.locator("td").nth(5).locator("p");
                    Locator priceTenLocator = line.locator("td").nth(6).locator("p");
                    Locator priceHundredLocator = line.locator("td").nth(7).locator("p");

                    System.out.println("Polling Vulbis de l'item : " + nameAndIdLocator.innerText());

                    String id = nameAndIdLocator.getAttribute("onclick").split(",")[1];
                    id = id.replace("'", "");
                    id = id.trim();

                    String recipeString = "";
                    if(recipeLocator.count() > 0){
                        recipeString = recipeLocator.getAttribute("onclick").split("'")[3];
                        recipeString = recipeString.replace("'", "");
                        recipeString = recipeString.trim();
                    }

                    String priceOne = priceOneLocator.innerText();
                    priceOne = priceOne.replace(" ", "");
                    priceOne = priceOne.replaceAll("[^\\d.]", "");

                    String priceTen = priceTenLocator.innerText();
                    priceTen = priceTen.replace(" ", "");
                    priceTen = priceTen.replaceAll("[^\\d.]", "");

                    String priceHundred = priceHundredLocator.innerText();
                    priceHundred = priceHundred.replace(" ", "");
                    priceHundred = priceHundred.replaceAll("[^\\d.]", "");

                    ExternalItemDTO dto = new ExternalItemDTO();

                    dto.setItemName(nameAndIdLocator.innerText());
                    dto.setItemID(Integer.parseInt(id));
                    dto.setRecipeString(recipeString.trim().isEmpty() ? null : recipeString);
                    dto.setPriceOne(priceOne.trim().isEmpty() || priceOne.trim().equals("-") ? null : Integer.parseInt(priceOne));
                    dto.setPriceTen(priceTen.trim().isEmpty() || priceTen.trim().equals("-") ? null : Integer.parseInt(priceTen));
                    dto.setPriceHundred(priceHundred.trim().isEmpty() || priceHundred.trim().equals("-") ? null : Integer.parseInt(priceHundred));

                    listeItems.add(dto);
                }

                // On verifie si une autre page est disponible
                Locator nextItemLocator = page.locator("#scanTable_next");
                if(!nextItemLocator.getAttribute("class").contains("disabled")){
                    nextItemLocator.click();
                }
                else{
                    allFetched = true;
                }
            }

            browser.close();
        }

        return listeItems;
    }

    /**
     * Collecte des informations depuis DofusDB
     * @param listFromVulbis Donnees alimentees depuis Vulbis
     * @return Donnees alimentees depuis Vulbis + DofusDB
     */
    private static List<ExternalItemDTO> pollDataFromDofusDB(List<ExternalItemDTO> listFromVulbis) {
        System.out.println("Recherche des données d'objet dans DofusDB");

        List<ExternalItemDTO> listeItems = new ArrayList<>();

        try{
            for(ExternalItemDTO dto : listFromVulbis){
                System.out.println("Polling DofusDB de l'item : " + dto.getItemName());

                //Préparation de la requête vers DofusDB
                URL url = new URL("https://api.dofusdb.fr/items/" + dto.getItemID());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                //Gestion du retour
                if(conn.getResponseCode() == 200){
                    StringBuilder apiResponseAsJSON = new StringBuilder();

                    Scanner scanner = new Scanner(url.openStream());
                    while (scanner.hasNext()) {
                        apiResponseAsJSON.append(scanner.nextLine());
                    }

                    scanner.close();

                    //Découpage du JSON
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonRoot = mapper.readTree(apiResponseAsJSON.toString());
                    JsonNode levelNode = jsonRoot.path("level");
                    JsonNode typeNode = jsonRoot.path("typeId");
                    JsonNode statsNode = jsonRoot.path("effects");

                    dto.setLevel(levelNode.asInt());
                    dto.setTypeId(typeNode.asInt());

                    for (Iterator<JsonNode> it = statsNode.elements(); it.hasNext(); ) {
                        JsonNode statNode = it.next();

                        Integer statId = statNode.path("characteristic").asInt();
                        Integer statFrom = statNode.path("from").asInt();
                        Integer statTo = statNode.path("to").asInt();

                        boolean estNegative = (statFrom < 0 && statTo < 0);

                        //Si la stat est négative, on inverse le from et le to
                        dto.addStatItem(new StatItemDTO(
                                (estNegative ? statTo : statFrom),
                                (estNegative ? statFrom : statTo),
                                dto.getItemID(),
                                statId
                        ));
                    }

                    listeItems.add(dto);
                }
                else{
                    System.err.println("Impossible de collecter l'objet " + dto.getItemName() + ". Ce dernier sera ignoré.");
                }
            }
        }
        catch (IOException ioe){
            ioe.printStackTrace();
            ExitHandlerHelper.exit("Erreur lors de la collecte des infos DofusDB : " + ioe.getMessage());
        }

        return listeItems;
    }

    /**
     * Collecte des informations depuis Brifus
     * @param enrichedListWithDofusDB Donnees alimentees depuis Vulbis + DofusDB
     * @return Donnees alimentees depuis Vulbis + DofusDB + Brifus
     */
    private static List<ExternalItemDTO> pollDataFromBrifus(List<ExternalItemDTO> enrichedListWithDofusDB) {
        System.out.println("Recherche des données d'objet dans Brifus");

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<ExternalItemDTO> listeItems = new ArrayList<>();

        try{
            for(ExternalItemDTO dto : enrichedListWithDofusDB){
                if(dto.getRecipeString() != null && !dto.getRecipeString().trim().isEmpty()){
                    System.out.println("Recherche du taux de brisage de l'item " + dto.getItemName() + " - Code " + dto.getItemID());
                    URL urlCoeff = new URL("https://api.brifus.fr/coeff");

                    HttpURLConnection connCoeff = (HttpURLConnection) urlCoeff.openConnection();
                    connCoeff.setRequestMethod("POST");
                    connCoeff.setRequestProperty("Content-Type", "application/json");
                    connCoeff.setRequestProperty("Accept", "application/json");

                    connCoeff.setDoOutput(true);

                    String body = "{\"item\":" + dto.getItemID() + ",\"server\":" + server.getBrifusID() + "}";

                    try(OutputStream os = connCoeff.getOutputStream()) {
                        byte[] input = body.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    try(BufferedReader br = new BufferedReader(new InputStreamReader(connCoeff.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }

                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode rootNode = objectMapper.readTree(response.toString());

                        if(rootNode.path("coefficient") != null){

                            String dateTime = "";
                            if(rootNode.path("dateTime") != null){
                                dateTime = "(Valeur du " +  rootNode.path("dateTime").asText() + ")";
                                dto.setBrifusUpdateDate(LocalDateTime.from(df.parse(rootNode.path("dateTime").asText())));
                            }

                            Double tx = rootNode.path("coefficient").asDouble();
                            dto.setTauxBrisage(tx.floatValue());

                            System.out.println("Coefficient de brisage de l'item : " + dto.getItemName() + " : " + tx.floatValue() + ". " + dateTime);
                        }
                    }
                }


                listeItems.add(dto);
            }
        }
        catch (IOException ioe){
            ioe.printStackTrace();
            ExitHandlerHelper.exit("Erreur lors de la collecte des infos Brifus : " + ioe.getMessage());
        }

        return listeItems;
    }
}
