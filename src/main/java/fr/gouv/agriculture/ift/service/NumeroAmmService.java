package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.dto.NumeroAmmDTO;
import fr.gouv.agriculture.ift.model.NumeroAmm;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface NumeroAmmService {

    List<NumeroAmm> findNumerosAmm(String campagneIdMetier, String cultureIdMetier, String cibleIdMetier, String filtre, Pageable pageable);
    List<NumeroAmm> findAllNumerosAmm();
    List<NumeroAmm> findAllNumerosAmm(Pageable pageable);
    List<NumeroAmm> findNumerosAmmByCampagneAndCultureAndCible(String campagneIdMetier, String cultureIdMetier, String cibleIdMetier, String filtre, Pageable pageable);
    List<NumeroAmm> findNumerosAmmByCampagneAndCultureAndProduitAndCible(String campagneIdMetier, String cultureIdMetier, String produitLibelle, String cibleIdMetier);

    List<NumeroAmmDTO> findNumerosAmmWithValidities(String filtre, Pageable pageable);
    NumeroAmm findNumeroAmmById(UUID numeroAmmId);
    NumeroAmm findNumeroAmmById(UUID numeroAmmId, Class<? extends Throwable> throwableClass);
    NumeroAmm findNumeroAmmByIdMetier(String idMetier);
    NumeroAmm findNumeroAmmByIdMetierWithCache(String idMetier);
    void cleanCache();
    NumeroAmmDTO save(NumeroAmmDTO numeroAmmDTO);
    NumeroAmmDTO updateById(UUID id, NumeroAmmDTO numeroAmmDTO);
    void delete(UUID id);
    String addNumerosAmm(InputStream inputStream);
}
