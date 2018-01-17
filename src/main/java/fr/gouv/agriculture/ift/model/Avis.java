package fr.gouv.agriculture.ift.model;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Avis extends AbstractTimestampedEntity {

    @ApiModelProperty(hidden = true)
    @JsonView(Views.Internal.class)
    @Id
    private UUID id;

    @NotNull
    private Integer note;

    private String commentaire;

    @JsonView(Views.Internal.class)
    private String contact;
}
