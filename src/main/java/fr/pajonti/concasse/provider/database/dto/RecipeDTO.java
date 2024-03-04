package fr.pajonti.concasse.provider.database.dto;

public class RecipeDTO {
    private final Integer vulbisIDCraft;
    private final Integer vulbisIDComposant;
    private final Integer quantity;

    public RecipeDTO(Integer vulbisIDCraft, Integer vulbisIDComposant, Integer quantity) {
        this.vulbisIDCraft = vulbisIDCraft;
        this.vulbisIDComposant = vulbisIDComposant;
        this.quantity = quantity;
    }

    public Integer getVulbisIDCraft() {
        return vulbisIDCraft;
    }

    public Integer getVulbisIDComposant() {
        return vulbisIDComposant;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
