package fr.pajonti.concasse.provider.database.dto;

public class NiveauMetierDTO {
    private int metierID;
    private int serverID;
    private int niveauMetier;

    public NiveauMetierDTO(int metierID, int serverID, int niveauMetier) {
        this.metierID = metierID;
        this.serverID = serverID;
        this.niveauMetier = niveauMetier;
    }

    public int getMetierID() {
        return metierID;
    }

    public void setMetierID(int metierID) {
        this.metierID = metierID;
    }

    public int getServerID() {
        return serverID;
    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    public int getNiveauMetier() {
        return niveauMetier;
    }

    public void setNiveauMetier(int niveauMetier) {
        this.niveauMetier = niveauMetier;
    }
}
