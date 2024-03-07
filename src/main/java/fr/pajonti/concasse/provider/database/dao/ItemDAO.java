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
        statement.executeUpdate("MERGE INTO ITEM (VULBIS_ID, NOM, TYPE_ID, EST_CONCASSABLE) VALUES (" + dto.getId() + ", '" + sanitizedName + "', " + dto.getTypeID() + ", " + (dto.isEstConcassable() ? 1 : 0) + ");");
        statement.close();
    }

    public ItemDTO loadItemByID(Integer componentID) throws SQLException {
        ItemDTO item = null;
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT VULBIS_ID, NOM, TYPE_ID, EST_CONCASSABLE FROM ITEM");

        while(rs.next()){
            item = new ItemDTO(rs.getInt("VULBIS_ID"), rs.getString("NOM"), rs.getInt("TYPE_ID"), rs.getBoolean("EST_CONCASSABLE"));
        }

        return item;
    }
}
