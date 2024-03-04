package fr.pajonti.concasse.provider.database.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.provider.database.DatabaseDAO;
import fr.pajonti.concasse.provider.database.dto.NiveauMetierDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NiveauMetierDAO extends DatabaseDAO {
    public NiveauMetierDAO(Configuration configuration) throws SQLException {
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

    public NiveauMetierDTO getNiveauMetierByMetierIDAndServerId(int metierID, int serverID) throws SQLException {
        Connection connection = DriverManager.getConnection(this.jdbcURL, this.username, this.password);
        Statement statement = connection.createStatement();

        NiveauMetierDTO dto = null;

        ResultSet rs = statement.executeQuery("SELECT METIER_ID, SERVER_ID, NIVEAU FROM NIVEAU_METIER WHERE METIER_ID = " + metierID + " AND SERVER_ID = " + serverID + ";");

        while(rs.next()){
            dto = new NiveauMetierDTO(rs.getInt("METIER_ID"), rs.getInt("SERVER_ID"), rs.getInt("NIVEAU"));
        }

        connection.close();

        return dto;
    }

    public List<NiveauMetierDTO> getNiveauMetierListByServerId(Integer serverID) throws SQLException {
        Connection connection = DriverManager.getConnection(this.jdbcURL, this.username, this.password);
        Statement statement = connection.createStatement();

        List<NiveauMetierDTO> list = new ArrayList<>();

        ResultSet rs = statement.executeQuery("SELECT METIER_ID, SERVER_ID, NIVEAU FROM NIVEAU_METIER WHERE SERVER_ID = " + serverID + ";");

        while(rs.next()){
            list.add(new NiveauMetierDTO(rs.getInt("METIER_ID"), rs.getInt("SERVER_ID"), rs.getInt("NIVEAU")));
        }

        connection.close();

        return list;
    }

    public void updateNiveauMetier(NiveauMetierDTO niveauMetierDTO) throws SQLException{
        Connection connection = DriverManager.getConnection(this.jdbcURL, this.username, this.password);
        Statement statement = connection.createStatement();

        statement.executeUpdate("UPDATE NIVEAU_METIER SET NIVEAU = " + niveauMetierDTO.getNiveauMetier() + " WHERE METIER_ID = " + niveauMetierDTO.getMetierID() + " AND SERVER_ID = " + niveauMetierDTO.getServerID() + ";");

        connection.close();
    }
}
