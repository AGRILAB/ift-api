package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepository extends JpaRepository<Configuration, String> { }
