package fr.pajonti.concasse.provider.external.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.provider.database.dto.ServerDTO;
import fr.pajonti.concasse.provider.external.dto.ExternalItemDTO;

import java.util.ArrayList;
import java.util.List;

public class VulbisDAO {
    private final Configuration configuration;
    private final ServerDTO server;

    public VulbisDAO(Configuration configuration, ServerDTO server) {
        this.configuration = configuration;
        this.server = server;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ServerDTO getServer() {
        return server;
    }

    public List<ExternalItemDTO> pollDataFromVulbis() {
        List<ExternalItemDTO> list = new ArrayList<>();

        return list;
    }
}
