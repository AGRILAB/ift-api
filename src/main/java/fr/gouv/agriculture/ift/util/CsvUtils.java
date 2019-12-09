package fr.gouv.agriculture.ift.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import fr.gouv.agriculture.ift.exception.ServerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CsvUtils {

    private static final String LINE_SEPARATOR = "\n";
    private static final char COLUMN_SEPARATOR = '\t';

    public static String writeAsCSV(Class type, Object object) {

        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(type)
                .withHeader()
                .withLineSeparator(LINE_SEPARATOR)
                .withColumnSeparator(COLUMN_SEPARATOR);

        ObjectWriter writer = mapper.writer(schema);
        try {
            return writer.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new ServerException(e.getMessage());
        }
    }

    public static String writeAsCSV(Class type, Object object, String lineSeparator, char columnSeparator) {

        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(type)
                .withHeader()
                .withLineSeparator(lineSeparator)
                .withColumnSeparator(columnSeparator)
                .withoutQuoteChar();
        ObjectWriter writer = mapper.writer(schema);
        try {
            return writer.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new ServerException(e.getMessage());
        }
    }
}
