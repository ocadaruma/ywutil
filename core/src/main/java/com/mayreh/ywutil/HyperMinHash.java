package com.mayreh.ywutil;

import java.util.List;

public class HyperMinHash {
    // constant for 0.5/ln(2)
    private static final double HLL_ALPHA_INF = 0.721347520444481703680;

    private static final int HASH_BITS = 128;

    public static class Config {
        public final int p;
        public final int numRegisters;

        public final int q;
        public final int hllQ;

        public final int r;

        public final int hllBits;

        public Config(int p, int q, int r) {
            if (p < 1 || q < 1 || r < 1) {
                throw new IllegalArgumentException("P,Q,R must be positive");
            }
            if (p + (1 << q) + r > HASH_BITS) {
                throw new IllegalArgumentException("P+2^Q+R must not exceed " + HASH_BITS);
            }
            if (q + r > Integer.SIZE) {
                throw new IllegalArgumentException("Q+R must not exceed 32");
            }

            this.p = p;
            this.q = q;
            this.r = r;

            numRegisters = 1 << p;
            hllQ = 1 << q;
            hllBits = HASH_BITS - r;
        }

        public static final Config DEFAULT = new Config(14, 6, 10);
    }

    final Config config;
    final int[] registers;

    public HyperMinHash(Config config) {
        this.config = config;
        registers = new int[config.numRegisters];
    }

    public HyperMinHash() {
        this(Config.DEFAULT);
    }

    public void add(byte[] element) {
        final byte[] hash = HashUtils.murmurHash3x64128(element, 0x1fb03e03);

        final BitMap bitmap = new BitMap(hash);
        final int register = bitmap.getInt(0, config.p);
        final int patLen = bitmap.leadingZeros(config.p, config.hllQ) + 1;
        final int rBits = bitmap.getInt(config.hllBits, config.r);

        final int packed = rBits | (patLen << config.r);

        if (packed > registers[register]) {
            registers[register] = packed;
        }
    }

    public long cardinality() {
        final double m = config.numRegisters;

        final int[] regHisto = new int[config.hllBits];
        for (int i = 0; i < config.numRegisters; i++) {
            regHisto[registers[i] >>> config.r]++;
        }

        double z = m * tau((m - regHisto[config.hllQ + 1]) / m);
        for (int i = config.hllQ; i >= 1; --i) {
            z += regHisto[i];
            z *= 0.5;
        }

        z += m * sigma(regHisto[0] / m);

        final double E = Math.round(HLL_ALPHA_INF * m * m / z);
        return (long)E;
    }

    public static HyperMinHash merge(List<? extends HyperMinHash> sketches) {
        if (sketches.isEmpty()) {
            throw new IllegalArgumentException("sketches cannot be empty");
        }

        final HyperMinHash result = new HyperMinHash();
        for (HyperMinHash sketch : sketches) {
            for (int i = 0; i < result.registers.length; i++) {
                if (sketch.registers[i] > result.registers[i]) {
                    result.registers[i] = sketch.registers[i];
                }
            }
        }
        return result;
    }

    public static double similarity(List<? extends HyperMinHash> sketches) {
        if (sketches.isEmpty()) {
            throw new IllegalArgumentException("sketches cannot be empty");
        }

        if (sketches.size() == 1) {
            return 1;
        }

        long c = 0;
        long n = 0;
        final HyperMinHash head = sketches.get(0);

        for (int i = 0; i < head.registers.length; i++) {
            if (head.registers[i] != 0) {
                boolean contains = true;
                for (HyperMinHash sketch : sketches) {
                    contains = contains && (head.registers[i] == sketch.registers[i]);
                }
                if (contains) {
                    c++;
                }
            }

            for (HyperMinHash sketch : sketches) {
                if (sketch.registers[i] != 0) {
                    n++;
                    break;
                }
            }
        }

        if (c == 0) {
            return 0;
        }

        final double[] cs = new double[sketches.size()];
        for (int i = 0; i < sketches.size(); i++) {
            cs[i] = sketches.get(i).cardinality();
        }

        final Config config = head.config;
        final double nE = expectedCollision(config.p, config.q, config.r, cs);

        if (c < nE) {
            return 0;
        }

        return (c - nE) / (double)n;
    }

    public static long intersection(List<? extends HyperMinHash> sketches) {
        if (sketches.isEmpty()) {
            throw new IllegalArgumentException("sketches cannot be empty");
        }

        return (long)(similarity(sketches) * merge(sketches).cardinality());
    }

    private static double expectedCollision(int p, int q, int r, double... cs) {
        final int _2q = 1 << q;
        final int _2r = 1 << r;

        double x = 0;
        double b1 = 0;
        double b2 = 0;

        for (int i = 1; i <= _2q; i++) {
            for (int j = 1; j <= _2r; j++) {
                if (i != _2q) {
                    double den = Math.pow(2, p + r + i);
                    b1 = (_2r + j) / den;
                    b2 = (_2r + j + 1) / den;
                } else {
                    double den = Math.pow(2, p + r + i - 1);
                    b1 = j / den;
                    b2 = (j + 1) / den;
                }

                double product = 1;
                for (double c : cs) {
                    product *= Math.pow(1 - b2, c) - Math.pow(1 - b1, c);
                }

                x += product;
            }
        }

        return x * (1 << p);
    }

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
