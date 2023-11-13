import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("org.jetbrains.intellij") version "1.8.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.maven.apache.org/maven2") }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    localPath.set("C:\\Users\\ibnee\\AppData\\Local\\Programs\\Android Studio 2")
    //version.set("2021.3.3")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf("android",/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    patchPluginXml {
        sinceBuild.set("213")
        untilBuild.set("223.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }


}

dependencies{
    implementation(files("C:\\opencv\\build\\java\\opencv-480.jar"))
    implementation(files("C:\\opencv\\build\\java\\x64\\opencv_java480.dll"))
    implementation("nz.ac.waikato.cms.weka:weka-dev:3.9.3")
}



