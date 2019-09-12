package com.mayreh.ywutil;

public class BitMap {
    private final byte[] bytes;

    public BitMap(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getInt(int startIndex, int len) {
        int result = 0;
        int lenLeft = len;

        while(lenLeft > 0) {
            final int bitIndex = startIndex + lenLeft - 1;
            final int byteIndex = bitIndex / 8;
            final int bitLen = bitIndex % 8;

            int b = bytes[byteIndex] & 0xff;
            b >>>= (7 - bitLen);
            b &= ((1 << Math.min(len, 8)) - 1);
            b <<= len - lenLeft;

            result |= b;

            lenLeft -= bitLen;
        }

        return result;
    }

    public int leadingZeros(int bitIndex, int len) {
        final int endBitIdx = bitIndex + len - 1;

        final int byteIdx = bitIndex / 8;
        final int endByteIdx = endBitIdx / 8;

        int result = 0;

        // most significant byte
        final int msb = 8 - (bitIndex % 8);
        for (int i = msb - 1; i >= 0; i--) {
            if ((bytes[byteIdx] & (1 << i)) != 0) {
                return result;
            }
            result++;
            len--;
        }

        for (int i = byteIdx + 1; i <= endByteIdx; i++) {
            final int offset = Math.min(len, 8);
            for (int j = 0; j < offset; j++) {
                if ((bytes[i] & (1 << (7 - j))) != 0) {
                    return result;
                }
                result++;
                len--;
            }
        }

        return result;
    }
}
