package fr.pajonti.concasse.provider.database.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.provider.database.DatabaseDAO;
import fr.pajonti.concasse.provider.database.dto.ServerDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServerDAO extends DatabaseDAO {

    public ServerDAO(Configuration configuration) {
        super(configuration);
    }

    public List<ServerDTO> getServerList() throws SQLException {
        Connection connection = DriverManager.getConnection(this.jdbcURL, this.username, this.password);
        Statement statement = connection.createStatement();

        List<ServerDTO> liste = new ArrayList<>();

        ResultSet rs = statement.executeQuery("SELECT SERVER_ID, NOM, BRIFUS_ID FROM SERVER");

        while(rs.next()){
            liste.add(new ServerDTO(rs.getInt("SERVER_ID"), rs.getString("NOM"), rs.getInt("BRIFUS_ID")));
        }

        connection.close();

        return liste;
    }
}
