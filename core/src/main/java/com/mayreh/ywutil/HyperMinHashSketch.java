package com.mayreh.ywutil;

import java.util.Collection;

public class HyperMinHashSketch {

//    public static class Config {
//        public final int p;
//        public final int q;
//        public final int r;
//
//        public Config(int p, int q, int r) {
//            if (p < 1 || q < 1 || r < 1) {
//                throw new IllegalArgumentException("P,Q,R must be positive");
//            }
//            if (p + (1 << q) + r > 128) {
//                throw new IllegalArgumentException("P+2^Q+R must not exceed 128");
//            }
//            if (q + r > Integer.SIZE) {
//                throw new IllegalArgumentException("Q+R must not exceed 32");
//            }
//
//            this.p = p;
//            this.q = q;
//            this.r = r;
//        }
//
//        public static final Config DEFAULT = new Config(14, 6, 10);
//    }
//
//    private final Config config;
//    private final int[] registers;
//
//    public HyperMinHashSketch(Config config) {
//        this.config = config;
//        registers = new int[1 << config.p];
//    }
//
//    public HyperMinHashSketch() {
//        this(Config.DEFAULT);
//    }
//
//    public void add(byte[] element) {
//        long hash = Utils.murmurHash64A(element, 0xadc83b19);
//
//        int register = (int) (hash >>> (Long.SIZE - config.p));
////        byte patLen =
//        int mantissa = x;
//    }
//
//    public long cardinality() {
//
//    }
//
//    public static HyperMinHashSketch merge(Collection<? extends HyperMinHashSketch> sketches) {
//
//    }
//
//    public static double similarity(Collection<? extends HyperMinHashSketch> sketches) {
//
//    }
//
//    public static long intersection(Collection<? extends HyperMinHashSketch> sketches) {
//        if (sketches.isEmpty()) {
//            return 0;
//        }
//
//        return (long)(similarity(sketches) * merge(sketches).cardinality());
//    }
}
