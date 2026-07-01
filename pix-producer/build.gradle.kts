description = "Envia transações Pix para o Kafka"

dependencies {
    implementation(project(":pix-common"))
    implementation("org.springframework.boot:spring-boot-starter-kafka")
    implementation("io.confluent:kafka-avro-serializer:8.3.0")
}
