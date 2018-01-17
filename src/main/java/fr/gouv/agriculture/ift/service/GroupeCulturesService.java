package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.controller.form.GroupeCulturesForm;
import fr.gouv.agriculture.ift.model.GroupeCultures;

import java.util.List;
import java.util.UUID;

public interface GroupeCulturesService {

    GroupeCultures save(GroupeCulturesForm groupeCulturesForm);
    List<GroupeCultures> findAllGroupesCultures();
    GroupeCultures findGroupeCulturesById(UUID groupeCulturesId);
    GroupeCultures findGroupeCulturesByIdMetier(String idMetier);
    GroupeCultures updateById(UUID id, GroupeCulturesForm groupeCulturesForm);
    void delete(UUID id);

}
