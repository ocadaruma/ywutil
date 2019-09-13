# ywutil

[![Build Status](https://travis-ci.org/ocadaruma/ywutil.svg?branch=master)](https://travis-ci.org/ocadaruma/ywutil)

A HyperMinHash implementation written in Java

## Installation

Maven:

```xml
<dependency>
  <groupId>com.mayreh</groupId>
  <artifactId>ywutil</artifactId>
  <version>0.1.0</version>
</dependency>
```

Gradle:

```groovy
dependencies {
    compile 'com.mayreh:ywutil:0.1.0'
}
```

## Performance

- Machine: ThinkPad T470s
  - Intel(R) Core(TM) i7-7600U CPU @ 2.80GHz
  - 24GB RAM
- OS: Ubuntu 18.04.2 LTS
- JDK: openjdk version "11.0.3"

```
Benchmark                           Mode  Cnt         Score        Error  Units
HyperMinHashBenchmark.add          thrpt   10  10657456.197 ± 756933.514  ops/s
HyperMinHashBenchmark.cardinality  thrpt   10     42403.573 ±  10291.112  ops/s
HyperMinHashBenchmark.merge        thrpt   10     11662.358 ±   3091.894  ops/s
```

See [benchmark](https://github.com/ocadaruma/ywutil/tree/master/benchmark) for the details.
