package fr.gouv.agriculture.ift.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BilanGroupeCultures {

    private GroupeCultures groupeCultures;
    private List<BilanCulture> bilanCultures;
    private BilanParSegment bilanParSegment;
}
