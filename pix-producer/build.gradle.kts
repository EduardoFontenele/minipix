description = "Envia transações Pix para o Kafka"

dependencies {
    implementation(project(":pix-common"))
    implementation("org.springframework.kafka:spring-kafka")
}
