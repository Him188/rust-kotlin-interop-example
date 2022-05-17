# rust-kotlin-interop-example

An example showing interopability between Rust and Kotlin/Native.

## Compiling the project

### Compiling Kotlin part

```shell
$ ./gradlew assemble
```

### Compiling Rust part

```shell
$ cd ./untitled
$ cargo build
```

### Compiling the whole project

First, you have to compile both the project and expect failures. Headers will be generated from both compilers.

Then compile them again to get the dynamic libraries and binaries.

### Updating bindings

To update bindings to call from Kotlin, compile the Rust part. Similarly, compile the Kotlin part to update that for
Rust.
