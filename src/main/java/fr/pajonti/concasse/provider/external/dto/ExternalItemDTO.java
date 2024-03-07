    package fr.pajonti.concasse.provider.external.dto;

import fr.pajonti.concasse.provider.database.dto.StatItemDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO stockant les donnees d'item issues des services externes a Concasse (Vulbis, Brifus, DofusDB,...)
 */
@Getter
@Setter
public class ExternalItemDTO {

    private String itemName;
    private Integer itemID;
    private String recipeString;
    private Integer priceOne;
    private Integer priceTen;
    private Integer priceHundred;
    private LocalDateTime priceUpdateTimestamp;
    private Integer level;
    private Integer typeId;
    private boolean estConcassable;
    private List<StatItemDTO> statItemList;
    private Float tauxBrisage;
    private LocalDateTime brifusUpdateDate;

    public ExternalItemDTO(){
        statItemList = new ArrayList<>();
    }

    public void addStatItem(StatItemDTO statItemDTO){
        this.statItemList.add(statItemDTO);
    }

    public boolean estCraftable() {
       return this.getRecipeString() != null && !this.getRecipeString().trim().isEmpty();
    }

    public boolean possedeStats(){
        if(this.statItemList.isEmpty()){
            return false;
        }

        //Si au moins une stat est positive, alors l'item est concassable
        for(StatItemDTO statItem : this.statItemList){
            if(statItem.getStatLower() >= 0 && statItem.getStatUpper() >= 0){
                return true;
            }
        }

        return false;
    }
}
