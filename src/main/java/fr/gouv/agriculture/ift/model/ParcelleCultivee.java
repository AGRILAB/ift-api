package fr.gouv.agriculture.ift.model;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ParcelleCultivee extends AbstractTimestampedEntity {

    @Id
    @ApiModelProperty(hidden = true)
    @JsonView(Views.ExtendedPublic.class)
    private UUID id = UUID.randomUUID();

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    private Parcelle parcelle;

    @Transient
    private Culture culture;

    @ApiModelProperty(hidden = true)
    @JsonView(Views.Internal.class)
    @Column(name = "culture")
    @NotNull
    private String cultureJson;

    @Valid
    @Transient
    private List<IftTraitement> traitements = new ArrayList<>();

    @JsonView(value = Views.Internal.class)
    @OneToMany
    @JoinColumn(name="parcelle_cultivee_id")
    private List<Signature> signatures;

}
