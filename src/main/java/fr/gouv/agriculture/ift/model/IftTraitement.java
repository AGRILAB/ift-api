package fr.gouv.agriculture.ift.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    private BigDecimal doseReference;

    private Unite uniteDoseReference;

    private String produitLibelle;

    /* Données en entrée */

    @NotNull
    private Campagne campagne;

    @NotNull
    private Culture culture;

    @NotNull
    private TypeTraitement typeTraitement;

    private NumeroAmm numeroAmm;

    private Cible cible;

    private Unite unite;

    private BigDecimal dose;
    private BigDecimal volumeDeBouillie;
    private BigDecimal facteurDeCorrection;

    private LocalDateTime dateCreation = LocalDateTime.now();

    private String commentaire;

    /* Données utilisées pour le bilan */
    private LocalDate dateTraitement;

    @Transient
    @ApiModelProperty(hidden = true)
    @JsonView(Views.Internal.class)
    private String qrCodeUrl;
}
