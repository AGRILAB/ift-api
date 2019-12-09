package fr.gouv.agriculture.ift.model;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Bilan extends AbstractTimestampedEntity {

    @Id
    @ApiModelProperty(hidden = true)
    @JsonView(Views.ExtendedPublic.class)
    private UUID id;

    @Valid
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="bilan_id")
    private List<ParcelleCultivee> parcelleCultivees = new ArrayList<>();

    @Transient
    private Campagne campagne;

    @ApiModelProperty(hidden = true)
    @JsonView(Views.Internal.class)
    @Column(name = "campagne")
    private String campagneJson;

}
