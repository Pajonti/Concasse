package fr.pajonti.concasse.provider.database.dto;

public class ServerDTO {
    private Integer serverID;
    private String nom;
    private Integer brifusID;

    public ServerDTO(Integer serverID, String nom, Integer brifusID) {
        this.serverID = serverID;
        this.nom = nom;
        this.brifusID = brifusID;
    }

    public Integer getServerID() {
        return serverID;
    }

    public void setServerID(Integer serverID) {
        this.serverID = serverID;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getBrifusID() {
        return brifusID;
    }

    public void setBrifusID(Integer brifusID) {
        this.brifusID = brifusID;
    }
}
