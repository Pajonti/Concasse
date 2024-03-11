package fr.pajonti.concasse.provider.database.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.provider.database.DatabaseDAO;
import fr.pajonti.concasse.provider.database.dto.ItemDTO;
import fr.pajonti.concasse.provider.database.dto.PriceDTO;
import fr.pajonti.concasse.provider.database.dto.RecipeDTO;
import fr.pajonti.concasse.provider.database.dto.ServerDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    public PriceDTO getPriceForItem(Integer itemID, ServerDTO server) throws SQLException {
        DateTimeFormatter dtfInsertion = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        PriceDTO price = null;
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM PRICE WHERE VULBIS_ID = " + itemID + " AND SERVER_ID = " + server.getServerID() + ";");

        while(rs.next()){
            LocalDateTime lastRefreshDate = LocalDateTime.parse(rs.getString("LAST_REFRESH_DATE"), dtfInsertion);
            LocalDateTime lastPriceUpdateDate = LocalDateTime.parse(rs.getString("LAST_PRICE_UPDATE_DATE"), dtfInsertion);

            price = new PriceDTO(rs.getInt("VULBIS_ID"), rs.getInt("SERVER_ID"), rs.getInt("PRICE_ONE"), rs.getInt("PRICE_TEN"), rs.getInt("PRICE_HUNDRED"), lastRefreshDate, lastPriceUpdateDate);
        }

        return price;
    }

    public LocalDateTime getDateTimeRefreshVulbis(ServerDTO server) throws SQLException {
        DateTimeFormatter dtfInsertion = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime date = null;
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT MAX(LAST_PRICE_UPDATE_DATE) AS DT FROM PRICE WHERE SERVER_ID = " + server.getServerID() + ";");

        while(rs.next()){
            date = LocalDateTime.parse(rs.getString("DT"), dtfInsertion);
        }

        return date;
    }
}
