package fr.pajonti.concasse.configuration.fields;

public enum ConfigurationFieldsEnum {
    CONFIG_BASE_PATH("config.basePath"),
    CONFIG_DATABASE_PATH("config.databasePath");

    private String fieldName;

    ConfigurationFieldsEnum(String fieldName){
        this.fieldName = fieldName;
    }

    public String getField() {
        return fieldName;
    }

    public ConfigurationFieldsEnum getFieldByName(String fieldName){
        for(ConfigurationFieldsEnum config : values()){
            if(config.getField().equals(fieldName)){
                return config;
            }
        }
        return null;
    }
}
