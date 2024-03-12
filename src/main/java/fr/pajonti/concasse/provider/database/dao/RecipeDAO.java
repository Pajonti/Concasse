package fr.pajonti.concasse.provider.database.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.provider.database.DatabaseDAO;
import fr.pajonti.concasse.provider.database.dto.RecipeDTO;
import fr.pajonti.concasse.provider.database.dto.RuneDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RecipeDAO extends DatabaseDAO {
    public RecipeDAO(Configuration configuration) throws SQLException {
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

    public void register(RecipeDTO dto) throws SQLException {

        Statement statement = connection.createStatement();
        statement.executeUpdate("MERGE INTO RECIPE (VULBIS_ID_CRAFT, VULBIS_ID_COMPONENT, QUANTITY) " +
                                    "VALUES (" + dto.getVulbisIDCraft() + ", " + dto.getVulbisIDComposant() + ", " + dto.getQuantity() + ");");
        statement.close();
    }

    public List<RecipeDTO> collecterRecette(Integer itemID) throws SQLException {
        List<RecipeDTO> list = new ArrayList<>();
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM RECIPE WHERE VULBIS_ID_CRAFT = " + itemID + ";");

        while(rs.next()){
            list.add(new RecipeDTO(rs.getInt("VULBIS_ID_CRAFT"), rs.getInt("VULBIS_ID_COMPONENT"), rs.getInt("QUANTITY")));
        }

        return list;
    }
}
