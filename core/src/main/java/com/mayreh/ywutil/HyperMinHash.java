package com.mayreh.ywutil;

public class HyperMinHash {
    // constant for 0.5/ln(2)
    private static final double HLL_ALPHA_INF = 0.721347520444481703680;

    public static class Config {
        public final int p;
        public final int numRegisters;

        public final int q;
        public final int maxPatLen;

        public final int r;

        public Config(int p, int q, int r) {
            if (p < 1 || q < 1 || r < 1) {
                throw new IllegalArgumentException("P,Q,R must be positive");
            }
            if (p + (1 << q) + r > 128) {
                throw new IllegalArgumentException("P+2^Q+R must not exceed 128");
            }
            if (q + r > Integer.SIZE) {
                throw new IllegalArgumentException("Q+R must not exceed 32");
            }

            this.p = p;
            this.q = q;
            this.r = r;

            numRegisters = 1 << p;
            maxPatLen = 1 << q;
        }

        public static final Config DEFAULT = new Config(14, 6, 10);
    }

    private final Config config;
    private final int[] registers;

    public HyperMinHash(Config config) {
        this.config = config;
        registers = new int[config.numRegisters];
    }

    public HyperMinHash() {
        this(Config.DEFAULT);
    }

    public void add(byte[] element) {
        final byte[] hash = HashUtils.murmurHash3x64128(element, 0xadc83b19);

        final BitMap bitmap = new BitMap(hash);
        final int register = bitmap.getInt(0, config.p);
        final int patLen = bitmap.leadingZeros(config.p, config.q) + 1;
        final int rBits = bitmap.getInt(128 - config.r, config.r);

        final int packed = rBits | (patLen << config.r);

        registers[register] = Math.max(registers[register], packed);
    }

    public long cardinality() {
        final double m = config.numRegisters;

        final int[] regHisto = new int[64];
        for (int i = 0; i < config.numRegisters; i++) {
            regHisto[registers[i] >>> config.r]++;
        }

        double z = m * tau((m - regHisto[config.q + 1]) / m);
        for (int i = config.q; i >= 1; --i) {
            z += regHisto[i];
            z *= 0.5;
        }

        z += m * sigma(regHisto[0] / m);

        final double E = Math.round(HLL_ALPHA_INF * m * m / z);
        return (long)E;
    }
//
//    public static HyperMinHashSketch merge(Collection<? extends HyperMinHashSketch> sketches) {
//
//    }
//
//    public static double similarity(Collection<? extends HyperMinHashSketch> sketches) {
//
//    }

//    public static long intersection(Collection<? extends HyperMinHashSketch> sketches) {
//        if (sketches.isEmpty()) {
//            throw new IllegalArgumentException("sketches is empty");
//        }
//
//        return (long)(similarity(sketches) * merge(sketches).cardinality());
//    }

    private static double tau(double x) {
        if (x == 0.0 ||  x == 1.0) {
            return 0.0;
        }

        double zPrime;
        double y = 1.0;
        double z = 1 - x;
        do {
            x = Math.sqrt(x);
            zPrime = z;
            y *= 0.5;
            z -= Math.pow(1 - x, 2) * y;
        } while (zPrime != z);

        return z / 3;
    }

    private static double sigma(double x) {
        if (x == 1.0) {
            return Double.POSITIVE_INFINITY;
        }

        double zPrime;
        double y = 1;
        double z = x;
        do {
            x *= x;
            zPrime = z;
            z += x * y;
            y += y;
        } while (zPrime != z);

        return z;
    }
}
