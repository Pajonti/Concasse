package fr.pajonti.concasse.provider.database.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.provider.database.DatabaseDAO;
import fr.pajonti.concasse.provider.database.dto.PriceDTO;
import fr.pajonti.concasse.provider.database.dto.TauxBrisageDTO;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;

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

        DateTimeFormatter dtfInsertion = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");
        String timestampInsertion = "{ts '" + dtfInsertion.format(dto.getRefreshTimestamp()) + "'}";

        DateTimeFormatter dtfTxRefreshDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");
        String timestampTxRefresh = "{ts '" + dtfTxRefreshDate.format(dto.getTxUpdateTimestamp()) + "'}";

        Statement statement = connection.createStatement();
        statement.executeUpdate("MERGE INTO TX_BRISAGE " +
                                        "(VULBIS_ID, SERVER_ID, TAUX, LAST_REFRESH_DATE, LAST_TX_UPDATE_DATE) " +
                                    "VALUES " +
                                        "("
                                            + dto.getVulbisID() + ", "
                                            + dto.getServerID() + ", "
                                            + dto.getTauxConcassage() + ", "
                                            + timestampInsertion + ", "
                                            + timestampTxRefresh
                                        + ");");
        statement.close();
    }
}
