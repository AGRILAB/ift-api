package fr.gouv.agriculture.ift.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Entity(name = "V_PRODUIT_DOSE_REFERENCE")
@IdClass(ProduitDoseReferenceId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProduitDoseReference {

    @Id
    @ManyToOne
    private Produit produit;

    @Id
    private UUID doseReferenceId;

    @NotNull
    @ManyToOne
    private ValiditeProduit validiteProduit;

    @NotNull
    @ManyToOne
    private NumeroAmm numeroAmm;

    @NotNull
    @ManyToOne
    private Campagne campagne;

    @NotNull
    private Boolean biocontrole;

    @NotNull
    @ManyToOne
    private Culture culture;

    @NotNull
    @ManyToOne
    private Cible cible;

    @NotNull
    @ManyToOne
    private Segment segment;

    @NotNull
    @ManyToOne
    private Unite unite;

    private BigDecimal dose;


}
