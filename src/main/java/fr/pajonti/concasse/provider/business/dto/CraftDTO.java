package fr.pajonti.concasse.provider.business.dto;

import com.google.common.collect.Table;
import fr.pajonti.concasse.helper.technical.StringHelper;
import fr.pajonti.concasse.provider.database.dao.*;
import fr.pajonti.concasse.provider.database.dto.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;


@Getter
@Setter
public class CraftDTO {
    private ItemDTO item;
    private Integer nombreItemsACraft;
    private HashMap<ItemDTO, Map<Integer, Integer>> listeCourses;
    private Integer coutCraft;
    private Integer prixReventeDirect;
    private HashMap<StatEnum, Map<RuneDTO, Integer>> rendementRunesParFocus;
    private HashMap<StatEnum, Integer> rendementKamasParFocus;

    /* DAOs et DTOs */
    private ServerDTO serverDTO;
    private RecipeDAO recipeDAO;
    private PriceDAO priceDAO;
    private StatItemDAO statItemDAO;
    private ItemDAO itemDAO;
    private TauxBrisageDAO tauxBrisageDAO;

    public CraftDTO() {
        listeCourses = new HashMap<>();
        rendementRunesParFocus = new HashMap<>();
        rendementKamasParFocus = new HashMap<>();
    }

    public void calculerNombreItemsACraft(int montantAInvestir) throws SQLException {
        int nombreDeCraft = 0;

        List<RecipeDTO> listeIngredients = recipeDAO.collecterRecette(this.item.getId());

        //Dans le cas ou il n'y a pas d'ingredient, c'est que l'item n'est pas craftable. On arrete les frais ici
        if(listeIngredients.isEmpty()){
            this.nombreItemsACraft = -1;
        }

        else{
            while (true){
                nombreDeCraft++;
                int prixCalcule = 0;

                for(RecipeDTO recipeIngredient : listeIngredients){
                    PriceDTO priceIngredient = priceDAO.getPriceForItem(recipeIngredient.getVulbisIDComposant(), serverDTO);

                    //Si un ingredient n'est pas reference, on rejette l'objet
                    if(priceIngredient == null || (priceIngredient.getPriceOne() == null && priceIngredient.getPriceTen() == null && priceIngredient.getPriceHundred() == null)){
                        this.nombreItemsACraft = -1;
                        return;
                    }

                    Map<Integer, Integer> mapQuantites = new HashMap<>();

                    mapQuantites.put(1, recipeIngredient.getQuantity() * nombreDeCraft);
                    mapQuantites.put(10, 0);
                    mapQuantites.put(100, 0);

                    mapQuantites = optimiserMapQuantitesSelonPrix(mapQuantites, priceIngredient);

                    prixCalcule += mapQuantites.get(1) * priceIngredient.getPriceOne();
                    prixCalcule += mapQuantites.get(10) * priceIngredient.getPriceTen();
                    prixCalcule += mapQuantites.get(100) * priceIngredient.getPriceHundred();
                }

                if(prixCalcule > montantAInvestir){
                    this.nombreItemsACraft = Math.max(0, nombreDeCraft - 1);
                    return;
                }
            }
        }
    }

    public void calculerListeCourses() throws SQLException {
        try{
            List<RecipeDTO> listeIngredients = recipeDAO.collecterRecette(this.item.getId());
            HashMap<ItemDTO, Map<Integer, Integer>> mapCourses = new HashMap<>();

            for(RecipeDTO recipeIngredient : listeIngredients){
                PriceDTO priceIngredient = priceDAO.getPriceForItem(recipeIngredient.getVulbisIDComposant(), serverDTO);

                Map<Integer, Integer> mapQuantites = new HashMap<>();

                mapQuantites.put(1, recipeIngredient.getQuantity() * nombreItemsACraft);
                mapQuantites.put(10, 0);
                mapQuantites.put(100, 0);

                mapCourses.put(itemDAO.loadItemByID(recipeIngredient.getVulbisIDComposant()), optimiserMapQuantitesSelonPrix(mapQuantites, priceIngredient));
            }

            this.listeCourses = mapCourses;
        }
        catch(NullPointerException npe){
            this.listeCourses = null;
        }
    }

