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
public class BilanParSegment {

    private BigDecimal herbicide = new BigDecimal("0");
    private BigDecimal biocontrole = new BigDecimal("0");
    private BigDecimal semences = new BigDecimal("0");
    private BigDecimal insecticidesAcaricides = new BigDecimal("0");
    private BigDecimal fongicidesBactericides = new BigDecimal("0");
    private BigDecimal autres = new BigDecimal("0");
    private BigDecimal total = new BigDecimal("0");

    private BigDecimal surface;
}
