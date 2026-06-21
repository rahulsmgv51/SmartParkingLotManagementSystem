# Parking Lot Management System

## 1. Project Overview

A Parking Lot Management System that handles:

- Vehicle entry and exit
- Spot allocation
- Fee calculation
- Real-time dashboard metrics
- High-performance APIs
- Caching, messaging, and monitoring

This project is designed as a microservice-ready platform.

## 2. Core Tech Stack

### Spring Boot

Why:

- Rapid microservice development
- Built-in REST API support
- Production-ready ecosystem
- Easy integration with Redis, Kafka, and Prometheus

### Maven

Why:

- Dependency management
- Standard Java build lifecycle
- Easy CI/CD integration

### PostgreSQL

Why:

- Strong consistency for parking allocation
- ACID transactions to prevent double bookings
- Good support for reporting queries

### Redis

Why:

- Fast data lookup
- Reduces database load
- Used for active tickets and dashboard counters

### Apache Kafka

Why:

- Event-driven architecture
- Decouples system components
- Enables analytics and streaming pipelines

### Prometheus & Grafana

Why:

- Prometheus collects system and application metrics
- Grafana provides dashboards and alerts

### SpringDoc OpenAPI

Why:

- Auto-generates Swagger documentation
- Helps frontend and QA teams
- Keeps API docs in sync with code

### Docker / Podman (optional)

Why:

- Consistent deployment environment
- Easier local development
- Good fit for microservices

## 3. Project Setup

### Step 1: Create the Project

Use Spring Initializr, then include:

- Spring Web
- Spring Data JPA
- PostgreSQL Driver
- Spring Data Redis
- Spring Kafka
- Spring Boot Actuator
- Lombok
- Validation

### Step 2: Recommended Project Structure

```text
parking-lot/
├── controller/        # REST controllers
├── service/           # Business logic
├── repository/        # Persistence layer
├── entity/            # JPA entities
├── dto/               # Request / response objects
├── config/            # Kafka, Redis, tracing, actuator config
├── exception/         # Global exception handling
├── util/              # Helpers and constants
└── ParkingLotApplication.java
```

Why this structure:

- Separation of concerns
- Easier scaling into microservices
- Clear boundaries for each responsibility

## 4. Core Business Components

### Vehicle Entry Flow

1. API receives entry request
2. Check Redis / ticket state
3. If vehicle is not already parked:
   - assign a spot
   - persist ticket and spot state
   - publish Kafka event
   - update Redis counters

### Vehicle Exit Flow

1. Fetch active ticket
2. Calculate parking fee
3. Release the parking spot
4. Update ticket status and DB
5. Publish Kafka event
6. Update Redis counters

### Pricing Engine

- Hourly pricing strategy
- Optional first 15 minutes free
- Different rates for CAR, BIKE, TRUCK

This uses the Strategy Pattern.

## 5. Microservice-Ready Design

Even if the app runs as a monolith initially, the architecture supports splitting into:

- Entry Service
- Exit Service
- Pricing Service
- Analytics Service

Benefits:

- Better scalability
- Independent deployments
- Easier debugging

## 6. Observability Setup

### Spring Boot Actuator

Enable the following endpoints:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

### Prometheus Metrics

Track:

- API latency
- JVM memory usage
- CPU usage
- Custom metrics for entry and exit counts

### Grafana Dashboards

Useful panels:

| Metric                                   | Purpose                |
|------------------------------------------|------------------------|
| `vehicle_entry_total`                    | Traffic monitoring     |
| `vehicle_exit_total`                     | Exit flow tracking     |
| `jvm_memory_used_bytes`                  | Memory health          |
| `system_cpu_usage`                       | Load tracking          |
| `http_server_requests_seconds_count`     | API performance        |

## 7. Redis Usage Design

Redis stores:

- active vehicles
- ticket cache
- spot availability counters

Why Redis:

- avoids DB hits on every entry request
- improves response time
- supports concurrent load spikes

## 8. Kafka Event Design

### VehicleEntryEvent

```json
{
  "vehicleNumber": "UP32AB1234",
  "entryTime": "2026-06-15T12:00:00",
  "slotId": "A12"
}
```

### VehicleExitEvent

```json
{
  "vehicleNumber": "UP32AB1234",
  "duration": "2h 30m",
  "fee": 120.0
}
```

Why Kafka:

- decouples event producers and consumers
- supports analytics and streaming workflows
- enables asynchronous scaling

## 9. Key Design Patterns

- Strategy Pattern → pricing logic
- Factory Pattern → vehicle type instantiation
- Singleton → cache helper management
- Observer Pattern → event-driven Kafka workflow

## 10. Local Development Setup

Run these services locally:

```bash
docker run -d -p 5432:5432 postgres
docker run -d -p 6379:6379 redis
# Start Kafka if needed
docker-compose up -d
```

## 11. Example application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/parkinglot
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.redis.host=localhost
spring.redis.port=6379

spring.kafka.bootstrap-servers=localhost:9092

management.endpoints.web.exposure.include=health,info,metrics,prometheus
```

## 12. System Flow Summary

```text
Client -> Controller -> Service -> Redis / Database -> Kafka Event -> Analytics
```
