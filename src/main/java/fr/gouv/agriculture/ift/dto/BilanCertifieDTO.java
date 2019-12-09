package fr.gouv.agriculture.ift.dto;

import fr.gouv.agriculture.ift.model.BilanDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BilanCertifieDTO {
    private BilanDTO bilanDTO;
    private String verificationUrl;
}
