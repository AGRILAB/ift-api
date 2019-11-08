package fr.gouv.agriculture.ift.model;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Signature extends AbstractTimestampedEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    private String signature;

    @ApiModelProperty(hidden = true)
    @JsonView(Views.Internal.class)
    @ManyToOne
    @NotNull
    private Certificat certificat;

}
