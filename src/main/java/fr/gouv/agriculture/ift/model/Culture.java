package fr.gouv.agriculture.ift.model;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

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
public class Culture extends AbstractTimestampedEntity {

    @Id
    @ApiModelProperty(hidden = true)
    @JsonView(Views.ExtendedPublic.class)
    private UUID id;

    @NotEmpty
    private String idMetier;

    @NotEmpty
    private String libelle;

    @ApiModelProperty(hidden = true)
    @JsonView(Views.Internal.class)
    @NotEmpty
    private String normalizedLibelle;

    @ManyToOne
    @NotNull
    private GroupeCultures groupeCultures;
}
