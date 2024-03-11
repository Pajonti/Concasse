package fr.pajonti.concasse.provider.database.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.provider.database.DatabaseDAO;
import fr.pajonti.concasse.provider.database.dto.PriceDTO;
import fr.pajonti.concasse.provider.database.dto.RecipeDTO;
import fr.pajonti.concasse.provider.database.dto.StatItemDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StatItemDAO extends DatabaseDAO {
    public StatItemDAO(Configuration configuration) throws SQLException {
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

    public void register(StatItemDTO dto) throws SQLException {

        Statement statement = connection.createStatement();
        statement.executeUpdate("MERGE INTO STAT_ITEM " +
                                        "(ITEM_ID_VULBIS, STAT_ID, STAT_LOWER, STAT_UPPER) " +
                                    "VALUES " +
                                        "("
                                            + dto.getItemID() + ", "
                                            + dto.getStatID() + ", "
                                            + dto.getStatLower() + ", "
                                            + dto.getStatUpper()
                                        + ");");
        statement.close();
    }

    public List<StatItemDTO> getStatsFromItemByItemID(Integer itemID) throws SQLException {
        List<StatItemDTO> list = new ArrayList<>();
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM STAT_ITEM WHERE ITEM_ID_VULBIS = " + itemID + ";");

        while(rs.next()){
            list.add(new StatItemDTO(rs.getInt("STAT_LOWER"), rs.getInt("STAT_UPPER"), rs.getInt("ITEM_ID_VULBIS"), rs.getInt("STAT_ID")));
        }

        return list;
    }
}
