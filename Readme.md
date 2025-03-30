# Bookkeeping Wallet Application

## Prerequisites
Ensure you have the following installed on your system:
- Docker and Docker Compose
- Java 17
- Maven

## Setup and Run

### 1. Start Redis and RabbitMQ using Docker Compose
Run the following command in the project root where `docker-compose.yml` is located:
```sh
docker compose up -d
```
This will start Redis and RabbitMQ as required dependencies.

### 2. Run the Spring Boot Application
Use Maven to build and start the application:
```sh
mvn spring-boot:run
```
The application will start on port `8084`.

## API Endpoints

### Get Account Balance
**GET** `/accounts/{id}/balance`

#### Example cURL Request
```sh
curl -X GET "http://localhost:8084/accounts/1/balance?currency=USD" -H "Content-Type: application/json"
```

### Transfer Funds
**POST** `/accounts/{id}/transfer`

#### Request Body
```json
{
  "accountId": 1,
  "amount": 100.00,
  "currency": "USD",
  "type": "DEBIT"
}
```

#### Example cURL Request
```sh
curl -X POST "http://localhost:8084/accounts/1/transfer" \
     -H "Content-Type: application/json" \
     -d '{"accountId": 1, "amount": 100.00, "currency": "USD", "type": "DEBIT"}'
```

## Notes
- If running the Spring Boot application locally (without Docker), ensure `spring.redis.host=localhost` is set in `application.properties`.
- When using the app inside docker, set `spring.redis.host=redis` in `application.properties`.
- RabbitMQ Management UI is available at [http://localhost:15672](http://localhost:15672) with default credentials (`guest`/`guest`).

