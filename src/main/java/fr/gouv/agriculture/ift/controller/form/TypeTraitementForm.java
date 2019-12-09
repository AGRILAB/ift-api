package fr.gouv.agriculture.ift.controller.form;

import fr.gouv.agriculture.ift.model.TypeTraitement;
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
public class TypeTraitementForm {

    @NotEmpty
    private String libelle;

    @NotNull
    private Boolean avantSemis;

    public static TypeTraitement mapToTypeTraitement(TypeTraitementForm typeTraitementForm) {
        TypeTraitement typeTraitement = new TypeTraitement();
        typeTraitement.setLibelle(typeTraitementForm.getLibelle());
        typeTraitement.setAvantSemis(typeTraitementForm.getAvantSemis());
        return typeTraitement;
    }


}
