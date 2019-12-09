package fr.gouv.agriculture.ift.controller.csv;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fr.gouv.agriculture.ift.model.Culture;
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
@JsonPropertyOrder({"codeCulture", "labelCulture", "codeGroupeCulture"})
public class CultureCSV {

    @JsonProperty("Code_culture")
    private String codeCulture;

    @JsonProperty("Label_culture")
    private String labelCulture;

    @JsonProperty("Code_groupe_culture")
    private String codeGroupeCulture;

    public static List<CultureCSV> toCsvDTO(List<Culture> cultures) {
        List<CultureCSV> cultureCSVList = new ArrayList<>(cultures.size() + 1);

        cultures.forEach(culture ->
                cultureCSVList.add(CultureCSV.builder()
                        .codeCulture(culture.getIdMetier())
                        .labelCulture(culture.getLibelle())
                        .codeGroupeCulture(culture.getGroupeCultures().getIdMetier())
                        .build()
                ));
        return cultureCSVList;
    }
}