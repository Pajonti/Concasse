package fr.pajonti.concasse.provider.database.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.provider.database.DatabaseDAO;
import fr.pajonti.concasse.provider.database.dto.ItemDTO;
import fr.pajonti.concasse.provider.database.dto.PriceDTO;
import fr.pajonti.concasse.provider.database.dto.ServerDTO;
import fr.pajonti.concasse.provider.database.dto.TauxBrisageDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TauxBrisageDAO extends DatabaseDAO {
    public TauxBrisageDAO(Configuration configuration) throws SQLException {
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

    public void register(TauxBrisageDTO dto) throws SQLException {

        DateTimeFormatter dtfInsertion = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestampInsertion = dtfInsertion.format(dto.getRefreshTimestamp());

        DateTimeFormatter dtfTxRefreshDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestampTxRefresh = dtfTxRefreshDate.format(dto.getTxUpdateTimestamp());

        Statement statement = connection.createStatement();
        String query = "MERGE INTO TX_BRISAGE " +
                "(VULBIS_ID, SERVER_ID, TAUX, LAST_REFRESH_DATE, LAST_TX_UPDATE_DATE) " +
                "VALUES " +
                "("
                + dto.getVulbisID() + ", "
                + dto.getServerID() + ", "
                + dto.getTauxConcassage() + ", "
                + "'" + timestampInsertion + "', "
                + "'" + timestampTxRefresh + "'"
                + ");";

        statement.executeUpdate(query);
        statement.close();
    }

    public List<TauxBrisageDTO> getTauxConnus(ServerDTO serverDTO) throws SQLException {
        List<TauxBrisageDTO> liste = new ArrayList<>();
        Statement statement = connection.createStatement();
        DateTimeFormatter dtfInsertion = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ResultSet rs = statement.executeQuery("SELECT VULBIS_ID, SERVER_ID, TAUX, LAST_REFRESH_DATE, LAST_TX_UPDATE_DATE FROM TX_BRISAGE WHERE SERVER_ID = " + serverDTO.getServerID());

        while(rs.next()){
            LocalDateTime lastRefreshDateLDT = LocalDateTime.parse(rs.getString("LAST_REFRESH_DATE"), dtfInsertion);
            LocalDateTime lastTxUpdateDateLDT = LocalDateTime.parse(rs.getString("LAST_TX_UPDATE_DATE"), dtfInsertion);

            TauxBrisageDTO dto = new TauxBrisageDTO(rs.getInt("VULBIS_ID"), rs.getInt("SERVER_ID"), rs.getFloat("TAUX"), lastRefreshDateLDT, lastTxUpdateDateLDT);

            liste.add(dto);
        }

        return liste;
    }
}
