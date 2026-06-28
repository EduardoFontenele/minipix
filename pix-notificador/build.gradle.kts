description = "Envia notificações sobre transações Pix"

dependencies {
    implementation(project(":pix-common"))
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter")

}
