package fr.gouv.agriculture.ift.model;

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
public class Certificat extends AbstractTimestampedEntity {

    @Id
    private UUID id;

    @NotNull
    private String cert;
}
