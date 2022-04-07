package com.prmncr.normativecontrol.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ByteArraySerializer extends JsonSerializer<byte[]> {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    @Override
    public void serialize(byte[] bytes, JsonGenerator gen, SerializerProvider provider) throws IOException {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        gen.writeString(new String(hexChars));
    }
}
