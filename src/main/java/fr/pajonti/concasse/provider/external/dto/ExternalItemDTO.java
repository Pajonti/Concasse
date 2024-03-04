package fr.pajonti.concasse.provider.external.dto;

import fr.pajonti.concasse.provider.database.dto.StatItemDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO stockant les donnees d'item issues des services externes a Concasse (Vulbis, Brifus, DofusDB,...)
 */
public class ExternalItemDTO {

    private String itemName;
    private Integer itemID;
    private String recipeString;
    private Integer priceOne;
    private Integer priceTen;
    private Integer priceHundred;
    private Integer level;
    private Integer typeId;
    private List<StatItemDTO> statItemList;
    /**
     * Vaut 0 si aucune donnée dans Brifus
     */
    private Float tauxBrisage;
    private LocalDateTime brifusUpdateDate;

    public ExternalItemDTO(){
        statItemList = new ArrayList<>();
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getItemID() {
        return itemID;
    }

    public void setItemID(Integer itemID) {
        this.itemID = itemID;
    }

    public String getRecipeString() {
        return recipeString;
    }

    public void setRecipeString(String recipeString) {
        this.recipeString = recipeString;
    }

    public Integer getPriceOne() {
        return priceOne;
    }

    public void setPriceOne(Integer priceOne) {
        this.priceOne = priceOne;
    }

    public Integer getPriceTen() {
        return priceTen;
    }

    public void setPriceTen(Integer priceTen) {
        this.priceTen = priceTen;
    }

    public Integer getPriceHundred() {
        return priceHundred;
    }

    public void setPriceHundred(Integer priceHundred) {
        this.priceHundred = priceHundred;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public List<StatItemDTO> getStatItemList() {
        return statItemList;
    }

    public void setStatItemList(List<StatItemDTO> statItemList) {
        this.statItemList = statItemList;
    }

   public void addStatItem(StatItemDTO statItemDTO){
        this.statItemList.add(statItemDTO);
   }

    public Float getTauxBrisage() {
        return tauxBrisage;
    }

    public void setTauxBrisage(Float tauxBrisage) {
        this.tauxBrisage = tauxBrisage;
    }

    public LocalDateTime getBrifusUpdateDate() {
        return brifusUpdateDate;
    }

    public void setBrifusUpdateDate(LocalDateTime brifusUpdateDate) {
        this.brifusUpdateDate = brifusUpdateDate;
    }
}
