extern crate bindgen;
extern crate cbindgen;

use std::env;
use std::path::PathBuf;

use cbindgen::Config;
use cbindgen::Language::C;

fn main() {
    let crate_dir = env::var("CARGO_MANIFEST_DIR").unwrap();

    cbindgen::Builder::new()
        .with_crate(crate_dir)
        .with_language(C)
        .generate()
        .expect("Unable to generate bindings")
        .write_to_file("myrust.h");


    println!("cargo:rustc-link-search=../build/bin/native/debugShared");
    println!("cargo:rustc-link-lib=mykotlin");
}