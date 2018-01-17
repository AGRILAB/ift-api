package fr.gouv.agriculture.ift.controller.form;

import fr.gouv.agriculture.ift.model.Cible;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CibleForm {

    @NotEmpty
    private String idMetier;

    @NotEmpty
    private String libelle;

    public static Cible mapToCible(CibleForm cibleForm) {
        Cible cible = new Cible();
        cible.setIdMetier(cibleForm.getIdMetier());
        cible.setLibelle(cibleForm.getLibelle());
        return cible;
    }


}
