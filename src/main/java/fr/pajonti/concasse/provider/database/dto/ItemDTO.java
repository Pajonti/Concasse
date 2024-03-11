package fr.pajonti.concasse.provider.database.dto;

import lombok.Getter;

@Getter
public class ItemDTO {
    private final Integer id;
    private final String name;
    private final Integer typeID;
    private final boolean estConcassable;
    private final Integer level;

    public ItemDTO(Integer id, String name, Integer typeID, boolean estConcassable, Integer level) {
        this.id = id;
        this.name = name;
        this.typeID = typeID;
        this.estConcassable = estConcassable;
        this.level = level;
    }

}
