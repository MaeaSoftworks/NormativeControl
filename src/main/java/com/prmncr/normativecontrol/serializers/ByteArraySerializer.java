package com.prmncr.normativecontrol.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ByteArraySerializer extends JsonSerializer<byte[]> {

    @Override
    public void serialize(byte[] bytes, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray();
        for (byte b : bytes) {
            gen.writeNumber(b & 0xFF);
        }
        gen.writeEndArray();
    }
}
