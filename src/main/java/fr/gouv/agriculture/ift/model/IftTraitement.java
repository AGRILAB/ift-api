package fr.gouv.agriculture.ift.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class IftTraitement {

    private BigDecimal ift;

    private Segment segment;

    private Avertissement avertissement;

    /* Données en entrée */

    private Campagne campagne;
    private NumeroAmm numeroAmm;
    private Culture culture;
    private Cible cible;
    private Traitement traitement;
    private Unite unite;
    private BigDecimal dose;
    private BigDecimal volumeDeBouillie;
    private BigDecimal facteurDeCorrection;

    @NotNull
    private LocalDateTime dateCreation = LocalDateTime.now();
}
