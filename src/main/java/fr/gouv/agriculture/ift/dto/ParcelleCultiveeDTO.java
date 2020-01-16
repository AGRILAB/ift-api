package fr.gouv.agriculture.ift.dto;

import fr.gouv.agriculture.ift.model.Culture;
import fr.gouv.agriculture.ift.model.IftTraitement;
import fr.gouv.agriculture.ift.model.Parcelle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelleCultiveeDTO {

    @NotNull
    private Parcelle parcelle;

    @NotNull
    private Culture culture;

    @Valid
    private List<IftTraitement> traitements = new ArrayList<>();

}
