package com.mayreh.ywutil;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public class BitMapTest {

    @Test
    public void testGetInt() {
        // 00011111 10110000 00111110 00000011
        final BitMap bitmap = new BitMap(toBytes(0x1fb03e03));

        // 1111 101
        assertThat(bitmap.getInt(4, 7)).isEqualTo(125);

        // 0001 1111
        assertThat(bitmap.getInt(0, 8)).isEqualTo(31);

        // 0001
        assertThat(bitmap.getInt(0, 4)).isEqualTo(1);

        // 1110
        assertThat(bitmap.getInt(20, 4)).isEqualTo(14);

        // 1 10110000 00111110 0
        assertThat(bitmap.getInt(7, 18)).isEqualTo(221308);

        // full
        assertThat(bitmap.getInt(0, 32)).isEqualTo(0x1fb03e03);
    }

    @Test
    public void testLeadingZeros() {
        // 00011111 10110000 00111110 00000011
        final BitMap bitmap = new BitMap(toBytes(0x1fb03e03));

        // 1111 101
        assertThat(bitmap.leadingZeros(4, 7)).isEqualTo(0);

        // 0001 1111
        assertThat(bitmap.leadingZeros(0, 8)).isEqualTo(3);

        // 0001
        assertThat(bitmap.leadingZeros(0, 4)).isEqualTo(3);

        // 0000 001
        assertThat(bitmap.leadingZeros(12, 7)).isEqualTo(6);

        // 000011
        assertThat(bitmap.leadingZeros(26, 5)).isEqualTo(4);

        // 00000000 00000000 00000000 00000011
        final BitMap bitmap2 = new BitMap(toBytes(3));
        assertThat(bitmap2.leadingZeros(0, 32)).isEqualTo(30);
        assertThat(bitmap2.leadingZeros(1, 30)).isEqualTo(29);
    }

    private byte[] toBytes(int i) {
        final ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(i);

        return buffer.array();
    }
}
