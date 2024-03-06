package fr.pajonti.concasse.provider.external.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pajonti.concasse.helper.technical.ExitHandlerHelper;
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

public class BrifusDAO {

    private final List<ExternalItemDTO> listeInitialeItems;
    private final ServerDTO server;

    public BrifusDAO(List<ExternalItemDTO> externalItemDTOS, ServerDTO server) {
        this.listeInitialeItems = externalItemDTOS;
        this.server = server;
    }

    public List<ExternalItemDTO> pollDataFromBrifus() {
        List<ExternalItemDTO> listeEnrichie = new ArrayList<>();

        for(ExternalItemDTO itemInitial : this.listeInitialeItems){
            ExternalItemDTO itemEnrichi = enrichirItemWithTauxBrisage(itemInitial);

            if(itemEnrichi != null){
                listeEnrichie.add(itemEnrichi);
            }
        }

        return listeEnrichie;
    }

    private ExternalItemDTO enrichirItemWithTauxBrisage(ExternalItemDTO itemInitial) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        try{
            if(itemInitial.estCraftable()){
                URL urlCoeff = new URL("https://api.brifus.fr/coeff");

                HttpURLConnection connCoeff = (HttpURLConnection) urlCoeff.openConnection();
                connCoeff.setRequestMethod("POST");
                connCoeff.setRequestProperty("Content-Type", "application/json");
                connCoeff.setRequestProperty("Accept", "application/json");

                connCoeff.setDoOutput(true);

                String body = "{\"item\":" + itemInitial.getItemID() + ",\"server\":" + server.getBrifusID() + "}";

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
                            itemInitial.setBrifusUpdateDate(LocalDateTime.from(df.parse(rootNode.path("dateTime").asText())));
                        }

                        Double tx = rootNode.path("coefficient").asDouble();
                        itemInitial.setTauxBrisage(tx.floatValue());
                    }
                }
                catch(IOException ioe){

                }
            }
        }
        catch (IOException ioe){
            ioe.printStackTrace();
            ExitHandlerHelper.exit("Erreur lors de la collecte des infos Brifus : " + ioe.getMessage());
        }

        return itemInitial;
    }
}
