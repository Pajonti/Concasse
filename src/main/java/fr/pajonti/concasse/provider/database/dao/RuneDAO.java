package fr.pajonti.concasse.provider.database.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.provider.database.DatabaseDAO;
import fr.pajonti.concasse.provider.database.dto.ItemDTO;
import fr.pajonti.concasse.provider.database.dto.RuneDTO;
import fr.pajonti.concasse.provider.database.dto.StatEnum;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RuneDAO extends DatabaseDAO {
    public RuneDAO(Configuration configuration) throws SQLException {
        super(configuration);
    }

    @Override
    protected void finalize(){
        try{
            this.connection.close();
        }
        catch (SQLException se){
            //Do nothing
        }
    }

    public void register(RuneDTO dto) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("MERGE INTO RUNE (VULBIS_ID, STAT_ID, TIER, WEIGHT) VALUES (" + dto.getItemID() + ", " + dto.getStatID() + ", " + dto.getTier() + ", " + dto.getWeight() + ");");
        statement.close();
    }

    /**
     * Les IDs des runes sont connus d'avance, mais ne peuvent pas etre inseres avant les items. Aussi, on les injecte via
     * une requete DAO factice une fois les items insérés
     * @return Liste de RuneDTO
     */
    public List<RuneDTO> getRunesStatic() {
        List<RuneDTO> list = new ArrayList<>();

        //Runes Tier 3
        list.add(new RuneDTO(1551, StatEnum.FORCE.getStatCode(), 3, (float) 1));
        list.add(new RuneDTO(1552, StatEnum.SAGESSE.getStatCode(), 3, (float) 3));
        list.add(new RuneDTO(1553, StatEnum.INTELLIGENCE.getStatCode(), 3, (float) 1));
        list.add(new RuneDTO(1554, StatEnum.VITALITE.getStatCode(), 3, (float) 0.2));
        list.add(new RuneDTO(1555, StatEnum.AGILITE.getStatCode(), 3, (float) 1));
        list.add(new RuneDTO(1556, StatEnum.CHANCE.getStatCode(), 3, (float) 1));
        list.add(new RuneDTO(7445, StatEnum.PODS.getStatCode(), 3, (float) 0.25));
        list.add(new RuneDTO(7450, StatEnum.INITIATIVE.getStatCode(), 3, (float) 0.1));
        list.add(new RuneDTO(10616, StatEnum.PUISSANCE_PIEGES.getStatCode(), 3, (float) 2));
        list.add(new RuneDTO(10619, StatEnum.PUISSANCE.getStatCode(), 3, (float) 2));


        //Runes Tier 2
        list.add(new RuneDTO(1545, StatEnum.FORCE.getStatCode(), 2, (float) 1));
        list.add(new RuneDTO(1546, StatEnum.SAGESSE.getStatCode(), 2, (float) 3));
        list.add(new RuneDTO(1547, StatEnum.INTELLIGENCE.getStatCode(), 2, (float) 1));
        list.add(new RuneDTO(1548, StatEnum.VITALITE.getStatCode(), 2, (float) 0.2));
        list.add(new RuneDTO(1549, StatEnum.AGILITE.getStatCode(), 2, (float) 1));
        list.add(new RuneDTO(1550, StatEnum.CHANCE.getStatCode(), 2, (float) 1));
        list.add(new RuneDTO(7444, StatEnum.PODS.getStatCode(), 2, (float) 0.25));
        list.add(new RuneDTO(7449, StatEnum.INITIATIVE.getStatCode(), 2, (float) 0.1));
        list.add(new RuneDTO(10613, StatEnum.DOMMAGES_PIEGES.getStatCode(), 2, (float) 5));
        list.add(new RuneDTO(10615, StatEnum.PUISSANCE_PIEGES.getStatCode(), 2, (float) 2));
        list.add(new RuneDTO(10618, StatEnum.PUISSANCE.getStatCode(), 2, (float) 2));
        list.add(new RuneDTO(10662, StatEnum.PROSPECTION.getStatCode(), 2, (float) 3));
        list.add(new RuneDTO(11638, StatEnum.FUITE.getStatCode(), 2, (float) 4));
        list.add(new RuneDTO(11640, StatEnum.TACLE.getStatCode(), 2, (float) 2));
        list.add(new RuneDTO(11642, StatEnum.ESQUIVE_PA.getStatCode(), 2, (float) 7));
        list.add(new RuneDTO(11644, StatEnum.ESQUIVE_PM.getStatCode(), 2, (float) 7));
        list.add(new RuneDTO(11646, StatEnum.RETRAIT_PA.getStatCode(), 2, (float) 7));
        list.add(new RuneDTO(11648, StatEnum.RETRAIT_PM.getStatCode(), 2, (float) 7));
        list.add(new RuneDTO(11650, StatEnum.DOMMAGES_POUSSEE.getStatCode(), 2, (float) 5));
        list.add(new RuneDTO(11652, StatEnum.RESISTANCE_POUSSEE.getStatCode(), 2, (float) 2));
        list.add(new RuneDTO(11654, StatEnum.DOMMAGES_CRITIQUES.getStatCode(), 2, (float) 5));
        list.add(new RuneDTO(11656, StatEnum.RESISTANCE_CRITIQUE.getStatCode(), 2, (float) 5));
        list.add(new RuneDTO(11658, StatEnum.DOMMAGES_FIXE_TERRE.getStatCode(), 2, (float) 5));
        list.add(new RuneDTO(11660, StatEnum.DOMMAGES_FIXE_FEU.getStatCode(), 2, (float) 5));
        list.add(new RuneDTO(11662, StatEnum.DOMMAGES_FIXE_EAU.getStatCode(), 2, (float) 5));
        list.add(new RuneDTO(11664, StatEnum.DOMMAGES_FIXE_AIR.getStatCode(), 2, (float) 5));
        list.add(new RuneDTO(11666, StatEnum.DOMMAGES_FIXE_NEUTRE.getStatCode(), 2, (float) 5));
        list.add(new RuneDTO(19337, StatEnum.SOIN.getStatCode(), 2, (float) 10));
        list.add(new RuneDTO(19338, StatEnum.RESISTANCE_FIXE_AIR.getStatCode(), 2, (float) 2));
        list.add(new RuneDTO(19339, StatEnum.RESISTANCE_FIXE_EAU.getStatCode(), 2, (float) 2));
        list.add(new RuneDTO(19340, StatEnum.RESISTANCE_FIXE_FEU.getStatCode(), 2, (float) 2));
        list.add(new RuneDTO(19341, StatEnum.RESISTANCE_FIXE_NEUTRE.getStatCode(), 2, (float) 2));
        list.add(new RuneDTO(19342, StatEnum.RESISTANCE_FIXE_TERRE.getStatCode(), 2, (float) 2));


        //Runes Tier 1
        list.add(new RuneDTO(1519, StatEnum.FORCE.getStatCode(), 1, (float) 1));
        list.add(new RuneDTO(1521, StatEnum.SAGESSE.getStatCode(), 1, (float) 3));
        list.add(new RuneDTO(1522, StatEnum.INTELLIGENCE.getStatCode(), 1, (float) 1));
        list.add(new RuneDTO(1523, StatEnum.VITALITE.getStatCode(), 1, (float) 0.2));
        list.add(new RuneDTO(1524, StatEnum.AGILITE.getStatCode(), 1, (float) 1));
        list.add(new RuneDTO(1525, StatEnum.CHANCE.getStatCode(), 1, (float) 1));
        list.add(new RuneDTO(1557, StatEnum.PA.getStatCode(), 1, (float) 100));
        list.add(new RuneDTO(1558, StatEnum.PM.getStatCode(), 1, (float) 90));
        list.add(new RuneDTO(7433, StatEnum.POURCENTAGE_CRIT.getStatCode(), 1, (float) 10));
        list.add(new RuneDTO(7434, StatEnum.SOIN.getStatCode(), 1, (float) 10));
        list.add(new RuneDTO(7435, StatEnum.DOMMAGES.getStatCode(), 1, (float) 5));
        list.add(new RuneDTO(7436, StatEnum.PUISSANCE.getStatCode(), 1, (float) 2));
        list.add(new RuneDTO(7437, StatEnum.DOMMAGES_RENVOI.getStatCode(), 1, (float) 20));
        list.add(new RuneDTO(7438, StatEnum.PORTEE.getStatCode(), 1, (float) 51));
        list.add(new RuneDTO(7442, StatEnum.INVOCATION.getStatCode(), 1, (float) 30));
        list.add(new RuneDTO(7443, StatEnum.PODS.getStatCode(), 1, (float) 0.25));
        list.add(new RuneDTO(7446, StatEnum.DOMMAGES_PIEGES.getStatCode(), 1, (float) 5));
        list.add(new RuneDTO(7447, StatEnum.PUISSANCE_PIEGES.getStatCode(), 1, (float) 2));
        list.add(new RuneDTO(7448, StatEnum.INITIATIVE.getStatCode(), 1, (float) 0.1));
        list.add(new RuneDTO(7451, StatEnum.PROSPECTION.getStatCode(), 1, (float) 3));
        list.add(new RuneDTO(7452, StatEnum.RESISTANCE_FIXE_FEU.getStatCode(), 1, (float) 2));
        list.add(new RuneDTO(7453, StatEnum.RESISTANCE_FIXE_AIR.getStatCode(), 1, (float) 2));
        list.add(new RuneDTO(7454, StatEnum.RESISTANCE_FIXE_EAU.getStatCode(), 1, (float) 2));
        list.add(new RuneDTO(7455, StatEnum.RESISTANCE_FIXE_TERRE.getStatCode(), 1, (float) 2));
        list.add(new RuneDTO(7456, StatEnum.RESISTANCE_FIXE_NEUTRE.getStatCode(), 1, (float) 2));
        list.add(new RuneDTO(7457, StatEnum.RESISTANCE_POURCENTAGE_FEU.getStatCode(), 1, (float) 6));
        list.add(new RuneDTO(7458, StatEnum.RESISTANCE_POURCENTAGE_AIR.getStatCode(), 1, (float) 6));
        list.add(new RuneDTO(7459, StatEnum.RESISTANCE_POURCENTAGE_TERRE.getStatCode(), 1, (float) 6));
        list.add(new RuneDTO(7460, StatEnum.RESISTANCE_POURCENTAGE_NEUTRE.getStatCode(), 1, (float) 6));
        list.add(new RuneDTO(7560, StatEnum.RESISTANCE_POURCENTAGE_EAU.getStatCode(), 1, (float) 6));
        list.add(new RuneDTO(10057, StatEnum.ARME_CHASSE.getStatCode(), 1, (float) 5));
        list.add(new RuneDTO(11637, StatEnum.FUITE.getStatCode(), 1, (float) 4));
        list.add(new RuneDTO(11639, StatEnum.TACLE.getStatCode(), 1, (float) 2));
        list.add(new RuneDTO(11641, StatEnum.ESQUIVE_PA.getStatCode(), 1, (float) 7));
        list.add(new RuneDTO(11643, StatEnum.ESQUIVE_PM.getStatCode(), 1, (float) 7));
        list.add(new RuneDTO(11645, StatEnum.RETRAIT_PA.getStatCode(), 1, (float) 7));
        list.add(new RuneDTO(11647, StatEnum.RETRAIT_PM.getStatCode(), 1, (float) 7));
        list.add(new RuneDTO(11649, StatEnum.DOMMAGES_POUSSEE.getStatCode(), 1, (float) 5));
        list.add(new RuneDTO(11651, StatEnum.RESISTANCE_POUSSEE.getStatCode(), 1, (float) 2));
        list.add(new RuneDTO(11653, StatEnum.DOMMAGES_CRITIQUES.getStatCode(), 1, (float) 5));
        list.add(new RuneDTO(11655, StatEnum.RESISTANCE_CRITIQUE.getStatCode(), 1, (float) 5));
        list.add(new RuneDTO(11657, StatEnum.DOMMAGES_FIXE_TERRE.getStatCode(), 1, (float) 5));
        list.add(new RuneDTO(11659, StatEnum.DOMMAGES_FIXE_FEU.getStatCode(), 1, (float) 5));
        list.add(new RuneDTO(11661, StatEnum.DOMMAGES_FIXE_EAU.getStatCode(), 1, (float) 5));
        list.add(new RuneDTO(11663, StatEnum.DOMMAGES_FIXE_AIR.getStatCode(), 1, (float) 5));
        list.add(new RuneDTO(11665, StatEnum.DOMMAGES_FIXE_NEUTRE.getStatCode(), 1, (float) 5));
        list.add(new RuneDTO(18719, StatEnum.DOMMAGES_MELEE.getStatCode(), 1, (float) 6));
        list.add(new RuneDTO(18720, StatEnum.DOMMAGES_A_DISTANCE.getStatCode(), 1, (float) 6));
        list.add(new RuneDTO(18721, StatEnum.DOMMAGES_ARMES.getStatCode(), 1, (float) 6));
        list.add(new RuneDTO(18722, StatEnum.DOMMAGES_SORTS.getStatCode(), 1, (float) 6));
        list.add(new RuneDTO(18723, StatEnum.RESISTANCE_POURCENTAGE_MELEE.getStatCode(), 1, (float) 6));
        list.add(new RuneDTO(18724, StatEnum.RESISTANCE_POURCENTAGE_DISTANTS.getStatCode(), 1, (float) 6));


        return list;
    }

    public List<RuneDTO> getRunes() throws SQLException {
        List<RuneDTO> list = new ArrayList<>();
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM RUNE;");

        while(rs.next()){
            list.add(new RuneDTO(rs.getInt("VULBIS_ID"), rs.getInt("STAT_ID"), rs.getInt("TIER"), rs.getFloat("WEIGHT")));
        }

        return list;
    }
}
