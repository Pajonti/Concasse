package fr.pajonti.concasse.activities.moneymaking;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.helper.technical.ExitHandlerHelper;
import fr.pajonti.concasse.helper.technical.StringHelper;
import fr.pajonti.concasse.helper.technical.UserInputHelper;
import fr.pajonti.concasse.provider.business.dto.CraftDTO;
import fr.pajonti.concasse.provider.database.dao.*;
import fr.pajonti.concasse.provider.database.dto.*;

import java.sql.SQLException;
import java.util.*;

public class ConcasseCraftActivity {
    static Configuration activityConfiguration = null;
    static List<MetierDTO> listeMetiers = null;
    static ServerDTO server = null;
    static ItemDAO itemDAO = null;
    static RuneDAO runeDAO = null;
    static RecipeDAO recipeDAO = null;
    static PriceDAO priceDAO = null;
    static StatItemDAO statItemDAO = null;
    static TauxBrisageDAO tauxBrisageDAO = null;


    public static void run(ServerDTO serverDTO, Configuration configuration) {
        try {
            server = serverDTO;
            activityConfiguration = configuration;
            itemDAO = new ItemDAO(activityConfiguration);
            runeDAO = new RuneDAO(activityConfiguration);
            recipeDAO = new RecipeDAO(activityConfiguration);
            priceDAO = new PriceDAO(activityConfiguration);
            statItemDAO = new StatItemDAO(activityConfiguration);
            tauxBrisageDAO = new TauxBrisageDAO(activityConfiguration);

            /* Definition des donnees communes */
            int montantAInvestir = definirMontantInvesti();
            int niveauMinimumObjet = definirNiveauMinimumObjet();
            boolean inclureCraftEtRevente = definirCraftEtRevente();

            List<ItemDTO> itemsCraftables = itemDAO.getCraftableItemsAllowedByMetier(server, niveauMinimumObjet);

            Table<Integer, Integer, RuneDTO> tableStatRangRune = HashBasedTable.create();

            for (RuneDTO runeDTO : runeDAO.getRunes()){
                tableStatRangRune.put(runeDTO.getStatID(), runeDTO.getTier(), runeDTO);
            }

            System.out.println("Items identifiés : " + itemsCraftables.size());

            List<CraftDTO> listeCrafts = new ArrayList<>();

            for(ItemDTO itemCraftable : itemsCraftables){

                CraftDTO craft = new CraftDTO();

                craft.setServerDTO(server);
                craft.setRecipeDAO(recipeDAO);
                craft.setPriceDAO(priceDAO);
                craft.setStatItemDAO(statItemDAO);
                craft.setItemDAO(itemDAO);
                craft.setTauxBrisageDAO(tauxBrisageDAO);

                craft.setItem(itemCraftable);
                craft.calculerNombreItemsACraft(montantAInvestir);
                craft.calculerListeCourses();
                craft.calculerCoutCraft();
                craft.calculerPrixReventeDirecte();
                craft.calculerProductionRunes(tableStatRangRune);
                craft.calculerRendementKamasRunes();

                if(craft.getListeCourses() != null){
                    listeCrafts.add(craft);
                }
            }

            float bestYield = 0;

            for(CraftDTO craft : listeCrafts){
                for(Map.Entry<StatEnum, Integer> rendementRunes : craft.getRendementKamasParFocus().entrySet()){
                    int cashGenereSurConcasse = rendementRunes.getValue();
                    int cashEnRevente = craft.getPrixReventeDirect();
                    int coutCraft = craft.getCoutCraft();

                    float yieldConcasse = (float) cashGenereSurConcasse / coutCraft;
                    float yieldRevente = (float) cashEnRevente / coutCraft;

                    if(yieldConcasse > bestYield && craft.getItem().isEstConcassable()){
                        craft.displayBISConcasse(rendementRunes.getKey());
                        bestYield = yieldConcasse;
                    }

                    if(inclureCraftEtRevente && yieldRevente > bestYield){
                        System.out.println("Meilleur yield revente : " + craft.getItem().getName() + " : Y=" + yieldRevente);
                        bestYield = yieldRevente;
                    }
                }

            }
        }
        catch (SQLException se){
            se.printStackTrace();
            ExitHandlerHelper.exit("Erreur lors de l'accès a la base : " + se.getMessage());
        }
        catch (IllegalArgumentException iae){
            iae.printStackTrace();
            ExitHandlerHelper.exit("Erreur lors du traitement d'un item : " + iae.getMessage());
        }
    }

    private static boolean definirCraftEtRevente() {
        System.out.println(" ");

        String message = "Si vous souhaitez voir apparaître des opérations de craft sans concassage dans le calcul de" +
                " rentabilité (i.e. des opérations de craft et revente), entrez la valeur 1 ci-dessous. Sinon, entrez" +
                " n'importe quel chiffre";

        StringHelper.printStringWithFixedWidth(message, 80, false);

        int lowerLevel = UserInputHelper.readUserInputAsInteger();
        return lowerLevel == 1;
    }

    private static int definirNiveauMinimumObjet() {
        System.out.println(" ");

        String message = "Entrez le niveau minimum d'objet que vous souhaitez voir apparaître dans la recherche. " +
                "Si vous souhaitez ne pas avoir de restriction, entrez une valeur de zéro.";

        StringHelper.printStringWithFixedWidth(message, 80, false);

        int lowerLevel = UserInputHelper.readUserInputAsInteger();
        if(lowerLevel > 200 || lowerLevel < 0){
            lowerLevel = 0;
        }

        return lowerLevel;
    }

    private static int definirMontantInvesti() {
        System.out.println(" ");

        String message = "Entrez le montant de Kamas que vous souhaitez investir dans l'opération. Un montant trop faible pourrait vous fermer les portes de" +
                "certains crafts.";

        StringHelper.printStringWithFixedWidth(message, 80, false);

        int cash = UserInputHelper.readUserInputAsInteger();
        if(cash < 0){
            cash = 0;
        }

        return cash;
    }
}
