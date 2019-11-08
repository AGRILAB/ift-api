package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.dto.BilanCertifieDTO;
import fr.gouv.agriculture.ift.dto.ParcelleCultiveeDTO;
import fr.gouv.agriculture.ift.model.BilanDTO;
import fr.gouv.agriculture.ift.model.ParcelleCultivee;

import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

public interface BilanIftService {
    BilanDTO getBilanById(UUID bilanId);
    BilanDTO getBilan(List<ParcelleCultiveeDTO> parcellesCultiveesDto);
    BilanCertifieDTO getBilanCertifie(List<ParcelleCultiveeDTO> parcellesCultiveesDto, String campagneIdMetier);
    BilanDTO getBilan(List<ParcelleCultivee> parcellesCultivees, boolean fromSignature);
    void getBilanPDF(OutputStream out, String titre, BilanCertifieDTO bilanCertifieDTO);
}
