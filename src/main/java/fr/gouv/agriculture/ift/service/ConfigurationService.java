package fr.gouv.agriculture.ift.service;

public interface ConfigurationService {

    /**
     * @param id Identifiant de la variable de configuration (i.e. son nom)
     * @return Retourne la valeur correspondant à l'id fourni
     */
    String getValue(String id);

    /**
     * @param id Identifiant de la variable de configuration (i.e. son nom)
     * @return Retourne la valeur correspondant à l'id fourni sous la forme d'un Integer
     */
    Integer getValueAsInteger(String id);

    /**
     * @param id Identifiant de la variable de configuration (i.e. son nom)
     * @return Retourne la valeur correspondant à l'id fourni sous la forme d'un Long
     */
    Long getValueAsLong(String id);

    /**
     * @param id Identifiant de la variable de configuration (i.e. son nom)
     * @return Retourne la valeur correspondant à l'id fourni sous la forme d'un Float
     */
    Float getValueAsFloat(String id);

    /**
     * @param id Identifiant de la variable de configuration (i.e. son nom)
     * @return Retourne la valeur correspondant à l'id fourni sous la forme d'un Double
     */
    Double getValueAsDouble(String id);

    /**
     * @param id Identifiant de la variable de configuration (i.e. son nom)
     * @return Retourne la valeur correspondant à l'id fourni sous la forme d'un Boolean
     */
    Boolean getValueAsBoolean(String id);
}