    public void calculerCoutCraft() throws SQLException {
        try{
            int price = 0;

            HashMap<ItemDTO, Map<Integer, Integer>> mapIngredients = this.getListeCourses();
            for(Map.Entry<ItemDTO, Map<Integer, Integer>> ingredient : mapIngredients.entrySet()){
                PriceDTO pricingIngredient = priceDAO.getPriceForItem(ingredient.getKey().getId(), serverDTO);
                Map<Integer, Integer> listeAchat = ingredient.getValue();

                if(listeAchat.get(1) > 0){
                    if(pricingIngredient.getPriceOne() != null){
                        price += pricingIngredient.getPriceOne() * listeAchat.get(1);
                    }
                    else{
                        throw new IllegalArgumentException("Tentative de collecte d'un prix inconnu : " + ingredient.getKey().getName() + " - Pack de 1");
                    }
                }

                if(listeAchat.get(10) > 0){
                    if(pricingIngredient.getPriceTen() != null){
                        price += pricingIngredient.getPriceTen() * listeAchat.get(10);
                    }
                    else{
                        throw new IllegalArgumentException("Tentative de collecte d'un prix inconnu : " + ingredient.getKey().getName() + " - Pack de 10");
                    }
                }

                if(listeAchat.get(100) > 0){
                    if(pricingIngredient.getPriceHundred() != null){
                        price += pricingIngredient.getPriceHundred() * listeAchat.get(100);
                    }
                    else{
                        throw new IllegalArgumentException("Tentative de collecte d'un prix inconnu : " + ingredient.getKey().getName() + " - Pack de 100");
                    }
                }
            }

            this.setCoutCraft(price);
        }
        catch (NullPointerException npe){
            //Erreur : un objet de recette est absent du dataset. Positionnement d'un cout factice pour ignorer.
            this.setCoutCraft(999999999);
        }
    }

    public void calculerPrixReventeDirecte() throws SQLException {
        int prixRevente = 0;

        Integer nombreRestantItemsAVendre = this.getNombreItemsACraft();
        PriceDTO priceDTO = priceDAO.getPriceForItem(this.getItem().getId(), serverDTO);

        if(priceDTO.getPriceHundred() != null && priceDTO.getPriceOne() != null && priceDTO.getPriceHundred() > priceDTO.getPriceOne() * 100){
            while(nombreRestantItemsAVendre > 100){
                nombreRestantItemsAVendre = nombreRestantItemsAVendre - 100;
                prixRevente += priceDTO.getPriceHundred();
            }
        }

        if(priceDTO.getPriceTen() != null && priceDTO.getPriceOne() != null && priceDTO.getPriceTen() > priceDTO.getPriceOne() * 10){
            while(nombreRestantItemsAVendre > 10){
                nombreRestantItemsAVendre = nombreRestantItemsAVendre - 10;
                prixRevente += priceDTO.getPriceTen();
            }
        }

        if(priceDTO.getPriceOne() != null){
            prixRevente += priceDTO.getPriceOne() * nombreRestantItemsAVendre;
        }

        this.prixReventeDirect = prixRevente;
    }

    public void calculerProductionRunes(Table<Integer, Integer, RuneDTO> mapReferenceRunes) throws SQLException {
        List<StatItemDTO> listeStatItem = statItemDAO.getStatsFromItemByItemID(this.item.getId());
        List<StatItemDTO> listeStatItemNettoyee = new ArrayList<>();

        for(StatItemDTO ligneStat : listeStatItem){
            if(mapReferenceRunes.containsRow(ligneStat.getStatID())){
                listeStatItemNettoyee.add(ligneStat);
            }
        }

        if(!listeStatItemNettoyee.isEmpty()){
            //On identifie si une stat est negative : On ne sait pas gerer le focus dans ce cas là
            boolean hasNegativeStat = false;
            for(StatItemDTO ligneStat : listeStatItemNettoyee){
                if(ligneStat.getStatLower() < 0){
                    hasNegativeStat = true;
                }
            }

            if(!hasNegativeStat){
                //Pour chaque ligne de stat qui matche une rune, on calcule le rendement en focusant sur cette rune
                for(StatItemDTO ligneStat : listeStatItemNettoyee){
                    rendementRunesParFocus.put(StatEnum.getEnumByValue(ligneStat.getStatID()), calculerProductionRuneSelonFocus(mapReferenceRunes, StatEnum.getEnumByValue(ligneStat.getStatID()), listeStatItemNettoyee));
                }
            }


            //On rajoute ensuite une ligne sans focus (StatEenum.NONE)
            rendementRunesParFocus.put(StatEnum.NONE, calculerProductionRuneSansFocus(mapReferenceRunes, listeStatItemNettoyee));
        }
    }

