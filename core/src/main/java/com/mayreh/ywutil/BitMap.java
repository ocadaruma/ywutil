package com.mayreh.ywutil;

public class BitMap {
    private final byte[] bytes;

    public BitMap(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getInt(int bitIndex, int len) {
        final int endBitIdx = bitIndex + len - 1;

        final int byteIdx = bitIndex / 8;
        final int endByteIdx = endBitIdx / 8;

        int result = 0;

        // least significant byte
        final int lsb = 7 - (endBitIdx % 8);
        result |= ((bytes[endByteIdx] & 0xff) >>> lsb);

        // rest
        int bitOffset = endBitIdx % 8;
        len -= bitOffset;
        for (int i = endByteIdx - 1; i >= byteIdx; i--) {
            result |= ((bytes[i] & ((1 << Math.min(len, 8)) - 1)) << bitOffset);
            bitOffset += 8;
            len -= 8;
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
