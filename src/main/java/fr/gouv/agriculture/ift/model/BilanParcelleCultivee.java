package fr.gouv.agriculture.ift.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BilanParcelleCultivee {

    private ParcelleCultivee parcelleCultivee;

    private BilanParSegment bilanParSegment;

}
