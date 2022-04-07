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
            gen.writeNumber(unsignedToBytes(b));
        }
        gen.writeEndArray();
    }

    private static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }
}
