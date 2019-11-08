package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.dto.JwksDTO;
import fr.gouv.agriculture.ift.service.CertificatService;
import fr.gouv.agriculture.ift.util.Views;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_JWKS, produces = MediaType.APPLICATION_JSON_VALUE)
public class JwksController {

    @Autowired
    CertificatService certificatService;

    @JsonView(Views.Public.class)
    @GetMapping
    public JwksDTO getJwks(){

        return certificatService.getAllAsJwks();
    }
}
