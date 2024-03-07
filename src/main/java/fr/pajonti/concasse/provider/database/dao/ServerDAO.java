package fr.pajonti.concasse.provider.database.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.provider.database.DatabaseDAO;
import fr.pajonti.concasse.provider.database.dto.ServerDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServerDAO extends DatabaseDAO {

    public ServerDAO(Configuration configuration) throws SQLException {
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

    public List<ServerDTO> getServerList() throws SQLException {
        Statement statement = connection.createStatement();

        List<ServerDTO> liste = new ArrayList<>();

        ResultSet rs = statement.executeQuery("SELECT SERVER_ID, NOM, BRIFUS_ID FROM SERVER");

        while(rs.next()){
            liste.add(new ServerDTO(rs.getInt("SERVER_ID"), rs.getString("NOM"), rs.getInt("BRIFUS_ID")));
        }

        return liste;
    }
}
