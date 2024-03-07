package fr.pajonti.concasse.provider.database.dto;

import lombok.Getter;

@Getter
public class ItemDTO {
    private final Integer id;
    private final String name;
    private final Integer typeID;
    private final boolean estConcassable;

    public ItemDTO(Integer id, String name, Integer typeID, boolean estConcassable) {
        this.id = id;
        this.name = name;
        this.typeID = typeID;
        this.estConcassable = estConcassable;
    }

}
