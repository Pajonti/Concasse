package fr.pajonti.concasse.provider.database.dto;


public enum StatEnum {
    NONE(-999),
    INITIATIVE(44),
    VITALITE(11),
    PODS(40),
    AGILITE(14),
    CHANCE(13),
    INTELLIGENCE(15),
    FORCE(10),
    RESISTANCE_POUSSEE(85),
    RESISTANCE_CRITIQUE(87),
    RESISTANCE_FIXE_TERRE(54),
    RESISTANCE_FIXE_EAU(56),
    RESISTANCE_FIXE_NEUTRE(58),
    RESISTANCE_FIXE_FEU(55),
    RESISTANCE_FIXE_AIR(57),
    PUISSANCE(25),
    PUISSANCE_PIEGES(69),
    SAGESSE(12),
    PROSPECTION(48),
    FUITE(78),
    TACLE(79),
    //Litteralement 0 pour l'arme de chasse
    ARME_CHASSE(0),
    DOMMAGES_PIEGES(70),
    DOMMAGES_POUSSEE(84),
    DOMMAGES_CRITIQUES(86),
    DOMMAGES_FIXE_TERRE(88),
    DOMMAGES_FIXE_EAU(90),
    DOMMAGES_FIXE_NEUTRE(92),
    DOMMAGES_FIXE_FEU(89),
    DOMMAGES_FIXE_AIR(91),
    RESISTANCE_POURCENTAGE_TERRE(33),
    RESISTANCE_POURCENTAGE_EAU(35),
    RESISTANCE_POURCENTAGE_NEUTRE(37),
    RESISTANCE_POURCENTAGE_FEU(34),
    RESISTANCE_POURCENTAGE_AIR(36),
    ESQUIVE_PA(27),
    ESQUIVE_PM(28),
    RETRAIT_PA(82),
    RETRAIT_PM(83),
    DOMMAGES_RENVOI(50),
    POURCENTAGE_CRIT(18),
    SOIN(49),
    RESISTANCE_POURCENTAGE_MELEE(124),
    RESISTANCE_POURCENTAGE_ARMES(142),
    RESISTANCE_POURCENTAGE_DISTANTS(121),
    //Inexistant so far
    RESISTANCE_POURCENTAGE_SORTS(-99),
    DOMMAGES_MELEE(125),
    DOMMAGES_ARMES(122),
    DOMMAGES_A_DISTANCE(120),
    DOMMAGES_SORTS(123),
    DOMMAGES(16),
    INVOCATION(26),
    PORTEE(19),
    PM(23),
    PA(1);

    private Integer statCode;

    StatEnum(Integer code){
        this.statCode = code;
    }

    public Integer getStatCode() {
        return statCode;
    }

    public static StatEnum getEnumByValue(Integer statCode){
        for (StatEnum e : values()) {
            if (e.statCode.equals(statCode)) {
                return e;
            }
        }
        return null;
    }
}



