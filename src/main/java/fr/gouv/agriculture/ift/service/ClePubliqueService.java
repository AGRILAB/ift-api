package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.model.ClePublique;

public interface ClePubliqueService {
    ClePublique findClePubliqueByCle(String cle);
    ClePublique save(ClePublique clePublique);
}
