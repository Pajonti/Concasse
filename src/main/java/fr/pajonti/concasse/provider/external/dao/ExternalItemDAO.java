package fr.pajonti.concasse.provider.external.dao;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.helper.technical.ExitHandlerHelper;
import fr.pajonti.concasse.provider.database.dao.*;
import fr.pajonti.concasse.provider.database.dto.*;
import fr.pajonti.concasse.provider.external.dto.ExternalItemDTO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ExternalItemDAO {
    private final Configuration configuration;

    private final ServerDTO server;

    public ExternalItemDAO(Configuration configuration, ServerDTO server){
        this.configuration =  configuration;
        this.server = server;
    }

    public void saveDataRefreshList(List<ExternalItemDTO> externalItemList) {
        //Dans un premier temps, on va sauvegarder les items en base
        this.saveItemsIntoDatabase(externalItemList);

        //Ensuite, on sauvegarde les tables qui sont liées
        this.saveRunesIntoDatabase(externalItemList);
        this.savePricesIntoDatabase(externalItemList);
        this.saveRecipesIntoDatabase(externalItemList);
        this.saveStatsItemsIntoDatabase(externalItemList);
        this.saveTauxBrisageIntoDatabase(externalItemList);
    }

    private void saveRunesIntoDatabase(List<ExternalItemDTO> externalItemList) {
        System.out.println("Sauvegarde des runes en base");
        try{
            RuneDAO dao = new RuneDAO(this.configuration);
            for(RuneDTO dto : dao.getRunesStatic()){
                dao.register(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ExitHandlerHelper.exit("Erreur lors de l'insertion en base d'un item : " + e.getMessage());
        }
    }

    private void saveItemsIntoDatabase(List<ExternalItemDTO> externalItemList) {
        System.out.println("Sauvegarde des items en base");
        try{
            ItemDAO dao = new ItemDAO(this.configuration);
            for(ExternalItemDTO dto : externalItemList){
                System.out.println("Sauvegarde de l'item " + dto.getItemName());
                ItemDTO itemDTO = new ItemDTO(dto.getItemID(), dto.getItemName(), dto.getTypeId());
                dao.register(itemDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ExitHandlerHelper.exit("Erreur lors de l'insertion en base d'un item : " + e.getMessage());
        }
    }

    private void savePricesIntoDatabase(List<ExternalItemDTO> externalItemList) {
        System.out.println("Sauvegarde des prix en base");
        try{
            PriceDAO dao = new PriceDAO(this.configuration);
            for(ExternalItemDTO dto : externalItemList){
                System.out.println("Sauvegarde des prix de l'item " + dto.getItemName());
                PriceDTO itemDTO = new PriceDTO(dto.getItemID(), this.server.getServerID(), dto.getPriceOne(), dto.getPriceTen(), dto.getPriceHundred(), LocalDateTime.now(), dto.getPriceUpdateTimestamp());
                dao.register(itemDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ExitHandlerHelper.exit("Erreur lors de l'insertion en base d'un prix : " + e.getMessage());
        }
    }

    private void saveRecipesIntoDatabase(List<ExternalItemDTO> externalItemList) {
        System.out.println("Sauvegarde des recettes en base");
        try{
            RecipeDAO dao = new RecipeDAO(this.configuration);
            for(ExternalItemDTO dto : externalItemList){
                if(dto.estCraftable()){
                    System.out.println("Sauvegarde de la recette de l'item " + dto.getItemName());

                    for(String compo : dto.getRecipeString().split(",")){
                        String[] compQty = compo.split("\\|");

                        RecipeDTO itemDTO = new RecipeDTO(dto.getItemID(), Integer.parseInt(compQty[0]), Integer.parseInt(compQty[1]));
                        dao.register(itemDTO);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ExitHandlerHelper.exit("Erreur lors de l'insertion en base d'une recette : " + e.getMessage());
        }
    }

    private void saveStatsItemsIntoDatabase(List<ExternalItemDTO> externalItemList) {
        System.out.println("Sauvegarde des stats d'item en base");
        try{
            StatItemDAO dao = new StatItemDAO(this.configuration);
            for(ExternalItemDTO dto : externalItemList){
                System.out.println("Sauvegarde de la recette de l'item " + dto.getItemName());

                for(StatItemDTO statDTO : dto.getStatItemList()){
                    dao.register(statDTO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ExitHandlerHelper.exit("Erreur lors de l'insertion en base d'une stat d'item : " + e.getMessage());
        }
    }

    private void saveTauxBrisageIntoDatabase(List<ExternalItemDTO> externalItemList) {
        System.out.println("Sauvegarde des taux de brisage en base");
        try{
            TauxBrisageDAO dao = new TauxBrisageDAO(this.configuration);
            for(ExternalItemDTO dto : externalItemList){
                if(dto.estCraftable() && dto.getTauxBrisage() != null && dto.getTauxBrisage() > 0f){
                    System.out.println("Sauvegarde du taux de brisage de " + dto.getItemName());

                    TauxBrisageDTO itemDTO = new TauxBrisageDTO(dto.getItemID(), server.getServerID(), dto.getTauxBrisage(), LocalDateTime.now(), dto.getPriceUpdateTimestamp());
                    dao.register(itemDTO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ExitHandlerHelper.exit("Erreur lors de l'insertion en base d'un taux de brisage : " + e.getMessage());
        }
    }
}
