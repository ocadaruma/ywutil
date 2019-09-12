package com.mayreh.ywutil;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public class BitMapTest {

    @Test
    public void testGetInt() {
        // 00011111 10110000 00111110 00000011
        //        1 10110000 00111110 0
        // | bytes[3] >>> 7 & 11111111 << 0   18  24
        // | bytes[2] >>> 0 & 11111111 << 1   17  16
        // | bytes[1] >>> 0 & 11111111 << 9   9   8
        // | bytes[0] >>> 0 & 00000001 << 17  1   7
        final BitMap bitmap = new BitMap(toBytes(0x1fb03e03));

        // 1111 101
        // | bytes[1] >>> 5 & 11111111 << 0   7   10
        // | bytes[0] >>> 0 & 00001111 << 3   4   7
        assertThat(bitmap.getInt(4, 7)).isEqualTo(125);

        // 0001 1111
        // | bytes[0] >>> 0 & 11111111 << 0   7   7
        assertThat(bitmap.getInt(0, 8)).isEqualTo(31);

        // 0001
        // | bytes[0] >>> 4 & 11111111 << 0   4   3
        assertThat(bitmap.getInt(0, 4)).isEqualTo(1);

        // 1110
        // | bytes[2] >>> 0 & 00001111 << 0   3   23
        assertThat(bitmap.getInt(20, 4)).isEqualTo(14);

        // 0011
        // | bytes[2] >>> 4 & 11111111 << 0   3   19
        assertThat(bitmap.getInt(16, 4)).isEqualTo(3);

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
