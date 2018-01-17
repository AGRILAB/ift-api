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
public class BilanParcelle {

    private String nom;
    private Culture culture;
    private List<BilanTraitementParcelle> traitements;
    private TotalBilan total;

}
