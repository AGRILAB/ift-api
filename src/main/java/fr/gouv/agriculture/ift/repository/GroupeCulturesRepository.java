package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.GroupeCultures;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupeCulturesRepository extends JpaRepository<GroupeCultures, UUID> {

    GroupeCultures findGroupeCulturesByIdMetier(String idMetier);
}
