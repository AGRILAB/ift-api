package fr.gouv.agriculture.ift.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParcelleCultiveeListDTO {

    @Valid
    List<ParcelleCultiveeDTO> parcellesCultivees;

}
