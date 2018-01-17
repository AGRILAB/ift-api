package fr.gouv.agriculture.ift.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.gouv.agriculture.ift.model.UniteDeConversion;
import fr.gouv.agriculture.ift.util.Views;

import java.io.IOException;

public class UniteDeConversionSerializer extends StdSerializer<UniteDeConversion> {

    public UniteDeConversionSerializer() {
        this(null);
    }

    public UniteDeConversionSerializer(Class<UniteDeConversion> t) {
        super(t);
    }

    @Override
    public void serialize(
            UniteDeConversion value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        Class<?> activeView = provider.getActiveView();
        if (activeView != null && Views.ExtendedPublic.class.isAssignableFrom(activeView)){
            jgen.writeStringField("id", value.getId().toString());
        }
        jgen.writeStringField("unite", value.getUnite().getIdMetier());
        jgen.writeStringField("type", value.getType().toString());
        jgen.writeEndObject();
    }
}
