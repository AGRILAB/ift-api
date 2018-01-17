package fr.gouv.agriculture.ift.controller.form;

import fr.gouv.agriculture.ift.model.Unite;
import fr.gouv.agriculture.ift.model.UniteDeConversion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UniteForm {

    @NotEmpty
    private String idMetier;

    @NotEmpty
    private String libelle;

    private UniteDeConversion uniteDeConversion;

    public static Unite mapToUnite(UniteForm uniteForm) {
        Unite unite = new Unite();
        unite.setIdMetier(uniteForm.getIdMetier());
        unite.setLibelle(uniteForm.getLibelle());
        unite.setUniteDeConversion(uniteForm.getUniteDeConversion());
        return unite;
    }


}
