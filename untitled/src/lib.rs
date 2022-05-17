use std::ffi::CStr;
use std::fmt::format;

use crate::bindings::{greeting_from_kotlin, kotlin_main};

trait MyRustTrait {
    fn name(&self) -> &'static str;

    // Traits can provide default method definitions.
    fn talk(&self) {
        println!("{} says {}", self.name(), "sss");
    }
}

/// cbindgen:ignore
mod bindings;

#[cfg(test)]
mod tests {
    #[test]
    fn it_works() {
        let result = 2 + 2;
        assert_eq!(result, 4);
    }
}


#[no_mangle]
pub extern "C" fn test(i: i32) {
    unsafe {
        println!("{}", format!("rust called: parameter: {}, return from kotlin: {}",
                               i.to_string(),
                               CStr::from_ptr(greeting_from_kotlin()).to_str().unwrap()))
    }
}
//
// #[no_mangle]
// pub extern "C" fn testTrait(t: Box<dyn MyRustTrait>) {
//     t.talk();
// }

pub fn main() {
    unsafe { kotlin_main() }
}