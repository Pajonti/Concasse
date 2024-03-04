package fr.pajonti.concasse.provider.database.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.provider.database.DatabaseDAO;
import fr.pajonti.concasse.provider.database.dto.PriceDTO;
import fr.pajonti.concasse.provider.database.dto.StatItemDTO;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;

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
                                            + dto.getStatLower()
                                        + ");");
        statement.close();
    }
}
