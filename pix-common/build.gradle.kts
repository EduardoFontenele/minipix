plugins {
    `java-library`
    id("com.github.davidmc24.gradle.plugin.avro")
}

description = "Modelos e DTOs compartilhados entre os módulos do mini-pix"

dependencies {
    api("org.apache.avro:avro:1.11.4")
}
