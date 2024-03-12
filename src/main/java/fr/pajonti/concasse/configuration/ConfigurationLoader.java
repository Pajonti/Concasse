package fr.pajonti.concasse.configuration;

import fr.pajonti.concasse.helper.technical.ExitHandlerHelper;
import fr.pajonti.concasse.initializer.subinitializers.ConfigurationInitializer;
import fr.pajonti.concasse.initializer.MainInitializer;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Classe de chargement d'une configuration
 */
public class ConfigurationLoader {
    private ConfigurationLoader(){

    }

    /**
     * Permet, selon la valeur du parametre <code>config</code>, soit de charger une configuration depuis le disque
     * si un fichier de configuration est déjà present, soit de générer une nouvelle configuration si ce paramètre
     * vaut <code>null</code>
     * @param config Chemin du fichier de configuration à utiliser
     * @see ConfigurationInitializer#generateConfig
     * @return {@link Configuration} chargée ou initialisée
     */
    public static Configuration load(String config) {
        try{
            if(config == null || config.trim().isEmpty()){
                MainInitializer initializer = new MainInitializer();
                initializer.initialize();

                return initializer.getConfiguration();
            }
            else{
                return new Configuration(config);
            }
        }
        catch (IOException | SQLException e){
            ExitHandlerHelper.exit("Erreur lors du chargement de la configuration : " + e.getMessage());
            return null;
        }
    }
}
