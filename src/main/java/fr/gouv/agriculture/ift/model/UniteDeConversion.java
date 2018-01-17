package fr.gouv.agriculture.ift.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.gouv.agriculture.ift.dto.UniteDeConversionDeserializer;
import fr.gouv.agriculture.ift.dto.UniteDeConversionSerializer;
import fr.gouv.agriculture.ift.model.enumeration.TypeDeConversion;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(using = UniteDeConversionSerializer.class)
@JsonDeserialize(using = UniteDeConversionDeserializer.class)
public class UniteDeConversion {

    @Id
    private UUID id;

    @OneToOne
    private Unite unite;

    private TypeDeConversion type;
}
