package fr.pajonti.concasse.provider.database.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.provider.database.DatabaseDAO;
import fr.pajonti.concasse.provider.database.dto.ItemDTO;

import java.sql.*;

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
        statement.executeUpdate("MERGE INTO ITEM (VULBIS_ID, NOM, TYPE_ID) VALUES (" + dto.getId() + ", '" + sanitizedName + "', " + dto.getTypeID() + ");");
        statement.close();
    }
}
