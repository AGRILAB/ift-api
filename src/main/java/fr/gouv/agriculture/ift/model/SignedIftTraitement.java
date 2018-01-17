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
public class SignedIftTraitement extends AbstractTimestampedEntity {

    @Id
    @ApiModelProperty(hidden = true)
    @JsonView(Views.ExtendedPublic.class)
    private UUID id;

    @Transient
    IftTraitement iftTraitement;

    @ApiModelProperty(hidden = true)
    @JsonView(Views.ExtendedPublic.class)
    @Column(name = "ift_traitement")
    @NotNull
    private String iftTraitementJson;

    @NotNull
    private String signature;

    @ApiModelProperty(hidden = true)
    @JsonView(Views.ExtendedPublic.class)
    @ManyToOne
    @NotNull
    private ClePublique clePublique;
}
