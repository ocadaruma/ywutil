package com.mayreh.ywutil;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilsTest {

    @Test
    public void testMurmurHash3x64128() {
        byte[] data = "Lorem ipsum dolor sit amet, consectetur adipisicing elit"
                .getBytes(StandardCharsets.UTF_8);

        byte[] result = Utils.murmurHash3x64128(data, 104729);
        ByteBuffer buffer = ByteBuffer.wrap(result);


        System.out.print(Long.toHexString(buffer.getLong(0)));
        System.out.println(Long.toHexString(buffer.getLong(8)));
//        assertThat(result).isEqualTo(0x0920e0c1b7eeb261L);

    }
}
