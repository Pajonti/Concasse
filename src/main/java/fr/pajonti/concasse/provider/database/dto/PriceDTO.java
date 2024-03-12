package fr.pajonti.concasse.provider.database.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
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

}
