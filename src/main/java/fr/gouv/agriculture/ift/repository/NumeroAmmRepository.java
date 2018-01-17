package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.NumeroAmm;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NumeroAmmRepository extends JpaRepository<NumeroAmm, UUID> {

    NumeroAmm findNumeroAmmByIdMetier(String idMetier);
    List<NumeroAmm> findNumeroAmmByIdMetierLike(String idMetier, Pageable pageable);
}
