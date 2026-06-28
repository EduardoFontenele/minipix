description = "Consome e valida transações Pix do Kafka"

dependencies {
    implementation(project(":pix-common"))
    implementation("org.springframework.kafka:spring-kafka")
}
