package fr.gouv.agriculture.ift;

/**
 * Constants class will holds all projects' constants such as REGEXP, API_ROOT, ...
 */
public class Constants {

    public static final String CONF_JWT_TOKEN_SECRET = "ift.jwt.token.secret";
    public static final String CONF_JWT_TOKEN_EXPIRATION = "ift.jwt.token.expiration";

    public static final String IFT_API_ROOT = "ift-api";

    public static final String API_ROOT = "/api";
    public static final String HELLO = "/hello";
    public static final String CAMPAGNES = "/campagnes";
    public static final String GROUPES_CULTURES = "/groupes-cultures";
    public static final String TRAITEMENTS = "/traitements";
    public static final String SEGMENTS = "/segments";
    public static final String CULTURES = "/cultures";
    public static final String CIBLES = "/cibles";
    public static final String UNITES = "/unites";
    public static final String NUMEROS_AMM = "/numeros-amm";
    public static final String VALIDITES_PRODUITS = "/validites-produits";
    public static final String PRODUITS = "/produits";
    public static final String ADMIN = "/admin";
    public static final String DOSES_REFERENCE = "/doses-reference";
    public static final String DOSES_REFERENCE_CIBLE = DOSES_REFERENCE + "/cible";
    public static final String DOSES_REFERENCE_CULTURE = DOSES_REFERENCE + "/culture";
    public static final String PRODUITS_DOSES_REFERENCE = "/produits-doses-reference";
    public static final String CSV = "/csv";
    public static final String IFT = "/ift";
    public static final String TRAITEMENT = "/traitement";
    public static final String CERTIFIE = "/certifie";
    public static final String VERIFICATION_SIGNATURE = "/verification-signature";
    public static final String BILAN = "/bilan";
    public static final String AVIS = "/avis";
    public static final String AVERTISSEMENTS = "/avertissements";
    public static final String AUTH = "/auth";
    public static final String AGENTS = "/agents";

    public static final String API_HELLO_ROOT = API_ROOT + HELLO;
    public static final String API_CAMPAGNES_ROOT = API_ROOT + CAMPAGNES;
    public static final String API_GROUPES_CULTURES_ROOT = API_ROOT + GROUPES_CULTURES;
    public static final String API_TRAITEMENTS_ROOT = API_ROOT + TRAITEMENTS;
    public static final String API_SEGMENTS_ROOT = API_ROOT + SEGMENTS;
    public static final String API_CULTURES_ROOT = API_ROOT + CULTURES;
    public static final String API_CIBLES_ROOT = API_ROOT + CIBLES;
    public static final String API_UNITES_ROOT = API_ROOT + UNITES;
    public static final String API_NUMEROS_AMM_ROOT = API_ROOT + NUMEROS_AMM;
    public static final String API_VALIDITES_PRODUITS = API_ROOT + VALIDITES_PRODUITS;
    public static final String API_PRODUITS = API_ROOT + PRODUITS;
    public static final String API_DOSES_REFERENCE_ROOT = API_ROOT + DOSES_REFERENCE;
    public static final String API_PRODUITS_DOSES_REFERENCE_ROOT = API_ROOT + PRODUITS_DOSES_REFERENCE;
    public static final String API_ADMIN_ROOT = API_ROOT + ADMIN;
    public static final String API_IFT_ROOT = API_ROOT + IFT;
    public static final String API_AVIS_ROOT = API_ROOT + AVIS;
    public static final String API_AVERTISSEMENTS_ROOT = API_ROOT + AVERTISSEMENTS;

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    public static final String TOKEN = "token";

    public static final String ROLE = "role";

    /**
     * Role administrateur pour l'accès aux API de Spring Actuator lorsqu'elles sont exposées
     */
    public static final String ROLE_ADMIN = "ADMIN";

}