    private Map<RuneDTO, Integer> calculerProductionRuneSansFocus(Table<Integer, Integer, RuneDTO> mapReferenceRunes, List<StatItemDTO> listeStatItemNettoyee) throws SQLException {
        Map<RuneDTO, Integer> mapProduction = new HashMap<>();

        Float tauxBrisage = 0.40f;

        TauxBrisageDTO txBrisage = tauxBrisageDAO.getTauxItem(this.item.getId(), serverDTO);

        if(txBrisage != null && txBrisage.getTauxConcassage() != null && txBrisage.getTauxConcassage() > 0){
            tauxBrisage = txBrisage.getTauxConcassage() / 100;
        }

        for(StatItemDTO ligneDeStat : listeStatItemNettoyee){
            RuneDTO runeConcernee = mapReferenceRunes.get(ligneDeStat.getStatID(), 1);

            StatEnum stat = StatEnum.getEnumByValue(ligneDeStat.getStatID());
            Integer jet = ligneDeStat.getStatMoyenne();
            Float poidsStat = runeConcernee.getWeight();
            Integer level = item.getLevel();

            float poidsDeBrisage = (3 * jet * poidsStat * level / 200 + 1);
            Float poidsPondere = poidsDeBrisage / Math.max(1, poidsStat) * (StatEnum.PODS.equals(stat) ? 0.4f : 1);
            Float poidsPonderePostMultiplicationTaux = poidsPondere * tauxBrisage;
            float nombreRunesProduitesPourStat = poidsPonderePostMultiplicationTaux * this.nombreItemsACraft;

            mapProduction.put(runeConcernee, (int) Math.max(nombreRunesProduitesPourStat, 0));
        }

        return mapProduction;
    }

    private Map<RuneDTO, Integer> calculerProductionRuneSelonFocus(Table<Integer, Integer, RuneDTO> mapReferenceRunes, StatEnum statFocus, List<StatItemDTO> listeStatItemNettoyee) throws SQLException {
        Map<RuneDTO, Integer> mapProduction = new HashMap<>();

        Float tauxBrisage = 0.40f;

        TauxBrisageDTO txBrisage = tauxBrisageDAO.getTauxItem(this.item.getId(), serverDTO);

        if(txBrisage != null && txBrisage.getTauxConcassage() != null && txBrisage.getTauxConcassage() > 0){
            tauxBrisage = txBrisage.getTauxConcassage() / 100;
        }

        RuneDTO runeConcerneeParFocus = mapReferenceRunes.get(statFocus.getStatCode(), 1);

        Map<StatEnum, Float> mapPoidsBrisage = new HashMap<>();

        for(StatItemDTO ligneDeStat : listeStatItemNettoyee){
            RuneDTO runeConcernee = mapReferenceRunes.get(ligneDeStat.getStatID(), 1);

            StatEnum stat = StatEnum.getEnumByValue(ligneDeStat.getStatID());
            Integer jet = ligneDeStat.getStatMoyenne();
            Float poidsStat = runeConcernee.getWeight();
            Integer level = item.getLevel();

            float poidsDeBrisage = (3 * jet * poidsStat * level / 200 + 1);

            mapPoidsBrisage.put(stat, poidsDeBrisage);
        }

        Float poidsDeBrisagePondereUtiliseFocus = 0f;

        for(Map.Entry<StatEnum, Float> entreePoids : mapPoidsBrisage.entrySet()){
            StatEnum stat = entreePoids.getKey();
            Float poidsDeBrisage = entreePoids.getValue();

            poidsDeBrisagePondereUtiliseFocus += (statFocus.equals(stat) ? poidsDeBrisage : poidsDeBrisage * 0.5f);
        }

        poidsDeBrisagePondereUtiliseFocus = poidsDeBrisagePondereUtiliseFocus / Math.max(1, runeConcerneeParFocus.getWeight());
        Float runesGenerees = poidsDeBrisagePondereUtiliseFocus * tauxBrisage * nombreItemsACraft * (StatEnum.PODS.getStatCode().equals(statFocus.getStatCode()) ? 0.4f : 1);

        mapProduction.put(runeConcerneeParFocus, runesGenerees.intValue());

        return mapProduction;
    }

