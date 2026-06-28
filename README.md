# mini-pix

A Kafka laboratory that simulates the PIX payment system following BACEN and ISO 20022 specifications.

## Modules

| Module | Responsibility |
|---|---|
| `pix-common` | Shared DTOs and enums across modules |
| `pix-api` | HTTP layer — controllers, facade, clients and services |
| `pix-producer` | Publishes Pix transactions to Kafka |
| `pix-validator` | Consumes and validates transactions from Kafka |
| `pix-notificador` | Sends notifications about transactions |
| `pix-auditor` | Records and audits all transactions |
| `pix-app` | Main application — aggregates all modules |

## Endpoints

### `GET /pix/transactions/resolve`

Pre-confirmation step. Receives a Pix key and returns the receiver's data for the payer to review before confirming the transfer.

Queries in parallel:
- **DICT** (BACEN) — resolves the key to account and receiver data
- **Anti-fraud** — checks whether the key and payer are flagged
- **Limits** — checks whether the payer has sufficient limit for the amount

### `POST /pix/transactions/init`

Initiates the transaction after payer confirmation. Publishes to Kafka for processing by the remaining modules.

## Running locally

### Prerequisites

- Java 25
- WireMock installed via Homebrew

### Start WireMock (mock of external services)

```bash
wiremock --root-dir ~/wiremock --port 9090
```

### Start the application

```bash
./gradlew :pix-app:bootRun
```

## Test data

### JWT

Secret: `minipix-secret-key-for-local-dev`  
Algorithm: `HS256`  
Claim `sub`: payer's CPF (`52998224725`)

```
Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1Mjk5ODIyNDcyNSIsIm5hbWUiOiJFZHVhcmRvIEZvbnRlbmVsZSIsImlhdCI6MTUxNjIzOTAyMn0.ZEY0hTMxUHyjLhTet_sld-64t-mIET4brsfbDLu522o
```

### Headers for `GET /pix/transactions/resolve`

| Header | Value |
|---|---|
| `Authorization` | `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1Mjk5ODIyNDcyNSIsIm5hbWUiOiJFZHVhcmRvIEZvbnRlbmVsZSIsImlhdCI6MTUxNjIzOTAyMn0.ZEY0hTMxUHyjLhTet_sld-64t-mIET4brsfbDLu522o` |
| `correlation-id` | `f47ac10b-58cc-4372-a567-0e02b2c3d479` |
| `idempotency-key` | `3d3b5a1e-1c2d-4e5f-8a9b-0c1d2e3f4a5b` |
| `device-id` | `device-test-001` |
| `forwarded-for` | `189.120.45.32` |

### Request body for `GET /pix/transactions/resolve`

```json
{
  "receiverKey": "joao.silva@gmail.com",
  "amount": 250.00
}
```
