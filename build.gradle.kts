plugins {
    id("com.android.library") version "8.13.2"
}

group = "org.seramitae.ftc"
version = "0.1.0"

android {
    namespace = "org.seramitae.ftc"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    compileOnly("org.firstinspires.ftc:RobotCore:11.2.1")
    compileOnly("org.firstinspires.ftc:Hardware:11.2.1")
}