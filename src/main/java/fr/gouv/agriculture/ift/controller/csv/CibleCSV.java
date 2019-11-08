package fr.gouv.agriculture.ift.controller.csv;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fr.gouv.agriculture.ift.model.Cible;
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
@JsonPropertyOrder({"code", "label"})
public class CibleCSV {

    @JsonProperty("Code")
    private String code;

    @JsonProperty("Label")
    private String label;

    public static List<CibleCSV> toCsvDTO(List<Cible> cibles) {
        List<CibleCSV> cibleCSVList = new ArrayList<>(cibles.size() + 1);

        cibles.forEach(cible ->
                cibleCSVList.add(CibleCSV.builder()
                        .code(cible.getIdMetier())
                        .label(cible.getLibelle())
                        .build()
                ));
        return cibleCSVList;
    }
}