    public void calculerRendementKamasRunes() throws SQLException {
        for(Map.Entry<StatEnum, Map<RuneDTO, Integer>> mapEntry : rendementRunesParFocus.entrySet()){
            int yield = 0;

            Map<RuneDTO, Integer> mapRunes = mapEntry.getValue();

            for(Map.Entry<RuneDTO, Integer> generation : mapRunes.entrySet()){
                RuneDTO runeGeneree = generation.getKey();
                Integer amount = generation.getValue();

                PriceDTO priceRune = priceDAO.getPriceForItem(runeGeneree.getItemID(), serverDTO);
                yield += generateRuneDispatching(priceRune, amount);
            }

            rendementKamasParFocus.put(mapEntry.getKey(), yield);
        }
    }

    private int generateRuneDispatching(PriceDTO priceRune, Integer amount) {
        int resteADispatch = amount;
        int revenue = 0;

        if(priceRune != null && priceRune.getPriceHundred() != null && priceRune.getPriceHundred() != 0){
            while(resteADispatch > 100){
                revenue += priceRune.getPriceHundred();
                resteADispatch = resteADispatch - 100;
            }
        }

        if(priceRune != null && priceRune.getPriceTen() != null && priceRune.getPriceTen() != 0){
            while(resteADispatch > 10){
                revenue += priceRune.getPriceTen();
                resteADispatch = resteADispatch - 10;
            }
        }

        revenue += resteADispatch * priceRune.getPriceOne();

        return revenue;
    }

    private static Map<Integer, Integer> optimiserMapQuantitesSelonPrix(Map<Integer, Integer> mapQuantites, PriceDTO priceIngredient) {
        int requiredQuantity = mapQuantites.get(1);

        if(priceIngredient.getPriceHundred() != null && priceIngredient.getPriceOne() != null && priceIngredient.getPriceHundred() > 0 && priceIngredient.getPriceOne() > 0){
            while(requiredQuantity >= 100){
                requiredQuantity = requiredQuantity - 100;
                mapQuantites.put(100, mapQuantites.get(100) + 1);
                mapQuantites.put(1, mapQuantites.get(1) - 100);
            }
        }

        if(priceIngredient.getPriceTen() != null && priceIngredient.getPriceOne() != null && priceIngredient.getPriceTen() > 0 && priceIngredient.getPriceOne() > 0){
            while(requiredQuantity >= 10){
                requiredQuantity = requiredQuantity - 10;
                mapQuantites.put(10, mapQuantites.get(10) + 1);
                mapQuantites.put(1, mapQuantites.get(1) - 10);
            }
        }

        return mapQuantites;
    }

