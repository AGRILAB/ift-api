package fr.gouv.agriculture.ift.model;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
public abstract class AbstractTimestampedEntity {

    @ApiModelProperty(hidden = true)
    @JsonView(Views.Internal.class)
    @NotNull
    private LocalDateTime dateCreation = LocalDateTime.now();

    @ApiModelProperty(hidden = true)
    @JsonView(Views.Internal.class)
    @NotNull
    private LocalDateTime dateDerniereMaj = LocalDateTime.now();
}
