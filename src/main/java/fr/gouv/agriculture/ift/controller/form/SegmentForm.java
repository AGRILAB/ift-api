package fr.gouv.agriculture.ift.controller.form;

import fr.gouv.agriculture.ift.model.Segment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SegmentForm {

    @NotEmpty
    private String libelle;

    private String description;

    public static Segment mapToSegment(SegmentForm segmentForm) {
        Segment segment = new Segment();
        segment.setLibelle(segmentForm.getLibelle());
        segment.setDescription(segmentForm.getDescription());
        return segment;
    }


}
