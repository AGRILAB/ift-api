package fr.gouv.agriculture.ift.controller.form;

import fr.gouv.agriculture.ift.model.GroupeCultures;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupeCulturesForm {

    @NotEmpty
    private String idMetier;

    @NotEmpty
    private String libelle;

    public static GroupeCultures mapToGroupeCultures(GroupeCulturesForm groupeCulturesForm) {
        GroupeCultures groupeCultures = new GroupeCultures();
        groupeCultures.setIdMetier(groupeCulturesForm.getIdMetier());
        groupeCultures.setLibelle(groupeCulturesForm.getLibelle());
        return groupeCultures;
    }


}
