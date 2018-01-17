package fr.gouv.agriculture.ift.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import fr.gouv.agriculture.ift.model.Unite;
import fr.gouv.agriculture.ift.model.UniteDeConversion;
import fr.gouv.agriculture.ift.model.enumeration.TypeDeConversion;

import java.io.IOException;
import java.util.UUID;

public class UniteDeConversionDeserializer extends StdDeserializer<UniteDeConversion> {

    public UniteDeConversionDeserializer(){
        this(null);
    }

    public UniteDeConversionDeserializer(Class<UniteDeConversion> t){
        super(t);
    }

    @Override
    public UniteDeConversion deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        UUID id = null;
        if (node.get("id") != null){
            id = UUID.fromString(node.get("id").asText());
        }
        String unite = node.get("unite").asText();
        TypeDeConversion type = TypeDeConversion.valueOf(node.get("type").asText());

        UniteDeConversion uniteDeConversion = UniteDeConversion.builder()
                .unite(Unite.builder().idMetier(unite).build())
                .type(type)
                .build();

        if (id != null){
            uniteDeConversion.setId(id);
        }

        return uniteDeConversion;
    }
}
