package fr.pajonti.concasse.provider.external.dao;

import com.microsoft.playwright.*;
import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.helper.technical.ExitHandlerHelper;
import fr.pajonti.concasse.provider.database.dto.ServerDTO;
import fr.pajonti.concasse.provider.external.dto.ExternalItemDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class VulbisDAO {
    private final ServerDTO server;

    public VulbisDAO(ServerDTO server) {
        this.server = server;
    }

    public List<ExternalItemDTO> pollDataFromVulbis() {
        List<ExternalItemDTO> list = new ArrayList<>();

        String xmlDatasetFromVulbis = getXMLDatasetFromVulbis();

        if(!xmlDatasetFromVulbis.trim().isEmpty()){
            try{
                list = parseItemsFromXML(xmlDatasetFromVulbis);
            }
            catch (ParserConfigurationException | IOException | SAXException e){
                e.printStackTrace();
                ExitHandlerHelper.exit("Erreur lors de la collecte du flux XML Vulbis : " + e.getMessage());
            }
        }

        return list;
    }

    private List<ExternalItemDTO> parseItemsFromXML(String xml) throws ParserConfigurationException, IOException, SAXException {
        List<ExternalItemDTO> listeItems = new ArrayList<>();
        Document document = Jsoup.parse(xml);
        Elements table = document.select("#scanTable").select("tbody").select("tr");

        for(Element tableLine : table){
            Elements nameAndIdElements = tableLine.select("td:nth-child(2) > div:nth-child(1) > p");
            Elements recipeElements = tableLine.select("td:nth-child(2) > div:nth-child(2) > p");
            Elements priceUpdateElement = tableLine.select("td:nth-child(4)");
            Elements priceOneElement = tableLine.select("td:nth-child(6) > p");
            Elements priceTenElement = tableLine.select("td:nth-child(7) > p");
            Elements priceHundredElement = tableLine.select("td:nth-child(8) > p");

            String itemName = nameAndIdElements.text();
            itemName = itemName.replace("[", "");
            itemName = itemName.replace("]", "");
            itemName = itemName.replace("*", "");

            String id = nameAndIdElements.attr("onclick").split(",")[1];
            id = id.replace("'", "");
            id = id.trim();

            String recipeString = "";
            for(Element element : recipeElements){
                String txtVal = element.ownText();
                if(txtVal.equals("[Recette]")){
                    recipeString = element.attr("onclick").split("'")[3];
                    recipeString = recipeString.replace("'", "");
                    recipeString = recipeString.trim();

                    break;
                }
            }

            String priceUpdateTimestamp = priceUpdateElement.attr("data-order");
            LocalDateTime priceUpdateDateTime = Instant.ofEpochSecond(Long.parseLong(priceUpdateTimestamp)).atZone(ZoneId.systemDefault()).toLocalDateTime();

            String priceOne = priceOneElement.text();
            priceOne = priceOne.replace(" ", "");
            priceOne = priceOne.replaceAll("[^\\d.]", "");

            String priceTen = priceTenElement.text();
            priceTen = priceTen.replace(" ", "");
            priceTen = priceTen.replaceAll("[^\\d.]", "");

            String priceHundred = priceHundredElement.text();
            priceHundred = priceHundred.replace(" ", "");
            priceHundred = priceHundred.replaceAll("[^\\d.]", "");

            //Peuplement du DTO avec les infos collectées
            ExternalItemDTO dto = new ExternalItemDTO();

            dto.setItemName(itemName);
            dto.setItemID(Integer.parseInt(id));
            dto.setRecipeString(recipeString.trim().isEmpty() ? null : recipeString);
            dto.setPriceUpdateTimestamp(priceUpdateDateTime);
            dto.setPriceOne(priceOne.trim().isEmpty() || priceOne.trim().equals("-") ? null : Integer.parseInt(priceOne));
            dto.setPriceTen(priceTen.trim().isEmpty() || priceTen.trim().equals("-") ? null : Integer.parseInt(priceTen));
            dto.setPriceHundred(priceHundred.trim().isEmpty() || priceHundred.trim().equals("-") ? null : Integer.parseInt(priceHundred));

            System.out.println("Lecture de l'objet " + itemName + " dans Vulbis terminée.");

            listeItems.add(dto);
        }

        return listeItems;
    }

    private String getXMLDatasetFromVulbis() {
        System.out.println("Recherche des données d'objet dans Vulbis pour le serveur " + server.getNom());

        //Creation du navigateur embarque qui va venir collecter les donnees
        try (Playwright playwright = Playwright.create()) {
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
            launchOptions.setHeadless(false);
            launchOptions.setSlowMo(300);
            launchOptions.setTimeout(240000);
            Browser browser = playwright.firefox().launch(launchOptions);
            Page page = browser.newPage();
            page.setDefaultTimeout(240000);
            page.setDefaultNavigationTimeout(240000);

            //Toutes donnees confondues
//            page.navigate("https://www.vulbis.com/?server=" + server.getNom().replace(" ", "%20") + "&gids=&percent=0&craftableonly=false&select-type=-1&sellchoice=false&buyqty=1&sellqty=1&percentsell=0");
            page.navigate("https://www.vulbis.com/?server=" + server.getNom().replace(" ", "%20") + "&gids=&percent=0&craftableonly=false&select-type=9&sellchoice=false&buyqty=1&sellqty=1&percentsell=0");

            //Ready-check du navigateur
            page.waitForSelector("#scanTable > tbody > tr:nth-child(1) > td:nth-child(2) > div:nth-child(1) > p");

            //Déclenchement du passage en mono-tableau
            page.evaluate("let amountSel=document.getElementsByName(\"scanTable_length\");amountSel[0].options[7].selected = true;amountSel[0].dispatchEvent(new Event('change'));");

            //Nombre de lignes a lire
            Locator linesLocator = page.locator("#scanTable_info");
            String linesLocatorValue = linesLocator.innerText().split("sur")[1].replace("objets", "").trim().replace(".", "");
            int nbLignes = Integer.parseInt(linesLocatorValue);

            System.out.println("Browser Vulbis chargé, lecture des " + nbLignes + " éléments");

            //Conversion du contenu XML en String
            Locator tableauItems = page.locator("#scanTable_wrapper");
            return tableauItems.innerHTML().trim()/*.replace("\t", "").replace("\n", "")*/;
        }
    }
}
