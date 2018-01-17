package fr.gouv.agriculture.ift.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduitDoseReferenceId implements Serializable {
    private Produit produit;
    private UUID doseReferenceId;
}