package fr.gouv.agriculture.ift.model;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.util.Views;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BilanCulture {

    private Culture culture;

    @JsonView(Views.Internal.class)
    private List<BilanParcelleCultivee> bilanParcellesCultivees;

    private BilanParSegment bilanParSegment;
}