    public void displayBISConcasse(StatEnum statFocus) throws SQLException {
        LocalDateTime dateDernierRefresh = priceDAO.getDateTimeRefreshVulbis(serverDTO);
        Integer coutTotalCraft = 0;
        Integer revenuTotalCraft = 0;

        Map<RuneDTO, Integer> mapRenduRunes = this.rendementRunesParFocus.get(statFocus);


        System.out.println("| --------------------------------------------------------------------------- |");
        System.out.println("| " + StringHelper.padWithCharacter(this.item.getName(), 65, " ", 2) + "|" + StringHelper.padWithCharacter("x" + this.getNombreItemsACraft(), 10, " ", 3) + "|");
        System.out.println("| --------------------------------------------------------------------------- |");
        System.out.println("| Ingrédients (Dernier refresh des prix au " + dateDernierRefresh + ") :             |");

        TauxBrisageDTO tauxBrisageDTO = tauxBrisageDAO.getTauxItem(this.getItem().getId(), serverDTO);
        for (Map.Entry<ItemDTO, Map<Integer, Integer>> ingredient : listeCourses.entrySet()) {
            ItemDTO composant = ingredient.getKey();
            Map<Integer, Integer> nombreAchats = ingredient.getValue();
            PriceDTO pricing = priceDAO.getPriceForItem(composant.getId(), serverDTO);

            Integer quantiteTotale = nombreAchats.get(1) + nombreAchats.get(10) * 10 + nombreAchats.get(100) * 100;

            Integer pricingTotal = nombreAchats.get(1) * pricing.getPriceOne() + nombreAchats.get(10) * pricing.getPriceTen() + nombreAchats.get(100) * pricing.getPriceHundred();
            coutTotalCraft += pricingTotal;

            System.out.println("|   - "
                    + StringHelper.padWithCharacter("x" + quantiteTotale + " ", 8, " ", 2)
                    + StringHelper.padWithCharacter(composant.getName(), 35, " ", 2)
                    + StringHelper.padWithCharacter("pour " + pricingTotal + " K", 18, " ", 2)
                    + "           |");

            if (nombreAchats.get(1) != null && nombreAchats.get(1) > 0) {
                System.out.println("|     > "
                        + StringHelper.padWithCharacter("x" + nombreAchats.get(1) + " stacks de 1 à " + pricing.getPriceOne() + " K / Stack", 70, " ", 2)
                        + "|"
                );
            }

            if (nombreAchats.get(10) != null && nombreAchats.get(10) > 0) {
                System.out.println("|     > "
                        + StringHelper.padWithCharacter("x" + nombreAchats.get(10) + " stacks de 10 à " + pricing.getPriceTen() + " K / Stack", 70, " ", 2)
                        + "|"
                );
            }

            if (nombreAchats.get(100) != null && nombreAchats.get(100) > 0) {
                System.out.println("|     > "
                        + StringHelper.padWithCharacter("x" + nombreAchats.get(100) + " stacks de 100 à " + pricing.getPriceHundred() + " K / Stack", 70, " ", 2)
                        + "|"
                );
            }
        }

        System.out.println("|" + StringHelper.padWithCharacter(" ", 77, " ", 2) + "|");
        System.out.println("| Action : Craft et concassage - Focus sur : " + StringHelper.padWithCharacter(statFocus.toString(), 33, " ", 2) + "|");
        System.out.println(StringHelper.padWithCharacter("| Taux de brisage retenu : " + (tauxBrisageDTO == null ? 40.0 : tauxBrisageDTO.getTauxConcassage()) +"% " + (tauxBrisageDTO == null ? "(Valeur retenue car taux inconnu)" : "(Valeur au " + tauxBrisageDTO.getTxUpdateTimestamp() + ")") , 78, " ", 2) + "|");
        System.out.println("|" + StringHelper.padWithCharacter(" ", 77, " ", 2) + "|");

        System.out.println("| Rendement estimé : " + StringHelper.padWithCharacter("|", 58, " ", 1));

        for(Map.Entry<RuneDTO, Integer> mapRunes : mapRenduRunes.entrySet()){
            RuneDTO runeProduite = mapRunes.getKey();
            Integer quantite = mapRunes.getValue();

            ItemDTO runeAffiliee = itemDAO.loadItemByID(runeProduite.getItemID());
            PriceDTO prixRune = priceDAO.getPriceForItem(runeAffiliee.getId(), serverDTO);

            Map<Integer, Integer> mapPrixVente = new HashMap<>();
            mapPrixVente.put(1, quantite);
            mapPrixVente.put(10, 0);
            mapPrixVente.put(100, 0);

            mapPrixVente = optimiserMapQuantitesSelonPrix(mapPrixVente, prixRune);
            Integer pricingTotal = mapPrixVente.get(1) * prixRune.getPriceOne() + mapPrixVente.get(10) * prixRune.getPriceTen() + mapPrixVente.get(100) * prixRune.getPriceHundred();
            revenuTotalCraft += pricingTotal;

            System.out.println(   StringHelper.padWithCharacter("|   - x" + quantite, 12, " ", 2)
                                + StringHelper.padWithCharacter(runeAffiliee.getName(), 33, " ", 2)
                                + StringHelper.padWithCharacter("pour " + pricingTotal + " K", 33, " ", 2)
                                + "|");

            if (mapPrixVente.get(1) != null && mapPrixVente.get(1) > 0) {
                System.out.println("|     > "
                        + StringHelper.padWithCharacter("x" + mapPrixVente.get(1) + " ", 5, " ", 2)
                        + StringHelper.padWithCharacter("   stacks de 1 à " + prixRune.getPriceOne() + " K / Stack", 65, " ", 2)
                        + "|"
                );
            }

            if (mapPrixVente.get(10) != null && mapPrixVente.get(10) > 0) {
                System.out.println("|     > "
                        + StringHelper.padWithCharacter("x" + mapPrixVente.get(10) + " ", 5, " ", 2)
                        + StringHelper.padWithCharacter("   stacks de 10 à " + prixRune.getPriceTen() + " K / Stack", 65, " ", 2)
                        + "|"
                );
            }

            if (mapPrixVente.get(100) != null && mapPrixVente.get(100) > 0) {
                System.out.println("|     > "
                        + StringHelper.padWithCharacter("x" + mapPrixVente.get(100) + " ", 5, " ", 2)
                        + StringHelper.padWithCharacter("   stacks de 100 à " + prixRune.getPriceHundred() + " K / Stack", 65, " ", 2)
                        + "|"
                );
            }
        }

        System.out.println("| --------------------------------------------------------------------------- |");
        System.out.println("| Coût : " + StringHelper.padWithCharacter(String.valueOf(coutTotalCraft), 14, " ", 1) + " K " +
                           "| Revenu : " + StringHelper.padWithCharacter(String.valueOf(revenuTotalCraft), 12, " ", 1) + " K " +
                           "| Rendement : " + StringHelper.padWithCharacter(new DecimalFormat("####0.000").format((float) revenuTotalCraft / coutTotalCraft), 11, " ", 1)  + " |");
        System.out.println("| --------------------------------------------------------------------------- |");
        System.out.println(" ");
    }

