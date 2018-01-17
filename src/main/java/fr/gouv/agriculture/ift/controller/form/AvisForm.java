package fr.gouv.agriculture.ift.controller.form;

import fr.gouv.agriculture.ift.model.Avis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvisForm {

    @NotNull
    @Min(1)
    @Max(5)
    private Integer note;

    @Size(max = 1500)
    private String commentaire;

    @Size(max = 1500)
    private String contact;

    public static Avis mapToAvis(AvisForm avisForm) {
        Avis avis = new Avis();
        avis.setNote(avisForm.getNote());
        avis.setCommentaire(avisForm.getCommentaire());
        avis.setContact(avisForm.getContact());
        return avis;
    }

}
