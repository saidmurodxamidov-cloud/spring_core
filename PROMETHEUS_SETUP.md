# Prometheus Setup Guide

This guide explains how to set up and configure Prometheus for monitoring the Spring Boot application.

## Prerequisites

- Docker and Docker Compose installed
- Spring Boot application running on port 8080
- Actuator and Prometheus dependencies already included in `pom.xml`

## Quick Start

1. **Start Prometheus with Docker Compose:**
   ```bash
   docker-compose up -d prometheus
   ```

2. **Access Prometheus UI:**
   - Open http://localhost:9090 in your browser
   - The Prometheus UI allows you to query metrics and view dashboards

3. **Verify Metrics Collection:**
   - In Prometheus UI, go to Status → Targets
   - Check that `spring-boot-app` target is UP
   - Query metrics using PromQL, e.g., `jvm_memory_used_bytes`

## Configuration

### Prometheus Configuration (`prometheus.yml`)

The `prometheus.yml` file is already configured to:
- Scrape metrics from the Spring Boot application at `/actuator/prometheus`
- Use `host.docker.internal:8080` to access the host machine from Docker
- Set scrape interval to 30 seconds

### Basic Authentication (Optional)

To enable basic authentication for the Prometheus endpoint:

1. **Enable in `application.yml`:**
   ```yaml
   prometheus:
     security:
       enabled: true
       username: prometheus
       password: your_secure_password
   ```

2. **Update `prometheus.yml`:**
   Uncomment and configure the `basic_auth` section:
   ```yaml
   - job_name: 'spring-boot-app'
     # ... other config ...
     basic_auth:
       username: 'prometheus'
       password: 'your_secure_password'
   ```

3. **Restart the application** to apply security changes

4. **Restart Prometheus:**
   ```bash
   docker-compose restart prometheus
   ```

## Health Indicator

The application uses a custom `HealthIndicator` (not a controller) that:
- Checks database connectivity
- Provides environment information
- Integrates with Spring Boot Actuator

Access health status at: `http://localhost:8080/actuator/health`

## Available Endpoints

- **Health:** `http://localhost:8080/actuator/health`
- **Info:** `http://localhost:8080/actuator/info`
- **Prometheus Metrics:** `http://localhost:8080/actuator/prometheus`

## Troubleshooting

### Prometheus can't scrape metrics - Connection Refused Error

**Error:** `dial tcp 192.168.65.254:8080: connect: connection refused`

This usually means Prometheus can't reach your application. Try these solutions:

#### Solution 1: Verify Application is Running (Most Common)
1. **Check if the application is running:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```
   Or open in browser: http://localhost:8080/actuator/health

2. **Verify the Prometheus endpoint is accessible:**
   ```bash
   curl http://localhost:8080/actuator/prometheus
   ```

#### Solution 2: Use extra_hosts (Already Applied)
The `docker-compose.yml` now includes `extra_hosts` to ensure `host.docker.internal` resolves correctly on Windows. Restart Prometheus:
```bash
docker-compose restart prometheus
```

#### Solution 3: Use Your Windows Host IP Address
If `host.docker.internal` still doesn't work:

1. **Find your Windows host IP:**
   ```powershell
   ipconfig
   ```
   Look for "IPv4 Address" (usually something like `192.168.1.100`)

2. **Update `prometheus.yml`:**
   Replace `host.docker.internal:8080` with your actual IP:
   ```yaml
   static_configs:
     - targets: ['192.168.1.100:8080']  # Replace with your IP
   ```

3. **Restart Prometheus:**
   ```bash
   docker-compose restart prometheus
   ```

#### Solution 4: Check Docker Network
1. **Check Prometheus logs:**
   ```bash
   docker-compose logs prometheus
   ```

2. **Verify target status in Prometheus UI:**
   - Go to http://localhost:9090
   - Navigate to Status → Targets
   - Check for error messages

3. **Test connectivity from Prometheus container:**
   ```bash
   docker exec -it local-prometheus wget -O- http://host.docker.internal:8080/actuator/health
   ```

#### Solution 5: Firewall Issues (Windows)
If using your host IP, ensure Windows Firewall allows connections on port 8080:
```powershell
# Allow inbound connections on port 8080
New-NetFirewallRule -DisplayName "Spring Boot App" -Direction Inbound -LocalPort 8080 -Protocol TCP -Action Allow
```

### Basic Auth Issues

- Ensure credentials match in both `application.yml` and `prometheus.yml`
- Check that `prometheus.security.enabled=true` is set
- Verify the user has `PROMETHEUS` role in Spring Security

## Example PromQL Queries

- **JVM Memory Used:** `jvm_memory_used_bytes`
- **HTTP Requests:** `http_server_requests_seconds_count`
- **Database Connections:** `hikari_connections_active`
- **Application Uptime:** `process_uptime_seconds`

## Additional Resources

- [Prometheus Documentation](https://prometheus.io/docs/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer Prometheus](https://micrometer.io/docs/registry/prometheus)

