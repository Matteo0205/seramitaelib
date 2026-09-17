plugins {
    id("com.android.library") version "8.13.2"
    id("maven-publish")
}

val libraryVersion = providers.gradleProperty("libraryVersion").getOrElse("0.2.3")

group = "org.seramitae"
version = libraryVersion

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

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    compileOnly("org.firstinspires.ftc:RobotCore:11.2.1")
    compileOnly("org.firstinspires.ftc:Hardware:11.2.1")
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = project.group.toString()
                artifactId = "seramitaelib"
                version = project.version.toString()
            }
        }

        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/Matteo0205/seramitaelib")
                credentials {
                    username = findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                    password = findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}
