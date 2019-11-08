package fr.gouv.agriculture.ift.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BilanDTO {

    private List<BilanParcelleCultivee> bilanParcellesCultivees;
    private List<BilanGroupeCultures> bilanGroupesCultures;
    private List<BilanParcelle> bilanParcelles;
    private BilanParSegment bilanParSegment;
    private Campagne campagne;
}
