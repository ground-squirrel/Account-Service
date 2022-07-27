package account.business;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

public class RolesWrapperSerializer extends JsonSerializer<List<String>> {
    @Override
    public void serialize(List<String> list, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {

        gen.writeStartArray();
        for (String value : list) {
            gen.writeString(value);
        }
        gen.writeEndArray();

    }
}
