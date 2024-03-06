package fr.pajonti.concasse.provider.external.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pajonti.concasse.helper.technical.ExitHandlerHelper;
import fr.pajonti.concasse.provider.database.dto.StatItemDTO;
import fr.pajonti.concasse.provider.external.dto.ExternalItemDTO;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class DofusDBDAO {

    private final List<ExternalItemDTO> listeInitialeItems;

    public DofusDBDAO(List<ExternalItemDTO> externalItemDTOS) {
        this.listeInitialeItems = externalItemDTOS;
    }

    public List<ExternalItemDTO> pollDataFromDofusDB() {
        List<ExternalItemDTO> listeEnrichie = new ArrayList<>();

        for(ExternalItemDTO itemInitial : this.listeInitialeItems){
            ExternalItemDTO itemEnrichi = enrichirItemWithStatsEtLevel(itemInitial);

            if(itemEnrichi != null){
                listeEnrichie.add(itemEnrichi);
            }
        }

        return listeEnrichie;
    }

    private ExternalItemDTO enrichirItemWithStatsEtLevel(ExternalItemDTO itemInitial) {
        try{
            //Préparation de la requête vers DofusDB
            URL url = new URL("https://api.dofusdb.fr/items/" + itemInitial.getItemID());
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

                itemInitial.setLevel(levelNode.asInt());
                itemInitial.setTypeId(typeNode.asInt());

                for (Iterator<JsonNode> it = statsNode.elements(); it.hasNext(); ) {
                    JsonNode statNode = it.next();

                    Integer statId = statNode.path("characteristic").asInt();
                    Integer statFrom = statNode.path("from").asInt();
                    Integer statTo = statNode.path("to").asInt();

                    boolean estNegative = (statFrom < 0 && statTo < 0);

                    //Si la stat est négative, on inverse le from et le to
                    itemInitial.addStatItem(new StatItemDTO(
                            (estNegative ? statTo : statFrom),
                            (estNegative ? statFrom : statTo),
                            itemInitial.getItemID(),
                            statId
                    ));
                }
            }
            else{
                System.err.println("Impossible de collecter l'objet " + itemInitial.getItemName() + ". Ce dernier sera ignoré.");
            }

        }
        catch (IOException ioe){
            ioe.printStackTrace();
            ExitHandlerHelper.exit("Erreur lors de la collecte des infos DofusDB : " + ioe.getMessage());
        }

        return null;
    }
}