    public void displayBISResell() throws SQLException {
        LocalDateTime dateDernierRefresh = priceDAO.getDateTimeRefreshVulbis(serverDTO);
        Integer coutTotalCraft = 0;
        Integer revenuTotalCraft = 0;

        System.out.println("| --------------------------------------------------------------------------- |");
        System.out.println("| " + StringHelper.padWithCharacter(this.item.getName(), 65, " ", 2) + "|" + StringHelper.padWithCharacter("x" + this.getNombreItemsACraft(), 10, " ", 3) + "|");
        System.out.println("| --------------------------------------------------------------------------- |");
        System.out.println("| Ingrédients (Dernier refresh des prix au " + dateDernierRefresh + ") :             |");

        for (Map.Entry<ItemDTO, Map<Integer, Integer>> ingredient : listeCourses.entrySet()) {
            ItemDTO composant = ingredient.getKey();
            Map<Integer, Integer> nombreAchats = ingredient.getValue();
            PriceDTO pricing = priceDAO.getPriceForItem(composant.getId(), serverDTO);

            Integer quantiteTotale = nombreAchats.get(1) + nombreAchats.get(10) * 10 + nombreAchats.get(100) * 100;

            Integer pricingTotal = nombreAchats.get(1) * pricing.getPriceOne() + nombreAchats.get(10) * pricing.getPriceTen() + nombreAchats.get(100) * pricing.getPriceHundred();
            coutTotalCraft += pricingTotal;

            System.out.println("|   - "
                    + StringHelper.padWithCharacter("x" + quantiteTotale + " ", 8, " ", 2)
                    + StringHelper.padWithCharacter(composant.getName(), 35, " ", 2)
                    + StringHelper.padWithCharacter("pour " + pricingTotal + " K", 18, " ", 2)
                    + "           |");

            if (nombreAchats.get(1) != null && nombreAchats.get(1) > 0) {
                System.out.println("|     > "
                        + StringHelper.padWithCharacter("x" + nombreAchats.get(1) + " stacks de 1 à " + pricing.getPriceOne() + " K / Stack", 70, " ", 2)
                        + "|"
                );
            }

            if (nombreAchats.get(10) != null && nombreAchats.get(10) > 0) {
                System.out.println("|     > "
                        + StringHelper.padWithCharacter("x" + nombreAchats.get(10) + " stacks de 10 à " + pricing.getPriceTen() + " K / Stack", 70, " ", 2)
                        + "|"
                );
            }

            if (nombreAchats.get(100) != null && nombreAchats.get(100) > 0) {
                System.out.println("|     > "
                        + StringHelper.padWithCharacter("x" + nombreAchats.get(100) + " stacks de 100 à " + pricing.getPriceHundred() + " K / Stack", 70, " ", 2)
                        + "|"
                );
            }
        }

        System.out.println("|" + StringHelper.padWithCharacter(" ", 77, " ", 2) + "|");
        System.out.println("|" + StringHelper.padWithCharacter(" Action : Craft et revente", 77, " ", 2) + "|");
        System.out.println("|" + StringHelper.padWithCharacter(" ", 77, " ", 2) + "|");

        Map<Integer, Integer> mapPrixVente = new HashMap<>();
        mapPrixVente.put(1, this.nombreItemsACraft);
        mapPrixVente.put(10, 0);
        mapPrixVente.put(100, 0);

        PriceDTO prixItem = priceDAO.getPriceForItem(this.item.getId(), serverDTO);

        mapPrixVente = optimiserMapQuantitesSelonPrix(mapPrixVente, prixItem);
        Integer pricingTotal = mapPrixVente.get(1) * prixItem.getPriceOne() + mapPrixVente.get(10) * prixItem.getPriceTen() + mapPrixVente.get(100) * prixItem.getPriceHundred();

        System.out.println("|" + StringHelper.padWithCharacter(" Prix de vente estimé : " + pricingTotal + " K", 77, " ", 2) + "|");

        if (mapPrixVente.get(1) != null && mapPrixVente.get(1) > 0) {
            System.out.println("|     > "
                    + StringHelper.padWithCharacter("x" + mapPrixVente.get(1) + " ", 5, " ", 2)
                    + StringHelper.padWithCharacter("   stacks de 1 à " + prixItem.getPriceOne() + " K / Stack", 65, " ", 2)
                    + "|"
            );
        }

        if (mapPrixVente.get(10) != null && mapPrixVente.get(10) > 0) {
            System.out.println("|     > "
                    + StringHelper.padWithCharacter("x" + mapPrixVente.get(10) + " ", 5, " ", 2)
                    + StringHelper.padWithCharacter("   stacks de 10 à " + prixItem.getPriceTen() + " K / Stack", 65, " ", 2)
                    + "|"
            );
        }

        if (mapPrixVente.get(100) != null && mapPrixVente.get(100) > 0) {
            System.out.println("|     > "
                    + StringHelper.padWithCharacter("x" + mapPrixVente.get(100) + " ", 5, " ", 2)
                    + StringHelper.padWithCharacter("   stacks de 100 à " + prixItem.getPriceHundred() + " K / Stack", 65, " ", 2)
                    + "|"
            );
        }

        System.out.println("| --------------------------------------------------------------------------- |");
        System.out.println("| Coût : " + StringHelper.padWithCharacter(String.valueOf(coutTotalCraft), 14, " ", 1) + " K " +
                "| Revenu : " + StringHelper.padWithCharacter(String.valueOf(pricingTotal), 12, " ", 1) + " K " +
                "| Rendement : " + StringHelper.padWithCharacter(new DecimalFormat("####0.000").format((float) pricingTotal / coutTotalCraft), 11, " ", 1)  + " |");
        System.out.println("| --------------------------------------------------------------------------- |");
        System.out.println(" ");
    }
}
