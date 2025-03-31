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
**POST** `/accounts/transfer`

* implicit credit + create = omit "accountId" but provide "owner" name

* plain credit = provide accountId and omit owner

#### Request Body
```json
{
  "transactionId": "49f43fc6-84d8-4d8c-a05e-ced317342006",
  "accountId": 1,
  "owner": null,
  "amount": 100.00,
  "currency": "USD",
  "type": "DEBIT"
}
```

#### Example cURL Request
```sh
curl -X POST "http://localhost:8084/accounts/transfer" \
     -H "Content-Type: application/json" \
     -d '{"transactionId": "UUID-example", "accountId": 1, "amount": 100.00, "currency": "USD", "type": "DEBIT"}'
```

#### example postman collection is in src/test/resources/postman
remember to change/increment transactionId to reflect new requests especially if Redis 
already saved the values, otherwise it will be treated as already processed(a retry)

#### Access the H2 Console
Open http://localhost:8084/h2-console in a browser.

JDBC URL: Enter jdbc:h2:mem:testdb
(It must match your spring.datasource.url).

Username: sa
Password: (leave it empty unless you set one)

Click Connect.

#### Redis CLI
- check Redis container: docker ps then
- docker exec -it <redis_container_name> redis-cli
- use keys * to list all keys
- get <key>
- exists <key>
- and other commands

## Notes
- If running the Spring Boot application locally (without Docker), ensure `spring.redis.host=localhost` is set in `application.properties`.
- When using the app inside docker, set `spring.redis.host=redis` in `application.properties`.
- RabbitMQ Management UI is available at [http://localhost:15672](http://localhost:15672) with default credentials (`guest`/`guest`).


