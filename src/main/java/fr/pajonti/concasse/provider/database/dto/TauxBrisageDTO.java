package fr.pajonti.concasse.provider.database.dto;

import java.time.LocalDateTime;

public class TauxBrisageDTO {
    private final Integer vulbisID;
    private final Integer serverID;
    private final Float tauxConcassage;
    private final LocalDateTime refreshTimestamp;
    private final LocalDateTime txUpdateTimestamp;

    public TauxBrisageDTO(Integer vulbisID, Integer serverID, Float tauxConcassage, LocalDateTime refreshTimestamp, LocalDateTime txUpdateTimestamp) {
        this.vulbisID = vulbisID;
        this.serverID = serverID;
        this.tauxConcassage = tauxConcassage;
        this.refreshTimestamp = refreshTimestamp;
        this.txUpdateTimestamp = txUpdateTimestamp;
    }

    public Integer getVulbisID() {
        return vulbisID;
    }

    public Integer getServerID() {
        return serverID;
    }

    public Float getTauxConcassage() {
        return tauxConcassage;
    }

    public LocalDateTime getRefreshTimestamp() {
        return refreshTimestamp;
    }

    public LocalDateTime getTxUpdateTimestamp() {
        return txUpdateTimestamp;
    }
}
