package fr.gouv.agriculture.ift.dto;

import fr.gouv.agriculture.ift.model.NumeroAmm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NumeroAmmDTO {

    private UUID id;

    @NotNull
    private String idMetier;

    private Map<String, Map<String, Boolean>> validites;

    public static NumeroAmm mapToNumeroAmm(NumeroAmmDTO numeroAmmDTO) {
        NumeroAmm numeroAmm = new NumeroAmm();
        numeroAmm.setIdMetier(numeroAmmDTO.getIdMetier());
        return numeroAmm;
    }

}