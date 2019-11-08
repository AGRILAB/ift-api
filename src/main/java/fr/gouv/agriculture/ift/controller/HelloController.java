package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.dto.HelloDTO;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_HELLO_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { Constants.HELLO }, description = "Tester l'accès à notre API :)")
public class HelloController {

    @ApiOperation(value = "hello", notes = "Retourne Hello")
    @JsonView(Views.Public.class)
    @GetMapping
    public HelloDTO hello() {
        HelloDTO helloDTO = HelloDTO.builder()
                .message("Hello from IFT API")
                .build();
        return helloDTO;
    }
}
