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

        final byte[] result = HashUtils.murmurHash3x64128(data, 104729);
        final ByteBuffer buffer = ByteBuffer.wrap(result);
        final String hex = Long.toHexString(buffer.getLong(0)) + Long.toHexString(buffer.getLong(8));

        assertThat(hex).isEqualTo("6769dae0ba0f9ccf7e4bd221908cfc07");
    }

    @Test
    public void testCard() {
        final HyperMinHash sketch = new HyperMinHash();
        sketch.add("-1".getBytes(StandardCharsets.UTF_8));
        System.out.println(sketch.cardinality());
        for (int i = 0; i < 10000000; i++) {
            sketch.add(String.valueOf(i * 4).getBytes(StandardCharsets.UTF_8));
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
