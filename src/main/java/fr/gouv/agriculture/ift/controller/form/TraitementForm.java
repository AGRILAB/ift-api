package fr.gouv.agriculture.ift.controller.form;

import fr.gouv.agriculture.ift.model.Traitement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TraitementForm {

    @NotEmpty
    private String idMetier;

    @NotEmpty
    private String libelle;

    @NotNull
    private Boolean avantSemis;

    public static Traitement mapToTraitement(TraitementForm traitementForm) {
        Traitement traitement = new Traitement();
        traitement.setIdMetier(traitementForm.getIdMetier());
        traitement.setLibelle(traitementForm.getLibelle());
        traitement.setAvantSemis(traitementForm.getAvantSemis());
        return traitement;
    }


}
