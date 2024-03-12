package fr.pajonti.concasse.provider.database.dto;

public class StatItemDTO {
    private final Integer statLower;
    private final Integer statUpper;
    private final Integer itemID;
    private final Integer statID;

    public StatItemDTO(Integer statLower, Integer statUpper, Integer itemID, Integer statID) {
        this.statLower = statLower;
        this.statUpper = statUpper;
        this.itemID = itemID;
        this.statID = statID;
    }

    public Integer getStatLower() {
        return statLower;
    }

    public Integer getStatUpper() {
        return statUpper;
    }

    public Integer getItemID() {
        return itemID;
    }

    public Integer getStatID() {
        return statID;
    }

    public Integer getStatMoyenne() {
        //En cas de stat statique positive:
        if(this.statLower == 0 && this.statUpper > 0){
            return statUpper;
        }

        //En cas de stat statique negative:
        if(this.statLower < 0 && this.statUpper == 0){
            return statLower;
        }

        return (statUpper + statLower) / 2;
    }

    public boolean estFocussable() {
        return statLower >= 0 && statUpper > 0;
    }
}
