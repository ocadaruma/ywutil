package com.mayreh.ywutil;

public class BitMap {
    private final byte[] bytes;

    public BitMap(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getInt(int bitIndex, int len) {
        final int endBitIdx = bitIndex + len;

        final int byteIdx = bitIndex / 8;
        final int endByteIdx = endBitIdx / 8;

        int result = 0;

        // least significant byte index
        final int lsb = 8 - (endBitIdx % 8);
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
        return 0;
    }
}
