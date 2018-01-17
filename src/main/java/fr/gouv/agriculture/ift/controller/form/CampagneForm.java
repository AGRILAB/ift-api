package fr.gouv.agriculture.ift.controller.form;

import fr.gouv.agriculture.ift.model.Campagne;
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
public class CampagneForm {

    @NotEmpty
    private String idMetier;

    @NotEmpty
    private String libelle;

    @NotNull
    private Boolean active;

    public static Campagne mapToCampagne(CampagneForm campagneForm) {
        Campagne campagne = new Campagne();
        campagne.setIdMetier(campagneForm.getIdMetier());
        campagne.setLibelle(campagneForm.getLibelle());
        campagne.setActive(campagneForm.getActive());
        return campagne;
    }

}
