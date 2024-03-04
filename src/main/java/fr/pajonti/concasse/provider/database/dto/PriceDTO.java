package fr.pajonti.concasse.provider.database.dto;

import java.time.LocalDateTime;

public class PriceDTO {
    private final Integer vulbisID;
    private final Integer serverID;
    private final Integer priceOne;
    private final Integer priceTen;
    private final Integer priceHundred;
    private final LocalDateTime refreshTimestamp;
    private final LocalDateTime priceUpdateTimestamp;

    public PriceDTO(Integer vulbisID, Integer serverID, Integer priceOne, Integer priceTen, Integer priceHundred, LocalDateTime refreshTimestamp, LocalDateTime priceUpdateTimestamp) {
        this.vulbisID = vulbisID;
        this.serverID = serverID;
        this.priceOne = priceOne;
        this.priceTen = priceTen;
        this.priceHundred = priceHundred;
        this.refreshTimestamp = refreshTimestamp;
        this.priceUpdateTimestamp = priceUpdateTimestamp;
    }

    public Integer getVulbisID() {
        return vulbisID;
    }

    public Integer getServerID() {
        return serverID;
    }

    public Integer getPriceOne() {
        return priceOne;
    }

    public Integer getPriceTen() {
        return priceTen;
    }

    public Integer getPriceHundred() {
        return priceHundred;
    }

    public LocalDateTime getRefreshTimestamp() {
        return refreshTimestamp;
    }

    public LocalDateTime getPriceUpdateTimestamp() {
        return priceUpdateTimestamp;
    }
}
