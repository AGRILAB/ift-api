package fr.gouv.agriculture.ift.dto;

import fr.gouv.agriculture.ift.model.IftTraitement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IftTraitementSigneDTO {

    UUID id;

    IftTraitement iftTraitement;

    String signature;

}
