package fr.gouv.agriculture.ift.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalBilan {

    private BigDecimal herbicide = new BigDecimal("0");
    private BigDecimal horsHerbicide = new BigDecimal("0");
}
