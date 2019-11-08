package fr.gouv.agriculture.ift.controller.csv;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fr.gouv.agriculture.ift.model.DoseReference;
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
@JsonPropertyOrder({"campagneIdMetier", "numeroAmm", "biocontrole", "cultureIdMetier", "cultureLibelle", "cibleIdMetier", "cibleLibelle", "segmentIdMetier", "segmentLibelle", "dose", "uniteIdMetier", "uniteLibelle"})
public class DoseReferenceCSV {

    @JsonProperty("ID_campagne")
    private String campagneIdMetier;

    @JsonProperty("Code_AMM")
    private String numeroAmm;

    @JsonProperty("Biocontrole")
    private String biocontrole;

    @JsonProperty("Id_culture")
    private String cultureIdMetier;

    @JsonProperty("Culture")
    private String cultureLibelle;

    @JsonProperty("Id_cible")
    private String cibleIdMetier;

    @JsonProperty("Cible")
    private String cibleLibelle;

    @JsonProperty("Id_Segment")
    private String segmentIdMetier;

    @JsonProperty("Segment")
    private String segmentLibelle;

    @JsonProperty("Dose_ref")
    private String dose;

    @JsonProperty("Id_unite_dose_ref")
    private String uniteIdMetier;

    @JsonProperty("Unite_dose_ref")
    private String uniteLibelle;

    public static List<DoseReferenceCSV> toCsvDTO(List<DoseReference> dosesReference) {
        List<DoseReferenceCSV> doseReferenceCSVList = new ArrayList<>(dosesReference.size() + 1);

        dosesReference.forEach(doseReference ->
                doseReferenceCSVList.add(DoseReferenceCSV.builder()
                        .campagneIdMetier(doseReference.getCampagne().getIdMetier())
                        .numeroAmm(doseReference.getNumeroAmm().getIdMetier())
                        .biocontrole(getBooleanAsString(doseReference.getBiocontrole()))
                        .cultureIdMetier(doseReference.getCulture().getIdMetier())
                        .cultureLibelle(doseReference.getCulture().getLibelle())
                        .cibleIdMetier(doseReference.getCible() != null ? doseReference.getCible().getIdMetier() : "")
                        .cibleLibelle(doseReference.getCible() != null ? doseReference.getCible().getLibelle() : "")
                        .segmentIdMetier(doseReference.getSegment().getIdMetier())
                        .segmentLibelle(doseReference.getSegment().getLibelle())
                        .dose(doseReference.getDose() != null ? doseReference.getDose().toString() : "")
                        .uniteIdMetier(doseReference.getUnite().getIdMetier())
                        .uniteLibelle(doseReference.getUnite().getLibelle())
                        .build()
                ));
        return doseReferenceCSVList;
    }

    private static String getBooleanAsString(Boolean bool) {
        return bool ? "1" : "0";
    }
}