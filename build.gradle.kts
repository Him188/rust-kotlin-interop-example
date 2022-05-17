plugins {
    kotlin("multiplatform") version "1.7.0-Beta"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(BOTH) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosArm64("native") {
            val myrust by compilations.getByName("main").cinterops.creating {
                headers(project.projectDir.resolve("untitled/myrust.h"))
            }

            binaries {
                sharedLib {
                    linkerOpts("-v")
                    linkerOpts("-L${project.projectDir.resolve("untitled/target/debug/").absolutePath}")
//                    linkerOpts("-lmyrust")
                    linkerOpts("-Wl,-undefined,dynamic_lookup") // resolve symbols in runtime
//                    project.projectDir.resolve("untitled/target/debug/deps/untitled26-b64bf0fedd2d648a").walk().filter { it.isDirectory }.forEach {
//                        linkerOpts("-L${it.normalize().absolutePath.replace("\\", "/")}")
//                    }
                    baseName = "mykotlin"
                }

                executable {

                    linkerOpts("-v")
                    linkerOpts("-L${project.projectDir.resolve("untitled/target/debug/").absolutePath}")
                    linkerOpts("-lmyrust")
//                    project.projectDir.resolve("untitled/target/debug/deps/untitled26-b64bf0fedd2d648a").walk().filter { it.isDirectory }.forEach {
//                        linkerOpts("-L${it.normalize().absolutePath.replace("\\", "/")}")
//                    }
                    baseName = "KotlinExecutable"
                    entryPoint = "main.main"
                }
            }
        }
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }


    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting {

        }
        val nativeTest by getting
    }
}

val generateCBindings = tasks.register("generateCBindings") {
    dependsOn(tasks.getByName("linkDebugSharedNative"))
    doLast {
        val bindings = projectDir.resolve("untitled/src/bindings.rs")
        bindings.delete()
        ProcessBuilder().apply {
            redirectOutput(buildDir.resolve("output.txt"))
//            command(
//                "bindgen",
//                buildDir.resolve("bin/native/debugShared/libmykotlin_api.h").canonicalPath,
//                "-o ${bindings.absolutePath}"
//            )
            command(
                "sh",
                "-c",
                "bindgen \"${buildDir.resolve("bin/native/debugShared/libmykotlin_api.h").canonicalPath}\" -o \"${bindings.absolutePath}\""
            )
        }.start().waitFor().let { r ->
            check(r == 0) { "r=$r" }
        }
    }
}

tasks.getByName("assemble").dependsOn(generateCBindings)