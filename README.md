# mini-pix

A Kafka laboratory that simulates the PIX payment system following BACEN and ISO 20022 specifications.

## Modules

| Module | Responsibility |
|---|---|
| `pix-common` | Shared DTOs and enums across modules |
| `pix-api` | HTTP layer — controllers, facades, clients and services |
| `pix-producer` | Publishes Pix transactions to Kafka |
| `pix-validator` | Consumes and validates transactions from Kafka |
| `pix-notificador` | Sends notifications about transactions |
| `pix-auditor` | Records and audits all transactions |
| `pix-app` | Main application — aggregates all modules |

## Endpoints

### `POST /pix/transactions/resolve`

Pre-confirmation step. Receives a Pix key and returns the receiver's data for the payer to review before confirming the transfer.

Queries in parallel via virtual threads (200ms timeout per BACEN SLA):
- **DICT** (BACEN) — resolves the key to account and receiver data
- **Anti-fraud** — checks whether the key and payer are flagged
- **Limits** — checks whether the payer has sufficient limit for the amount

### `POST /pix/transactions/init`

Initiates the transaction after payer confirmation. Checks idempotency via Redis, re-resolves the receiver via DICT (never trusts client-supplied receiver data), builds an Avro `PixTransaction` message and publishes it to the `pix.transactions` Kafka topic (fire-and-forget — the HTTP response does not wait for the Kafka ack). On successful publish, the transaction is persisted to Postgres. Returns `202 Accepted` — outcome is delivered via notification.

## Running locally

### Prerequisites

- Java 25
- Redis running on `localhost:6379`
- Kafka broker running on `localhost:9092`
- Confluent Schema Registry running on `localhost:8082`
- Postgres running on `localhost:5433`
- WireMock installed via Homebrew (`brew install wiremock`)

### Start Kafka

Installed via Homebrew and run as a service:

```bash
brew services start kafka
```

Create the topic once (3 partitions):

```bash
kafka-topics --bootstrap-server localhost:9092 --create --topic pix.transactions --partitions 3
```

### Start the Schema Registry

```bash
docker run -d --name schema-registry \
  -e SCHEMA_REGISTRY_HOST_NAME=schema-registry \
  -e SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  -e SCHEMA_REGISTRY_LISTENERS=http://0.0.0.0:8082 \
  -p 8082:8082 \
  confluentinc/cp-schema-registry
```

### Start Postgres

```bash
docker run -d --name minipix-postgres \
  -e POSTGRES_DB=minipix \
  -e POSTGRES_USER=minipix \
  -e POSTGRES_PASSWORD=minipix \
  -p 5433:5432 \
  postgres:16-alpine
```

The `pix_transactions` table is created automatically on startup (`ddl-auto: update`).

### Start WireMock (mock of DICT, anti-fraud, and limits services)

```bash
wiremock --root-dir ~/wiremock --port 9090
```

### Start the application

```bash
./gradlew :pix-app:bootRun
```

### Inspect published messages

Console consumer aware of the Avro wire format (schema ID + Schema Registry lookup). Run via the Schema Registry container, since it already ships the Confluent CLI tools:

```bash
docker exec -it schema-registry kafka-avro-console-consumer \
  --bootstrap-server host.docker.internal:9092 \
  --topic pix.transactions \
  --group pix-cli-watcher \
  --property schema.registry.url=http://localhost:8082 \
  --property print.key=true \
  --key-deserializer org.apache.kafka.common.serialization.StringDeserializer \
  --skip-message-on-error
```

The `--key-deserializer` override is required — the message key is a plain string (`endToEndId`), not Avro-encoded, and the tool otherwise tries to Avro-decode it too.

### Query persisted transactions

```bash
docker exec minipix-postgres psql -U minipix -d minipix -c 'SELECT * FROM pix_transactions;'
```

### Swagger UI

Available at `http://localhost:8080/swagger-ui/index.html` after the app starts.

## Test data

### JWT

Secret: `minipix-secret-key-for-local-dev`  
Algorithm: `HS256`  
Claim `sub`: payer's CPF (`52998224725`)

```
Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1Mjk5ODIyNDcyNSIsIm5hbWUiOiJFZHVhcmRvIEZvbnRlbmVsZSIsImlhdCI6MTUxNjIzOTAyMn0.ZEY0hTMxUHyjLhTet_sld-64t-mIET4brsfbDLu522o
```

### `POST /pix/transactions/resolve`

**Headers**

| Header | Value |
|---|---|
| `Authorization` | `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1Mjk5ODIyNDcyNSIsIm5hbWUiOiJFZHVhcmRvIEZvbnRlbmVsZSIsImlhdCI6MTUxNjIzOTAyMn0.ZEY0hTMxUHyjLhTet_sld-64t-mIET4brsfbDLu522o` |
| `correlation-id` | `f47ac10b-58cc-4372-a567-0e02b2c3d479` |
| `idempotency-key` | `3d3b5a1e-1c2d-4e5f-8a9b-0c1d2e3f4a5b` |
| `device-id` | `device-test-001` |
| `forwarded-for` | `189.120.45.32` |

**Body**

```json
{
  "receiverKey": "joao.silva@gmail.com",
  "amount": 250.00
}
```

### `POST /pix/transactions/init`

**Headers**

| Header | Value |
|---|---|
| `Authorization` | `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1Mjk5ODIyNDcyNSIsIm5hbWUiOiJFZHVhcmRvIEZvbnRlbmVsZSIsImlhdCI6MTUxNjIzOTAyMn0.ZEY0hTMxUHyjLhTet_sld-64t-mIET4brsfbDLu522o` |
| `idempotency-key` | `7a1b2c3d-4e5f-6789-abcd-ef0123456789` |

**Body**

```json
{
  "receiverKey": "joao.silva@gmail.com",
  "amount": 250.00,
  "description": "Reembolso do almoço"
}
```
