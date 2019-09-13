package com.mayreh.ywutil.benchmark;

import com.mayreh.ywutil.HyperMinHash;
import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.concurrent.TimeUnit.SECONDS;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(SECONDS)
@Threads(1)
@Fork(1)
@Warmup(iterations = 5, time = 1, timeUnit = SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = SECONDS)
public class HyperMinHashBenchmark {
    private static final int SIZE = 1_000_000;

    @State(Scope.Thread)
    public static class AddState {
        Random r;
        byte[][] elements;
        HyperMinHash sketch;

        @Setup(Level.Trial)
        public void setup() {
            r = ThreadLocalRandom.current();
        }

        @Setup(Level.Invocation)
        public void initialize() {
            sketch = new HyperMinHash();
            elements = new byte[SIZE][];
            for (int i = 0; i < elements.length; i++) {
                elements[i] = UUID.randomUUID().toString().getBytes();
            }
        }
    }

    @State(Scope.Thread)
    public static class CardinalityState {
        Random r;
        byte[][] elements;
        HyperMinHash sketch;

        @Setup(Level.Trial)
        public void setup() {
            r = ThreadLocalRandom.current();
        }

        @Setup(Level.Invocation)
        public void initialize() {
            sketch = new HyperMinHash();

            for (int i = 0; i < SIZE; i++) {
                sketch.add(UUID.randomUUID().toString().getBytes());
            }
        }
    }

    @State(Scope.Thread)
    public static class MergeState {
        Random r;

        HyperMinHash sketch;
        HyperMinHash otherSketch;
        List<HyperMinHash> sketches;

        @Setup(Level.Trial)
        public void setup() {
            r = ThreadLocalRandom.current();
        }

        @Setup(Level.Invocation)
        public void initialize() {
            sketch = new HyperMinHash();
            otherSketch = new HyperMinHash();
            sketches = Arrays.asList(sketch, otherSketch);

            for (int i = 0; i < SIZE; i++) {
                sketch.add(UUID.randomUUID().toString().getBytes());
                otherSketch.add(UUID.randomUUID().toString().getBytes());
            }
        }
    }

    @Benchmark
    @OperationsPerInvocation(SIZE)
    public void add(AddState state) {
        for (byte[] element : state.elements) {
            state.sketch.add(element);
        }
    }

    @Benchmark
    public void cardinality(CardinalityState state) {
        state.sketch.cardinality();
    }

    @Benchmark
    public void merge(MergeState state) {
        HyperMinHash.merge(state.sketches);
    }
}
