package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.Configuration;
import fr.gouv.agriculture.ift.repository.ConfigurationRepository;
import fr.gouv.agriculture.ift.service.ConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    private ConfigurationRepository repository;

    public ConfigurationServiceImpl(ConfigurationRepository repository) {
        this.repository = repository;
    }

    @Override
    public String getValue(String id) {
        Configuration found = repository.findOne(id);

        if (found == null) {
            log.error("Configuration with id=" + id + " is not found in CONFIGURATION table.");
            throw new NotFoundException();
        } else {
            return found.getLibelle();
        }
    }

    @Override
    public Integer getValueAsInteger(String id) {
        return Integer.parseInt(getValue(id));
    }

    @Override
    public Long getValueAsLong(String id) {
        return Long.parseLong(getValue(id));
    }

    @Override
    public Float getValueAsFloat(String id) {
        return Float.parseFloat(getValue(id));
    }

    @Override
    public Double getValueAsDouble(String id) {
        return Double.parseDouble(getValue(id));
    }

    @Override
    public Boolean getValueAsBoolean(String id) {
        return Boolean.parseBoolean(getValue(id));
    }
}
