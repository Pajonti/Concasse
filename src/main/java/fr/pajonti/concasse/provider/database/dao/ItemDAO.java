package fr.pajonti.concasse.provider.database.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.provider.database.DatabaseDAO;
import fr.pajonti.concasse.provider.database.dto.ItemDTO;
import fr.pajonti.concasse.provider.database.dto.ServerDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO extends DatabaseDAO {
    public ItemDAO(Configuration configuration) throws SQLException {
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

    public void register(ItemDTO dto) throws SQLException {
        String sanitizedName = dto.getName().replace("'", "''");
        Statement statement = connection.createStatement();
        statement.executeUpdate("MERGE INTO ITEM (VULBIS_ID, NOM, TYPE_ID, EST_CONCASSABLE, LEVEL) VALUES (" + dto.getId() + ", '" + sanitizedName + "', " + dto.getTypeID() + ", " + (dto.isEstConcassable() ? 1 : 0) + ", " + dto.getLevel() + ");");
        statement.close();
    }

    public ItemDTO loadItemByID(Integer componentID) throws SQLException {
        ItemDTO item = null;
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT VULBIS_ID, NOM, TYPE_ID, EST_CONCASSABLE, LEVEL FROM ITEM WHERE VULBIS_ID = " + componentID);

        while(rs.next()){
            item = new ItemDTO(rs.getInt("VULBIS_ID"), rs.getString("NOM"), rs.getInt("TYPE_ID"), rs.getBoolean("EST_CONCASSABLE"), rs.getInt("LEVEL"));
        }

        return item;
    }

    public List<ItemDTO> getCraftableItemsAllowedByMetier(ServerDTO serverDTO, Integer niveauMinimum) throws SQLException {
        List<ItemDTO> list = new ArrayList<>();
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM ITEM " +
                "INNER JOIN METIER_PEUT_CRAFT MPC ON ITEM.TYPE_ID = MPC.TYPE_ID " +
                "INNER JOIN NIVEAU_METIER NM ON MPC.METIER_ID = NM.METIER_ID " +
                "WHERE ITEM.LEVEL >= " + niveauMinimum + " AND NM.SERVER_ID = " + serverDTO.getServerID() + " AND " +
                "NM.NIVEAU >= ITEM.LEVEL AND ITEM.VULBIS_ID IN (SELECT VULBIS_ID_CRAFT FROM RECIPE)");

        while(rs.next()){
            list.add(new ItemDTO(rs.getInt("VULBIS_ID"), rs.getString("NOM"), rs.getInt("TYPE_ID"), rs.getBoolean("EST_CONCASSABLE"), rs.getInt("LEVEL")));
        }

        return list;
    }
}
