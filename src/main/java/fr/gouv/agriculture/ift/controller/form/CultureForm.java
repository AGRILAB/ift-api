package fr.gouv.agriculture.ift.controller.form;

import fr.gouv.agriculture.ift.model.Culture;
import fr.gouv.agriculture.ift.model.GroupeCultures;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CultureForm {

    @NotEmpty
    private String idMetier;

    @NotEmpty
    private String libelle;

    @NotNull
    private UUID groupeCulturesId;

    public static Culture mapToCulture(CultureForm cultureForm, GroupeCultures groupeCultures) {
        Culture culture = new Culture();
        culture.setIdMetier(cultureForm.getIdMetier());
        culture.setLibelle(cultureForm.getLibelle());
        culture.setGroupeCultures(groupeCultures);
        return culture;
    }

}
