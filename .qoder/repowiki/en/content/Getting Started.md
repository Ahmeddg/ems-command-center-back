# Getting Started

<cite>
**Referenced Files in This Document**
- [pom.xml](file://pom.xml)
- [Dockerfile](file://Dockerfile)
- [docker-compose.yml](file://docker-compose.yml)
- [EmsCommandCenterApplication.java](file://src/main/java/com/example/ems_command_center/EmsCommandCenterApplication.java)
- [application.yml](file://src/main/resources/application.yml)
- [SecurityConfig.java](file://src/main/java/com/example/ems_command_center/config/SecurityConfig.java)
- [KeycloakJwtAuthenticationConverter.java](file://src/main/java/com/example/ems_command_center/config/KeycloakJwtAuthenticationConverter.java)
- [WebSocketConfig.java](file://src/main/java/com/example/ems_command_center/config/WebSocketConfig.java)
- [DataSeeder.java](file://src/main/java/com/example/ems_command_center/seeder/DataSeeder.java)
- [IncidentController.java](file://src/main/java/com/example/ems_command_center/controller/IncidentController.java)
- [UserController.java](file://src/main/java/com/example/ems_command_center/controller/UserController.java)
- [FacilityController.java](file://src/main/java/com/example/ems_command_center/controller/FacilityController.java)
- [rebuild-and-start.ps1](file://rebuild-and-start.ps1)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Quick Start](#quick-start)
4. [Local Development Setup](#local-development-setup)
5. [Environment Variables and Properties](#environment-variables-and-properties)
6. [Initial Data Seeding](#initial-data-seeding)
7. [Verification Checklist](#verification-checklist)
8. [API Access and WebSocket Connections](#api-access-and-websocket-connections)
9. [Troubleshooting Guide](#troubleshooting-guide)
10. [Conclusion](#conclusion)

## Introduction
This guide helps you install and run the EMS Command Center backend locally for development. It covers prerequisites, environment setup, database and identity provider configuration, property configuration, initial data seeding, verification steps, and quick API/WebSocket examples.

## Prerequisites
- Java 21 SDK installed and configured in your PATH.
- Apache Maven installed and configured in your PATH.
- Docker Desktop installed and running.
- Basic understanding of MongoDB and Keycloak concepts (realms, clients, roles, JWT).

## Quick Start
Follow these steps to get the backend running quickly with Docker Compose:
1. Build the Spring Boot application JAR:
   - Run: mvn clean package -DskipTests
2. Start the Docker Compose stack:
   - Run: docker-compose up -d
3. Verify services:
   - Backend: http://localhost:8081
   - Swagger UI: http://localhost:8081/swagger-ui.html
   - Mongo Express: http://localhost:8082
4. Seed initial data:
   - The application seeds data automatically when the database is empty.

**Section sources**
- [pom.xml:16-21](file://pom.xml#L16-L21)
- [docker-compose.yml:38-63](file://docker-compose.yml#L38-L63)
- [rebuild-and-start.ps1:8-44](file://rebuild-and-start.ps1#L8-L44)

## Local Development Setup
### Option A: Run with Docker Compose (Recommended)
- Uses a managed MongoDB container, optional Mongo Express UI, and builds the backend image from your current code.
- Ports exposed:
  - Backend: 8081
  - Mongo Express: 8082
- Environment variables are passed via docker-compose.yml for Keycloak and MongoDB.

Steps:
1. Ensure Docker is running.
2. From the project root, run:
   - docker-compose up -d
3. Wait for health checks to pass (compose defines health checks for MongoDB and backend).

**Section sources**
- [docker-compose.yml:1-73](file://docker-compose.yml#L1-L73)
- [Dockerfile:1-7](file://Dockerfile#L1-L7)

### Option B: Run Locally with External Services
- Run MongoDB locally or remotely and expose it via a URI.
- Run Keycloak locally or remotely and expose its JWK set endpoint.
- Start the Spring Boot app with defaults or override environment variables.

Steps:
1. Export environment variables for MongoDB URI and Keycloak JWK set URI.
2. Start the application:
   - java -jar target/<your-built-jar>
3. Confirm the app binds to port 8081.

**Section sources**
- [application.yml:5-17](file://src/main/resources/application.yml#L5-L17)
- [EmsCommandCenterApplication.java:1-14](file://src/main/java/com/example/ems_command_center/EmsCommandCenterApplication.java#L1-L14)

## Environment Variables and Properties
### Environment Variables
Set these in your shell or Docker Compose environment block:
- SPRING_DATA_MONGODB_URI: MongoDB connection string (default included).
- SERVER_PORT: Backend server port (default 8081).
- KEYCLOAK_JWK_SET_URI: Keycloak JWK set endpoint (default included).
- KEYCLOAK_CLIENT_ID: Resource server client ID (default included).
- KEYCLOAK_PRINCIPAL_CLAIM: Claim used as the principal name (default preferred_username).
- app.seed.enabled: Enable/disable automatic data seeding (default enabled).

Notes:
- The compose file passes KEYCLOAK_JWK_SET_URI and KEYCLOAK_CLIENT_ID as environment variables to the backend container.
- The backend reads these values from application.yml.

**Section sources**
- [application.yml:7-17](file://src/main/resources/application.yml#L7-L17)
- [application.yml:31-35](file://src/main/resources/application.yml#L31-L35)
- [docker-compose.yml:48-55](file://docker-compose.yml#L48-L55)

### Application Properties
- application.yml defines:
  - MongoDB connection and database name.
  - OAuth2/JWT configuration pointing to Keycloak.
  - Swagger/OpenAPI paths.
  - Logging levels.
  - Custom app.security.keycloak.* properties.

**Section sources**
- [application.yml:1-36](file://src/main/resources/application.yml#L1-L36)

## Initial Data Seeding
The application seeds the database automatically when collections are empty. This includes:
- Incidents
- Facilities (general and hospital-specific)
- Vehicles
- Reports
- Users (including ADMIN, DRIVER, MANAGER, USER)
- Analytics (dispatch volume and response time)
- Hospital manager data (patients, beds, resources, staff)

Behavior:
- Seeding runs only if the relevant collections are empty.
- Controlled by the app.seed.enabled property (enabled by default).

**Section sources**
- [DataSeeder.java:17-18](file://src/main/java/com/example/ems_command_center/seeder/DataSeeder.java#L17-L18)
- [DataSeeder.java:57-67](file://src/main/java/com/example/ems_command_center/seeder/DataSeeder.java#L57-L67)

## Verification Checklist
After starting the services:
1. Backend health:
   - Health check endpoint: http://localhost:8081/api-docs
   - Expected: 200 OK
2. Swagger UI:
   - URL: http://localhost:8081/swagger-ui.html
   - Expected: Swagger UI page
3. MongoDB:
   - Container: ems-mongodb
   - Port: 27017
   - Optional UI: http://localhost:8082
4. Mongo Express:
   - Username: admin
   - Password: admin123
5. Application startup:
   - Logs indicate successful startup and seeding completion.

**Section sources**
- [docker-compose.yml:56-61](file://docker-compose.yml#L56-L61)
- [DataSeeder.java:66](file://src/main/java/com/example/ems_command_center/seeder/DataSeeder.java#L66)

## API Access and WebSocket Connections
### REST API
- Base Path: /api
- Protected by OAuth2 JWT from Keycloak.
- Example endpoints:
  - GET /api/incidents (requires authenticated user)
  - GET /api/facilities (requires authenticated user)
  - GET /api/users/me (current user profile)
  - Requires appropriate roles per endpoint.

Swagger/OpenAPI:
- Docs: /api-docs
- UI: /swagger-ui.html

**Section sources**
- [IncidentController.java:25-30](file://src/main/java/com/example/ems_command_center/controller/IncidentController.java#L25-L30)
- [FacilityController.java:24-29](file://src/main/java/com/example/ems_command_center/controller/FacilityController.java#L24-L29)
- [UserController.java:72-80](file://src/main/java/com/example/ems_command_center/controller/UserController.java#L72-L80)
- [application.yml:19-24](file://src/main/resources/application.yml#L19-L24)

### WebSocket
- STOMP endpoints:
  - /ws (SockJS-enabled)
  - /ws-native (Raw WebSocket)
- Allowed origins:
  - http://localhost:5173
  - http://localhost:4173
  - http://localhost:3000
  - http://localhost:4200
- Message Broker:
  - Simple broker for destinations under /topic
  - Application destination prefixes under /app

**Section sources**
- [WebSocketConfig.java:20-29](file://src/main/java/com/example/ems_command_center/config/WebSocketConfig.java#L20-L29)
- [WebSocketConfig.java:31-49](file://src/main/java/com/example/ems_command_center/config/WebSocketConfig.java#L31-L49)

## Troubleshooting Guide
Common issues and resolutions:
- Port conflicts:
  - Change SERVER_PORT if 8081 is in use.
  - Adjust docker-compose ports mapping if needed.
- MongoDB connectivity:
  - Verify SPRING_DATA_MONGODB_URI points to a reachable MongoDB instance.
  - Confirm the database name matches ems_db.
- Keycloak connectivity:
  - Ensure KEYCLOAK_JWK_SET_URI is reachable from the backend container.
  - Confirm the client ID matches the resource server client configured in Keycloak.
- CORS errors:
  - Frontend origin must match allowed origins in SecurityConfig and WebSocketConfig.
- Health checks failing:
  - Wait for MongoDB to become healthy; backend health check depends on MongoDB readiness.
- No initial data:
  - Ensure app.seed.enabled is true and the database is empty.

**Section sources**
- [application.yml:7-17](file://src/main/resources/application.yml#L7-L17)
- [SecurityConfig.java:106-120](file://src/main/java/com/example/ems_command_center/config/SecurityConfig.java#L106-L120)
- [WebSocketConfig.java:32-48](file://src/main/java/com/example/ems_command_center/config/WebSocketConfig.java#L32-L48)
- [docker-compose.yml:12-16](file://docker-compose.yml#L12-L16)
- [docker-compose.yml:56-61](file://docker-compose.yml#L56-L61)

## Conclusion
You now have the backend running locally using Docker Compose, with MongoDB and optional Mongo Express, protected by Keycloak via OAuth2/JWT. The application auto-seeds initial data, and you can access REST APIs via Swagger UI and WebSocket endpoints for real-time updates.