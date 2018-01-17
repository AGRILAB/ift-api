package fr.gouv.agriculture.ift.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringHelper {

    public static String unaccent(final String src) {

        final String temp = Normalizer.normalize(src, Normalizer.Form.NFKD);
        final Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }

    public static String normalizeTerm(String term) {
        return unaccent(term.toUpperCase());
    }
}
