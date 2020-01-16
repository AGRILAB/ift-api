package fr.gouv.agriculture.ift.controller.csv;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fr.gouv.agriculture.ift.model.ValiditeProduit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"campagneIdMetier", "nomProduit", "AMM"})
public class ProduitCSV {

    @JsonProperty("ID_campagne")
    private String campagneIdMetier;

    @JsonProperty("Nom_produit")
    private String nomProduit;

    @JsonProperty("AMM")
    private String numeroAmm;

    public static List<ProduitCSV> toCsvDTO(List<ValiditeProduit> validitesProduit) {
        List<ProduitCSV> validiteProduitCSVList = new ArrayList<>(validitesProduit.size() + 1);

        validitesProduit.forEach(validiteProduit ->
                validiteProduitCSVList.add(ProduitCSV.builder()
                        .campagneIdMetier(validiteProduit.getCampagne().getIdMetier())
                        .nomProduit(validiteProduit.getProduit().getLibelle())
                        .numeroAmm(validiteProduit.getNumeroAmm().getIdMetier())
                        .build()
                ));
        return validiteProduitCSVList;
    }
}