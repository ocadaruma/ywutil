package com.mayreh.ywutil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Utils {
    /*
     * 64 bit version of MurmurHash2
     */
    public static long murmurHash64A(byte[] data, int seed) {
        int len = data.length;

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = (seed & 0xffffffffL) ^ (len * m);

        int p = 0;
        int end = len - (len & 7);

        while (p != end) {
            long k = (long)data[p] & 0xffL;
            k |= ((long)data[p + 1] & 0xffL) << 8;
            k |= ((long)data[p + 2] & 0xffL) << 16;
            k |= ((long)data[p + 3] & 0xffL) << 24;
            k |= ((long)data[p + 4] & 0xffL) << 32;
            k |= ((long)data[p + 5] & 0xffL) << 40;
            k |= ((long)data[p + 6] & 0xffL) << 48;
            k |= ((long)data[p + 7] & 0xffL) << 56;

            k *= m;
            k ^= k >>> r;
            k *= m;
            h ^= k;
            h *= m;

            p += 8;
        }

        switch (len & 7) {
            case 7: h ^= ((long)data[p + 6] & 0xffL) << 48;
            case 6: h ^= ((long)data[p + 5] & 0xffL) << 40;
            case 5: h ^= ((long)data[p + 4] & 0xffL) << 32;
            case 4: h ^= ((long)data[p + 3] & 0xffL) << 24;
            case 3: h ^= ((long)data[p + 2] & 0xffL) << 16;
            case 2: h ^= ((long)data[p + 1] & 0xffL) << 8;
            case 1:
                h ^= (long)data[p] & 0xffL;
                h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        return h;
    }

    private static long rotl64(long x, int r) {
        return (x << r) | (x >>> (64 -r));
    }

    private static long fmix64(long k) {
        k ^= (k >>> 33);
        k *= 0xff51afd7ed558ccdL;
        k ^= (k >>> 33);
        k *= 0xc4ceb9fe1a85ec53L;
        k ^= (k >>> 33);

        return k;
    }

    /*
     * 128 bit version of MurmurHash3 for x64
     */
    public static byte[] murmurHash3x64128(byte[] data, int seed) {
        final int len = data.length;
        final int nblocks = len / 16;

        long h1 = seed;
        long h2 = seed;

        final long c1 = 0x87c37b91114253d5L;
        final long c2 = 0x4cf5ad432745937fL;

        final ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < nblocks; i++) {
            long k1 = buffer.getLong((i*2 + 0) * 8);
            long k2 = buffer.getLong((i*2 + 1) * 8);

            k1 *= c1; k1 = rotl64(k1, 31); k1 *= c2; h1 ^= k1;

            h1 = rotl64(h1, 27); h1 += h2; h1 = h1*5 + 0x52dce729;

            k2 *= c2; k2 = rotl64(k2, 33); k2 *= c1; h2 ^= k2;

            h2 = rotl64(h2, 31); h2 += h1; h2 = h2*5 + 0x38495ab5;
        }

        long k1 = 0;
        long k2 = 0;

        final int tail = nblocks * 16;
        switch (len & 15) {
            case 15: k2 ^= ((long)data[tail + 14] & 0xff) << 48;
            case 14: k2 ^= ((long)data[tail + 13] & 0xff) << 40;
            case 13: k2 ^= ((long)data[tail + 12] & 0xff) << 32;
            case 12: k2 ^= ((long)data[tail + 11] & 0xff) << 24;
            case 11: k2 ^= ((long)data[tail + 10] & 0xff) << 16;
            case 10: k2 ^= ((long)data[tail +  9] & 0xff) << 8;
            case  9: k2 ^= ((long)data[tail +  8] & 0xff) << 0;
                k2 *= c2; k2 = rotl64(k2, 33); k2 *= c1; h2 ^= k2;

            case  8: k1 ^= ((long)data[tail +  7] & 0xff) << 56;
            case  7: k1 ^= ((long)data[tail +  6] & 0xff) << 48;
            case  6: k1 ^= ((long)data[tail +  5] & 0xff) << 40;
            case  5: k1 ^= ((long)data[tail +  4] & 0xff) << 32;
            case  4: k1 ^= ((long)data[tail +  3] & 0xff) << 24;
            case  3: k1 ^= ((long)data[tail +  2] & 0xff) << 16;
            case  2: k1 ^= ((long)data[tail +  1] & 0xff) << 8;
            case  1: k1 ^= ((long)data[tail +  0] & 0xff) << 0;
                k1 *= c1; k1 = rotl64(k1, 31); k1 *= c2; h1 ^= k1;
        }

        h1 ^= len; h2 ^= len;
        h1 += h2;
        h2 += h1;

        h1 = fmix64(h1);
        h2 = fmix64(h2);

        h1 += h2;
        h2 += h1;

        final ByteBuffer result = ByteBuffer.allocate(16);
        result.putLong(h1);
        result.putLong(h2);

        return result.array();
    }
}
