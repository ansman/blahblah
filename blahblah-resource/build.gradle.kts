plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.13.2"
    id("com.vanniktech.maven.publish") version "0.25.2"
}

kotlin {
    explicitApi()
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        publishAllLibraryVariants()
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "blahblah-resource"
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            resources.srcDir("src/commonMain/resources")

            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

tasks.register<Copy>("copyiOSTestResources") {
    from("$projectDir/src/commonMain/resources")
    into("build/bin/iosX64/debugTest/resources")
}

tasks.findByName("iosX64Test")!!.dependsOn("copyiOSTestResources")

android {
    namespace = "io.spherelabs.blahblahresource"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
    }

    sourceSets["main"].resources.setSrcDirs(
        listOf(
            "src/commonMain/resources",
        ),
    )
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.S01, automaticRelease = true)
    signAllPublications()
}
