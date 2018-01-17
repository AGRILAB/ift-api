package fr.gouv.agriculture.ift.controller.form;

import fr.gouv.agriculture.ift.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoseReferenceForm {

    @NotNull
    private UUID numeroAmmId;

    @NotNull
    private UUID campagneId;

    @NotNull
    private Boolean biocontrole;

    @NotNull
    private UUID cultureId;

    private UUID cibleId;

    @NotNull
    private UUID segmentId;

    @NotNull
    private UUID uniteId;

    private BigDecimal dose;

    public static DoseReference mapToDoseReference(DoseReferenceForm doseReferenceForm,
                                                   NumeroAmm numeroAmm,
                                                   Campagne campagne,
                                                   Culture culture,
                                                   Cible cible,
                                                   Segment segment,
                                                   Unite unite) {
        DoseReference doseReference = new DoseReference();
        doseReference.setNumeroAmm(numeroAmm);
        doseReference.setCampagne(campagne);
        doseReference.setBiocontrole(doseReferenceForm.getBiocontrole());
        doseReference.setCulture(culture);
        doseReference.setCible(cible);
        doseReference.setSegment(segment);
        doseReference.setUnite(unite);
        doseReference.setDose(doseReferenceForm.getDose());
        return doseReference;
    }

}
