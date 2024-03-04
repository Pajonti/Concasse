package fr.pajonti.concasse.provider.database.dto;

public class ItemDTO {
    private final Integer id;
    private final String name;
    private final Integer typeID;

    public ItemDTO(Integer id, String name, Integer typeID) {
        this.id = id;
        this.name = name;
        this.typeID = typeID;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getTypeID() {
        return typeID;
    }
}
