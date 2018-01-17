package fr.gouv.agriculture.ift.controller.form;

import fr.gouv.agriculture.ift.model.Produit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProduitForm {

    @NotEmpty
    private String libelle;

    public static Produit mapToProduit(ProduitForm produitForm) {
        Produit produit = new Produit();
        produit.setLibelle(produitForm.getLibelle());
        return produit;
    }

}
