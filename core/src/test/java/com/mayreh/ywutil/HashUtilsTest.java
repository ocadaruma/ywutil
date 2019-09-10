package com.mayreh.ywutil;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class HashUtilsTest {

    @Test
    public void testMurmurHash3x64128() {
        byte[] data = "Lorem ipsum dolor sit amet, consectetur adipisicing elit"
                .getBytes(StandardCharsets.UTF_8);

        byte[] result = HashUtils.murmurHash3x64128(data, 104729);
        ByteBuffer buffer = ByteBuffer.wrap(result);


        System.out.print(Long.toHexString(buffer.getLong(0)));
        System.out.println(Long.toHexString(buffer.getLong(8)));
//        assertThat(result).isEqualTo(0x0920e0c1b7eeb261L);

        final HyperMinHash sketch = new HyperMinHash();
        sketch.add("-1".getBytes(StandardCharsets.UTF_8));
        System.out.println(sketch.cardinality());
        for (int i = 0; i < 10000000; i++) {
            sketch.add(String.valueOf(i).getBytes(StandardCharsets.UTF_8));
        }
        System.out.println(sketch.cardinality());
    }

    private String bl(long l) {
        final String s = Long.toBinaryString(l);
        final int padding = 64 - s.length();

        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < padding; i++) {
            result.append('0');
        }
        result.append(s);
        return result.toString();
    }
}
