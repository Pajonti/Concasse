package fr.pajonti.concasse.provider.database.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.provider.database.DatabaseDAO;
import fr.pajonti.concasse.provider.database.dto.ItemDTO;
import fr.pajonti.concasse.provider.database.dto.ServerDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

public class GenericDAO extends DatabaseDAO {
    public GenericDAO(Configuration configuration) throws SQLException {
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

    /**
     * Equivait a "Y-a-t-il au moins un prix ou un taux de renseigne en base ?
     * @param serverDTO
     * @return
     * @throws SQLException
     */
    public boolean serverInitialized(ServerDTO serverDTO) throws SQLException {
        Statement statement = connection.createStatement();
        int i = 0;

        ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS CT FROM PRICE WHERE SERVER_ID = " + serverDTO.getServerID() + ";");
        while(rs.next()){
            i += rs.getInt("CT");
        }

        rs = statement.executeQuery("SELECT COUNT(*) AS CT FROM TX_BRISAGE WHERE SERVER_ID = " + serverDTO.getServerID() + ";");
        while(rs.next()){
            i += rs.getInt("CT");
        }

        return i != 0;
    }

    public String getDateDernierRefreshPrix(ServerDTO serverDTO) throws SQLException {
        Statement statement = connection.createStatement();
        String s = "Jamais";

        ResultSet rs = statement.executeQuery("SELECT MAX(LAST_REFRESH_DATE) AS DT FROM PRICE WHERE SERVER_ID = " + serverDTO.getServerID() + ";");
        while(rs.next()){
            if(rs.getString("DT") != null){
                s = rs.getString("DT");

                DateTimeFormatter dfSource = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.LENIENT);
                DateTimeFormatter dfCible = DateTimeFormatter.ofPattern("yyyy-MM-dd à HH:mm");

                LocalDateTime dt = LocalDateTime.parse(s, dfSource);
                s = dfCible.format(dt);
            }
        }

        return s;
    }

    public String getDateDernierRefreshTaux(ServerDTO serverDTO) throws SQLException {
        Statement statement = connection.createStatement();
        String s = "Jamais";

        ResultSet rs = statement.executeQuery("SELECT MAX(LAST_REFRESH_DATE) AS DT FROM TX_BRISAGE WHERE SERVER_ID = " + serverDTO.getServerID() + ";");
        while(rs.next()){
            if(rs.getString("DT") != null){
                s = rs.getString("DT");

                DateTimeFormatter dfSource = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.LENIENT);
                DateTimeFormatter dfCible = DateTimeFormatter.ofPattern("yyyy-MM-dd à HH:mm").withResolverStyle(ResolverStyle.LENIENT);

                LocalDateTime dt = LocalDateTime.parse(s, dfSource);
                s = dfCible.format(dt);
            }
        }

        return s;
    }
}
