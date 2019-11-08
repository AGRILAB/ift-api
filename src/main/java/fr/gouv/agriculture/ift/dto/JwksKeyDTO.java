package fr.gouv.agriculture.ift.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwksKeyDTO {

    String alg;
    String kty;
    String use;
    List<String> x5c;
    String n;
    String e;
    String kid;
    String x5t;
}
