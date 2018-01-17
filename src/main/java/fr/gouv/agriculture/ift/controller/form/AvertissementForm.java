package fr.gouv.agriculture.ift.controller.form;

import fr.gouv.agriculture.ift.model.Avertissement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvertissementForm {

    @NotEmpty
    private String idMetier;

    @NotEmpty
    private String libelle;

    public static Avertissement mapToAvertissement(AvertissementForm avertissementForm) {
        Avertissement avertissement = new Avertissement();
        avertissement.setIdMetier(avertissementForm.getIdMetier());
        avertissement.setLibelle(avertissementForm.getLibelle());
        return avertissement;
    }


}
