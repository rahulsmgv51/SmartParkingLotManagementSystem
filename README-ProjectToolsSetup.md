# Parking Lot Tooling and Infrastructure Setup

## 1. High-Level Architecture

- Spring Boot application
- PostgreSQL for persistent parking data
- Redis for cache and counter storage
- Kafka for event streaming
- Prometheus for metrics collection
- Grafana for visualization

## 2. Podman Setup

Install Podman:

```bash
sudo apt update
sudo apt install -y podman
```

Verify installation:

```bash
podman --version
```

Why use Podman?

- Daemonless and more secure
- Rootless container execution
- Kubernetes compatible
- Lightweight for local development

## 3. PostgreSQL Setup

Run PostgreSQL:

```bash
podman run -d \
  --name postgres-parking \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=parkinglot \
  -p 5432:5432 \
  postgres:15
```

Verify the container:

```bash
podman ps
```

Spring Boot configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/parkinglot
spring.datasource.username=postgres
spring.datasource.password=postgres
```

Why PostgreSQL?

- ACID transactions prevent double slot booking
- Strong consistency for ticketing
- Relational model fits vehicles, tickets, and parking spots

## 4. Redis Setup

Run Redis:

```bash
podman run -d \
  --name redis-cache \
  -p 6379:6379 \
  redis:7
```

Spring Boot configuration:

```properties
spring.redis.host=localhost
spring.redis.port=6379
```

Why Redis?

- Ultra-fast access
- Reduces database load
- Ideal for active vehicle tracking, slot counters, and session cache

Recommended Redis key design:

- `vehicle:{number}` → active ticket reference
- `slot:available` → list or counter of free slots
- `parking:entry_count` → total entries

## 5. Kafka Setup

Kafka requires Zookeeper and a Kafka broker.

Create a network for the containers:

```bash
podman network create parking-net
```

Run Zookeeper:

```bash
podman run -d \
  --name zookeeper \
  --network parking-net \
  -e ALLOW_ANONYMOUS_LOGIN=yes \
  bitnami/zookeeper:latest
```

Run Kafka:

```bash
podman run -d \
  --name kafka \
  --network parking-net \
  -p 9092:9092 \
  -e KAFKA_BROKER_ID=1 \
  -e KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e ALLOW_PLAINTEXT_LISTENER=yes \
  -e KAFKA_CFG_LISTENERS=PLAINTEXT://:9092 \
  -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  bitnami/kafka:latest
```

Spring Boot configuration:

```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=parking-group
```

Example Kafka topics:

- `parking-entry-topic`
- `parking-exit-topic`
- `parking-payment-topic`

Why Kafka?

- Event-driven architecture
- Decouples system components
- Handles bursts in parking entry and exit traffic
- Supports analytics and streaming pipelines

## 6. Prometheus Setup

Create `prometheus.yml`:

```yaml
global:
  scrape_interval: 5s

scrape_configs:
  - job_name: 'parking-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['host.containers.internal:8080']
```

Run Prometheus:

```bash
podman run -d \
  --name prometheus \
  -p 9090:9090 \
  -v $PWD/prometheus.yml:/etc/prometheus/prometheus.yml:Z \
  prom/prometheus
```

Spring Boot configuration:

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
```

Why Prometheus?

- Scrapes Spring Boot actuator metrics
- Stores time-series performance data
- Monitors API latency, JVM memory, and request counts
- Enables alerting and dashboard metrics

## 7. Grafana Setup

Run Grafana:

```bash
podman run -d \
  --name grafana \
  -p 3000:3000 \
  grafana/grafana
```

Open Grafana:

- URL: `http://localhost:3000`
- User: `admin`
- Password: `admin`

Add Prometheus as a data source using:

- URL: `http://prometheus:9090`

Why Grafana?

- Visual dashboards
- Real-time system monitoring
- Alerting support

Example dashboard metrics:

- `parking_vehicle_entry_total`
- `parking_vehicle_exit_total`
- `jvm_memory_used_bytes`
- `system_cpu_usage`
- `http_server_requests_seconds_count`

## 8. Container Networking Tip

When Spring Boot runs in a container, use this host address from inside containers:

- `host.containers.internal`

Example:

```properties
spring.redis.host=host.containers.internal
spring.datasource.url=jdbc:postgresql://host.containers.internal:5432/parkinglot
```

## 9. Tooling Summary

| Tool       | Role                   |
|-----------|------------------------|
| PostgreSQL | Source of truth       |
| Redis      | Speed layer           |
| Kafka      | Event backbone        |
| Prometheus | Metrics engine        |
| Grafana    | Visualization         |
| Podman     | Container runtime     |

## 10. Full System Flow

```text
Vehicle Entry API
      ↓
Redis Check (fast validation)
      ↓
PostgreSQL (store ticket)
      ↓
Kafka Event (async processing)
      ↓
Prometheus (metrics recorded)
      ↓
Grafana (dashboard view)
```

## 11. One-Command Startup Script

Save this as `start-infrastructure.sh`:

```bash
#!/bin/bash

podman start postgres-parking
podman start redis-cache
podman start zookeeper
podman start kafka
podman start prometheus
podman start grafana
```

Run the script:

```bash
bash start-infrastructure.sh
```
