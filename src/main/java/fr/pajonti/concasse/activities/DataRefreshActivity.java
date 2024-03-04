package fr.pajonti.concasse.activities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.helper.technical.ExitHandlerHelper;
import fr.pajonti.concasse.provider.database.dto.ServerDTO;
import fr.pajonti.concasse.provider.database.dto.StatItemDTO;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        new ExternalItemDAO(activityConfiguration, server).saveDataRefreshList(externalItemList);

        System.out.println("Rafraichissement des données terminé");
        //TODO : Split class in external DAOs ?
    }

    private static List<ExternalItemDTO> pollExternalDataset() {
        List<ExternalItemDTO> listFromVulbis = new VulbisDAO(activityConfiguration, server).pollDataFromVulbis();
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

            System.out.println("Démarrage du browser vers Vulbis. Cela pourrait prendre une minute ou deux.");

            //Initialisation du browser de collecte
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
            launchOptions.setHeadless(false);
            launchOptions.setSlowMo(300);
            launchOptions.setTimeout(240000);
            Browser browser = playwright.firefox().launch(launchOptions);
            Page page = browser.newPage();
            page.setDefaultTimeout(240000);
            page.setDefaultNavigationTimeout(240000);

            //Page avec 3 recettes complete
//            page.navigate("https://www.vulbis.com/?server=" + server.getNom().replace(" ", "%20") + "&gids=7652,16490,428,421,473,303,2411,383,2465,2539,1551,1552,1553,1554,1555,1556,7445,7450,10616,10619,1545,1546,1547,1548,1549,1550,7444,7449,10613,10615,10618,10662,11638,11640,11642,11644,11646,11648,11650,11652,11654,11656,11658,11660,11662,11664,11666,19337,19338,19339,19340,19341,19342,1519,1521,1522,1523,1524,1525,1557,1558,7433,7434,7435,7436,7437,7438,7442,7443,7446,7447,7448,7451,7452,7453,7454,7455,7456,7457,7458,7459,7460,7560,10057,11637,11639,11641,11643,11645,11647,11649,11651,11653,11655,11657,11659,11661,11663,11665,18719,18720,18721,18722,18723,18724&percent=0&craftableonly=false&select-type=-1&sellchoice=false&buyqty=1&sellqty=1&percentsell=0");

            //Page avec plusieurs items
//            page.navigate("https://www.vulbis.com/?server=" + server.getNom().replace(" ", "%20") + "&gids=&percent=0&craftableonly=false&select-type=2&sellchoice=false&buyqty=1&sellqty=1&percentsell=0");

            //Toutes donnees confondues
            page.navigate("https://www.vulbis.com/?server=" + server.getNom().replace(" ", "%20") + "&gids=&percent=0&craftableonly=false&select-type=-1&sellchoice=false&buyqty=1&sellqty=1&percentsell=0");

            page.waitForSelector("#scanTable > tbody > tr:nth-child(1) > td:nth-child(2) > div:nth-child(1) > p");
            //TODO : Voir si diminuer a 10 reduit la duree de trtm
            page.evaluate("let amountSel=document.getElementsByName(\"scanTable_length\");amountSel[0].options[7].selected = true;amountSel[0].dispatchEvent(new Event('change'));");

            System.out.println("Browser Vulbis chargé, lecture des éléments");
            boolean allFetched = false;

            //Nombre de lignes a lire
            Locator linesLocator = page.locator("#scanTable_info");
            String linesLocatorValue = linesLocator.innerText().split("sur")[1].replace("objets", "").trim().replace(".", "");
            int nbLignes = Integer.parseInt(linesLocatorValue);

            int count = 1;

            while(!allFetched){
                Locator tableauItems = page.locator("#scanTable > tbody");

                String contentTest = tableauItems.innerHTML().trim().replace("\t", "");
                System.out.println("AHOY CAPTAIN");

//                int nombreRows = tableauItems.locator("tr").count();
//
//                for(int ligneNumero = 0; ligneNumero < nombreRows; ligneNumero++){
//                    if(count % 100 == 0){
//                        System.out.println("Polling Vulbis : " + count + "/" + nbLignes);
//                    }
//
//                    Locator line = tableauItems.locator("tr").nth(ligneNumero);
//                    Locator nameAndIdLocator = line.locator("td").nth(1).locator("div").nth(0).locator("p");
//                    Locator recipeLocator = line.locator("td").nth(1).locator("div").nth(1).locator("p").getByText("[Recette]");
//                    Locator priceUpdateLocator = line.locator("td").nth(3);
//                    Locator priceOneLocator = line.locator("td").nth(5).locator("p");
//                    Locator priceTenLocator = line.locator("td").nth(6).locator("p");
//                    Locator priceHundredLocator = line.locator("td").nth(7).locator("p");
//
//                    String itemName = nameAndIdLocator.innerText();
//                    itemName = itemName.replace("[", "");
//                    itemName = itemName.replace("]", "");
//                    itemName = itemName.replace("*", "");
//
//                    String id = nameAndIdLocator.getAttribute("onclick").split(",")[1];
//                    id = id.replace("'", "");
//                    id = id.trim();
//
//                    String recipeString = "";
//                    if(recipeLocator.count() > 0){
//                        recipeString = recipeLocator.getAttribute("onclick").split("'")[3];
//                        recipeString = recipeString.replace("'", "");
//                        recipeString = recipeString.trim();
//                    }
//
//                    String priceUpdateTimestamp = priceUpdateLocator.getAttribute("data-order");
//                    LocalDateTime priceUpdateDateTime = Instant.ofEpochSecond(Long.parseLong(priceUpdateTimestamp)).atZone(ZoneId.systemDefault()).toLocalDateTime();
//
//                    String priceOne = priceOneLocator.innerText();
//                    priceOne = priceOne.replace(" ", "");
//                    priceOne = priceOne.replaceAll("[^\\d.]", "");
//
//                    String priceTen = priceTenLocator.innerText();
//                    priceTen = priceTen.replace(" ", "");
//                    priceTen = priceTen.replaceAll("[^\\d.]", "");
//
//                    String priceHundred = priceHundredLocator.innerText();
//                    priceHundred = priceHundred.replace(" ", "");
//                    priceHundred = priceHundred.replaceAll("[^\\d.]", "");
//
//                    ExternalItemDTO dto = new ExternalItemDTO();
//
//                    dto.setItemName(itemName);
//                    dto.setItemID(Integer.parseInt(id));
//                    dto.setRecipeString(recipeString.trim().isEmpty() ? null : recipeString);
//                    dto.setPriceUpdateTimestamp(priceUpdateDateTime);
//                    dto.setPriceOne(priceOne.trim().isEmpty() || priceOne.trim().equals("-") ? null : Integer.parseInt(priceOne));
//                    dto.setPriceTen(priceTen.trim().isEmpty() || priceTen.trim().equals("-") ? null : Integer.parseInt(priceTen));
//                    dto.setPriceHundred(priceHundred.trim().isEmpty() || priceHundred.trim().equals("-") ? null : Integer.parseInt(priceHundred));
//
//                    listeItems.add(dto);
//
//                    count++;
//                }
//
//                // On verifie si une autre page est disponible
//                Locator nextItemLocator = page.locator("#scanTable_next");
//                if(!nextItemLocator.getAttribute("class").contains("disabled")){
//                    nextItemLocator.click();
//                }
//                else{
//                    allFetched = true;
//                }
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
        int nbItems = listFromVulbis.size();
        int count = 1;

        try{
            for(ExternalItemDTO dto : listFromVulbis){
                if(count % 100 == 0){
                    System.out.println("Polling DofusDB : " + count + "/" + nbItems);
                }

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

                    count++;
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

        int nbItemsRecettables = 0;
        for(ExternalItemDTO dto : enrichedListWithDofusDB){
            if(dto.estCraftable()){
                nbItemsRecettables++;
            }
        }

        int count = 1;

        try{
            for(ExternalItemDTO dto : enrichedListWithDofusDB){
                if(dto.estCraftable()){
                    if(count % 20 == 0){
                        System.out.println("Polling Brifus : " + count + "/" + nbItemsRecettables);
                    }

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
                            count++;
                        }
                    }
                    catch(IOException ioe){
                        count++;
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
