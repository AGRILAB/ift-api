package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.model.Bilan;
import fr.gouv.agriculture.ift.model.Parcelle;

import java.util.List;

public interface BilanIftService {
    Bilan getBilan(List<Parcelle> traitements);
}
