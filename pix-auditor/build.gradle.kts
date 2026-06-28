description = "Registra e audita todas as transações Pix"

dependencies {
    implementation(project(":pix-common"))
    implementation("org.springframework.kafka:spring-kafka")
}
