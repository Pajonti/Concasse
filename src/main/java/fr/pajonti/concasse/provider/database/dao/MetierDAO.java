package fr.pajonti.concasse.provider.database.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.provider.database.DatabaseDAO;
import fr.pajonti.concasse.provider.database.dto.MetierDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MetierDAO extends DatabaseDAO {
    public MetierDAO(Configuration configuration) {
        super(configuration);
    }

    public List<MetierDTO> getMetiersList() throws SQLException {
        Connection connection = DriverManager.getConnection(this.jdbcURL, this.username, this.password);
        Statement statement = connection.createStatement();

        List<MetierDTO> liste = new ArrayList<>();

        ResultSet rs = statement.executeQuery("SELECT METIER_ID, METIER_LABEL FROM METIER");

        while(rs.next()){
            liste.add(new MetierDTO(rs.getInt("METIER_ID"), rs.getString("METIER_LABEL")));
        }

        connection.close();

        return liste;
    }
}
