package fr.pajonti.concasse.provider.database.dto;

public class StatItemDTO {
    private Integer statLower;
    private Integer statUpper;
    private Integer itemID;
    private Integer statID;

    public StatItemDTO(Integer statLower, Integer statUpper, Integer itemID, Integer statID) {
        this.statLower = statLower;
        this.statUpper = statUpper;
        this.itemID = itemID;
        this.statID = statID;
    }

    public Integer getStatLower() {
        return statLower;
    }

    public void setStatLower(Integer statLower) {
        this.statLower = statLower;
    }

    public Integer getStatUpper() {
        return statUpper;
    }

    public void setStatUpper(Integer statUpper) {
        this.statUpper = statUpper;
    }

    public Integer getItemID() {
        return itemID;
    }

    public void setItemID(Integer itemID) {
        this.itemID = itemID;
    }

    public Integer getStatID() {
        return statID;
    }

    public void setStatID(Integer statID) {
        this.statID = statID;
    }
}
