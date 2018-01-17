package fr.gouv.agriculture.ift.model;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ValiditeProduit extends AbstractTimestampedEntity {

    @Id
    @ApiModelProperty(hidden = true)
    @JsonView(Views.ExtendedPublic.class)
    private UUID id;

    @NotNull
    @ManyToOne
    private NumeroAmm numeroAmm;

    @NotNull
    @ManyToOne
    private Produit produit;

    @NotNull
    @ManyToOne
    private Campagne campagne;
}
