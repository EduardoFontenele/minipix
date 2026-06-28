plugins {
    id("org.springframework.boot")
}

description = "Aplicação principal — agrega todos os módulos do mini-pix"

dependencies {
    implementation(project(":pix-api"))
    implementation(project(":pix-producer"))
    implementation(project(":pix-validator"))
    implementation(project(":pix-notificador"))
    implementation(project(":pix-auditor"))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.kafka:spring-kafka")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}
