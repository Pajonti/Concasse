package fr.pajonti.concasse.initializer.subinitializers;

import fr.pajonti.concasse.configuration.Configuration;
import fr.pajonti.concasse.helper.technical.JFileChooserHelper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Classe d'initialisation de la configuration necessaire au fonctionnement de l'appli
 */
public class ConfigurationInitializer {

    private Configuration configuration;

    public ConfigurationInitializer(){
        this.configuration = new Configuration();
    }

    public Configuration generateConfig() {
        try{
            positionnerConfiguration();
            genererStructureDossierConfiguration();

            return this.configuration;

        } catch (IOException e) {
            System.out.println("Erreur " + e.getClass().getName() + " lors de la génération de la configuration : " + e.getMessage() + ". Le programme va maintenant se fermer");
            e.printStackTrace();
            System.exit(-55);
        }

        return null;
    }

    /**
     * Configure l'emplacement ou seront sauvegardees les donnees du produit
     * @return String representant le chemin d'acces au dossier de config
     * @throws IOException si le dossier de positionnement de la configuration n'est pas accessible
     */
    private void positionnerConfiguration() throws IOException {
        System.out.println("Vous allez choisir le dossier ou seront sauvegardees les donnees de configuration.");
        System.out.println("Appuyez sur une touche pour continuer...");

        new Scanner(System.in).nextLine();

        File directorySelection = JFileChooserHelper.getChooseDirectoryDialog();

        //Si la sélection du path s'est bien passée, on sauvegarde ce path.
        if(directorySelection != null){
            this.configuration.setBasePath(directorySelection.getAbsolutePath());
        }
        else{
            throw new IOException("Impossible de recuperer le dossier de sauvegarde de la configuration");
        }
    }

    /**
     * Génère la structure de dossiers dans le dossier de config renseigné
     */
    private void genererStructureDossierConfiguration() throws IOException {
        System.out.println("Génération de la structure de dossier...");

        //Chemin de la BDD H2
        String databasePath = this.configuration.getBasePath() + System.getProperty("file.separator") + "sql" + System.getProperty("file.separator") + "database";
        this.configuration.setDatabasePath(databasePath);

        Files.createDirectories(Paths.get(databasePath));
    }
}
