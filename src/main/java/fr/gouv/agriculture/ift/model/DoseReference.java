package fr.gouv.agriculture.ift.model;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DoseReference extends AbstractTimestampedEntity{

    @Id
    @ApiModelProperty(hidden = true)
    @JsonView(Views.ExtendedPublic.class)
    private UUID id;

    @ManyToOne
    @NotNull
    private NumeroAmm numeroAmm;

    @ManyToOne
    @NotNull
    private Campagne campagne;

    @NotNull
    private Boolean biocontrole;

    @ManyToOne
    @NotNull
    private Culture culture;

    @ManyToOne
    private Cible cible;

    @ManyToOne
    @NotNull
    private Segment segment;

    @ManyToOne
    @NotNull
    private Unite unite;

    private BigDecimal dose;
}
