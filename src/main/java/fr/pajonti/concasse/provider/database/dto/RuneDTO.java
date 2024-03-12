package fr.pajonti.concasse.provider.database.dto;

public class RuneDTO {
    private final Integer itemID;
    private final Integer statID;
    private final Integer tier;
    private final Float weight;

    public RuneDTO(Integer itemID, Integer statID, Integer tier, Float weight) {
        this.itemID = itemID;
        this.statID = statID;
        this.tier = tier;
        this.weight = weight;
    }

    public Integer getItemID() {
        return itemID;
    }

    public Integer getStatID() {
        return statID;
    }

    public Integer getTier() {
        return tier;
    }

    public Float getWeight() {
        return weight;
    }
}
