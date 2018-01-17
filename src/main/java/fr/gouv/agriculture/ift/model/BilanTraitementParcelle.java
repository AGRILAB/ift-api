package fr.gouv.agriculture.ift.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BilanTraitementParcelle extends TraitementParcelle {

    private TotalBilan total;
}
