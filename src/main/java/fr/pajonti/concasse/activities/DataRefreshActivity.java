package fr.pajonti.concasse.activities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.helper.technical.ExitHandlerHelper;
import fr.pajonti.concasse.provider.database.dto.ServerDTO;
import fr.pajonti.concasse.provider.database.dto.StatItemDTO;
import fr.pajonti.concasse.provider.external.dao.BrifusDAO;
import fr.pajonti.concasse.provider.external.dao.DofusDBDAO;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    }

    private static List<ExternalItemDTO> pollExternalDataset() {
        /* Collecte des infos Vulbis */
        List<ExternalItemDTO> listFromVulbis = new VulbisDAO(server).pollDataFromVulbis();

        /* Collecte des infos DofusDB */
        List<ExternalItemDTO> listAfterDofusDB = new DofusDBDAO(listFromVulbis).pollDataFromDofusDB();

        /* Collecte des infos dans Brifus */
        List<ExternalItemDTO> listAfterBrifus = new BrifusDAO(listAfterDofusDB, server).pollDataFromBrifus();

        return listAfterBrifus;
    }
}
