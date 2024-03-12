package fr.pajonti.concasse.provider.external.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pajonti.concasse.helper.technical.ExitHandlerHelper;
import fr.pajonti.concasse.provider.database.dto.StatItemDTO;
import fr.pajonti.concasse.provider.external.dto.ExternalItemDTO;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class DofusDBDAO {

    private final List<ExternalItemDTO> listeInitialeItems;

    public DofusDBDAO(List<ExternalItemDTO> externalItemDTOS) {
        this.listeInitialeItems = externalItemDTOS;
    }

    public List<ExternalItemDTO> pollDataFromDofusDB() {
        List<ExternalItemDTO> listeEnrichie = new ArrayList<>();
        int count = 1;
        int listSize = listeInitialeItems.size();

        for(ExternalItemDTO itemInitial : this.listeInitialeItems){
            ExternalItemDTO itemEnrichi = enrichirItemWithStatsEtLevel(itemInitial);

            if(itemEnrichi != null){
                listeEnrichie.add(itemEnrichi);
            }
            System.out.println("[" + count + "/" + listSize +  "] Lecture de l'objet " + itemInitial.getItemName() + " dans DofusDB terminée.");
            count++;
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
                itemInitial.setEstConcassable(definirConcassable(typeNode.asInt()));

                for (Iterator<JsonNode> it = statsNode.elements(); it.hasNext(); ) {
                    JsonNode statNode = it.next();

                    //Category = 0 -> Uniquement les stats de l'item en flat ou %, pas ses autres effets
                    int category = statNode.path("category").asInt();
                    int statId = statNode.path("characteristic").asInt();
                    int statFrom = statNode.path("from").asInt();
                    int statTo = statNode.path("to").asInt();

                    int statUpper = Math.max(statFrom, statTo);
                    int statLower = Math.min(statFrom, statTo);

                    if(statUpper > 0 && statLower == 0){
                        statLower = statUpper;
                    }

                    if(statLower < 0 && statUpper == 0){
                        statUpper = statLower;
                    }

                    boolean statEstRelevant = statId >= 0 && statUpper != 0 && statLower != 0;

                    //On bloque la stat max pour rejeter les valeurs aberrantes
                    if(statEstRelevant && (category == 0 || category == 1) && statEstNonAberrante(statId, statLower, statUpper)){
                        //Si la stat est négative, on inverse le from et le to
                        itemInitial.addStatItem(new StatItemDTO(
                                statLower,
                                statUpper,
                                itemInitial.getItemID(),
                                statId
                        ));
                    }

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

        return itemInitial;
    }

    private boolean statEstNonAberrante(int statId, int statLower, int statUpper) {
        switch(statId){
            //Cas speficique : Arme de chasse
            case 0:
                return statUpper == 1;
            //Petites stat : Plafond a 15
            case 1:
            case 23:
            case 19:
            case 26:
                return statUpper <= 15;
            //Stats moyennes : Plafond à 100
            case 123:
            case 16:
            case 18:
            case 120:
            case 121:
            case 124:
            case 125:
            case 122:
            case 142:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 50:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 70:
            case 82:
            case 83:
            case 27:
            case 28:
            case 49:
            case 69:
            case 78:
            case 79:
            case 86:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
                return statUpper <= 100;
            //Grosses stat : Plafond à 900
            case 84:
            case 85:
            case 87:
            case 12:
            case 25:
            case 10:
            case 13:
            case 14:
            case 15:
            case 48:
            case 11:
            case 40:
            case 44:
            case 29:
                return statUpper <= 900;
            //Autres stats manquees : dans le doute on retourne OK
            default:
                return true;
        }
    }

    private boolean definirConcassable(Integer typeItem) {
        Set<Integer> set = new HashSet<>();

        set.add(2);     //Arc
        set.add(4);     //Baton
        set.add(3);     //Baguette
        set.add(1);     //Amulette
        set.add(9);     //Anneau
        set.add(5);     //Dague
        set.add(22);    //Faux
        set.add(19);    //Hache
        set.add(7);     //Marteau
        set.add(8);     //Pelle
        set.add(21);    //Pioche
        set.add(6);     //Epee
        set.add(16);    //Cape
        set.add(17);    //Chapeau
        set.add(10);    //Ceinture
        set.add(11);    //Bottes
        set.add(82);    //Bouclier

        return set.stream().anyMatch(i -> Objects.equals(i, typeItem));
    }
}
