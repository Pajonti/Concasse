package fr.pajonti.concasse.provider.database.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.provider.database.DatabaseDAO;
import fr.pajonti.concasse.provider.database.dto.PriceDTO;

import java.sql.*;
import java.time.format.DateTimeFormatter;

public class PriceDAO extends DatabaseDAO {
    public PriceDAO(Configuration configuration) throws SQLException {
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

    public void register(PriceDTO dto) throws SQLException {

        DateTimeFormatter dtfInsertion = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestampInsertion = dtfInsertion.format(dto.getRefreshTimestamp());

        DateTimeFormatter dtfRefreshPrice = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestampRefresh = dtfRefreshPrice.format(dto.getPriceUpdateTimestamp());

        Statement statement = connection.createStatement();
        statement.executeUpdate("MERGE INTO PRICE " +
                                        "(VULBIS_ID, SERVER_ID, PRICE_ONE, PRICE_TEN, PRICE_HUNDRED, LAST_REFRESH_DATE, LAST_PRICE_UPDATE_DATE) " +
                                    "VALUES " +
                                        "("
                                            + dto.getVulbisID() + ", "
                                            + dto.getServerID() + ", "
                                            + (dto.getPriceOne() == null ? "null" : dto.getPriceOne()) + ", "
                                            + (dto.getPriceTen() == null ? "null" : dto.getPriceTen()) + ", "
                                            + (dto.getPriceHundred() == null ? "null" : dto.getPriceHundred()) + ", "
                                            + "'" + timestampInsertion + "', "
                                            + "'" + timestampRefresh + "'"
                                        + ");");
        statement.close();
    }
}
