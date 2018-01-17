package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.model.*;
import fr.gouv.agriculture.ift.service.BilanIftService;
import fr.gouv.agriculture.ift.service.IftService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BilanIftServiceImpl implements BilanIftService {

    @Autowired
    private IftService iftService;

    @Override
    public Bilan getBilan(List<Parcelle> parcelles) {
        Bilan bilan = Bilan.builder()
                .bilanParcelles(new ArrayList<>())
                .total(new TotalBilan())
                .build();

        BigDecimal totalHerbicide = new BigDecimal("0");
        BigDecimal pourcentageHerbicide = new BigDecimal("0");
        BigDecimal totalHorsHerbicide = new BigDecimal("0");
        BigDecimal pourcentageHorsHerbicide = new BigDecimal("0");

        for (Parcelle parcelle : parcelles) {
            BilanParcelle bilanParcelle = BilanParcelle.builder()
                    .nom(parcelle.getNom())
                    .culture(parcelle.getCulture())
                    .traitements(new ArrayList<>())
                    .total(new TotalBilan())
                    .build();

            for (TraitementParcelle traitementParcelle : parcelle.getTraitements()) {
                IftTraitement traitement = traitementParcelle.getIftTraitement();
                IftTraitement iftTraitement = iftService.computeIftTraitement(traitement.getCampagne().getIdMetier(), traitement.getNumeroAmm() != null ? traitement.getNumeroAmm().getIdMetier() : null, traitement.getCulture().getIdMetier(), traitement.getCible() != null ? traitement.getCible().getIdMetier() : null, traitement.getTraitement().getIdMetier(), traitement.getUnite() != null ? traitement.getUnite().getIdMetier() : null, traitement.getDose(), traitement.getVolumeDeBouillie(), traitement.getFacteurDeCorrection());

                BilanTraitementParcelle bilanTraitementParcelle = new BilanTraitementParcelle();
                bilanTraitementParcelle.setDate(traitementParcelle.getDate());
                bilanTraitementParcelle.setIftTraitement(iftTraitement);
                bilanTraitementParcelle.setTotal(new TotalBilan());

                if (iftTraitement.getSegment().getIdMetier().equals("S3")) {
                    bilanTraitementParcelle.getTotal().setHerbicide(iftTraitement.getIft());
                    bilanParcelle.getTotal().setHerbicide(bilanParcelle.getTotal().getHerbicide().add(iftTraitement.getIft()));

                    totalHerbicide = totalHerbicide.add(iftTraitement.getIft());
                    pourcentageHerbicide = pourcentageHerbicide.add(iftTraitement.getFacteurDeCorrection());
                } else {
                    bilanTraitementParcelle.getTotal().setHorsHerbicide(iftTraitement.getIft());
                    bilanParcelle.getTotal().setHorsHerbicide(bilanParcelle.getTotal().getHorsHerbicide().add(iftTraitement.getIft()));

                    totalHorsHerbicide = totalHorsHerbicide.add(iftTraitement.getIft());
                    pourcentageHorsHerbicide = pourcentageHorsHerbicide.add(iftTraitement.getFacteurDeCorrection());
                }

                bilanParcelle.getTraitements().add(bilanTraitementParcelle);
            }

            bilan.getBilanParcelles().add(bilanParcelle);
        }

        if (!pourcentageHerbicide.equals(new BigDecimal("0"))) {
            bilan.getTotal().setHerbicide(totalHerbicide.divide(pourcentageHerbicide.divide(new BigDecimal("100"), 10, RoundingMode.CEILING), 10, RoundingMode.CEILING));
        }
        if (!pourcentageHorsHerbicide.equals(new BigDecimal("0"))) {
            bilan.getTotal().setHorsHerbicide(totalHorsHerbicide.divide(pourcentageHorsHerbicide.divide(new BigDecimal("100"), 10, RoundingMode.CEILING), 10, RoundingMode.CEILING));
        }
        return bilan;
    }
}
