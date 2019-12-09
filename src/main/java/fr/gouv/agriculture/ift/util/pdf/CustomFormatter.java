package fr.gouv.agriculture.ift.util.pdf;

import fr.gouv.agriculture.ift.util.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CustomFormatter {

    public static String formatDate(LocalDate value) {
        return DateUtils.parseLocalDate(value);
    }

    public static String formatNumber(BigDecimal number) {
        return number.toPlainString();
    }
}
