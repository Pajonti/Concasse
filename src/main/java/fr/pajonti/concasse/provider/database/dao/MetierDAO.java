package fr.pajonti.concasse.provider.database.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.provider.database.DatabaseDAO;
import fr.pajonti.concasse.provider.database.dto.MetierDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MetierDAO extends DatabaseDAO {
    public MetierDAO(Configuration configuration) throws SQLException {
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

    public List<MetierDTO> getMetiersList() throws SQLException {
        Statement statement = connection.createStatement();

        List<MetierDTO> liste = new ArrayList<>();

        ResultSet rs = statement.executeQuery("SELECT METIER_ID, METIER_LABEL FROM METIER");

        while(rs.next()){
            liste.add(new MetierDTO(rs.getInt("METIER_ID"), rs.getString("METIER_LABEL")));
        }

        return liste;
    }
}
