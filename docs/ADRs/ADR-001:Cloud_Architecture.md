# ADR-001: Cloud Architecture

#### **Status: Accepted**

## Context

The application is designed as a microservices-based system. Multiple backend services need to be accessed through a common entry point, discovered dynamically, and configured consistently across environments.

## Decision

Use Spring Cloud infrastructure to provide the core cloud architecture:

- API Gateway as the single entry point for external client requests.
- Eureka Server for service discovery.
- Config Server for centralized configuration management.
- PostgreSQL for persistent application data.

## Architecture
```text
                         ┌──────────────┐
                         │    Client    │
                         └──────┬───────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   API Gateway   │
                       └────────┬────────┘
                                │
             ┌──────────────────┼──────────────────┐
             ▼                  ▼                  ▼
       ┌───────────┐      ┌───────────┐      ┌───────────┐
       │ Identity  │      │ Transform │      │  Reports  │
       │  Service  │      │  Service  │      │  Service  │
       └─────┬─────┘      └─────┬─────┘      └─────┬─────┘
             │                  │                  │
             └──────────────────┼──────────────────┘
                                │
                    ┌───────────┴──────────┐
                    │                      │
              ┌─────▼─────┐         ┌──────▼──────┐
              │   Eureka  │         │   Config    │
              │   Server  │         │   Server    │
              └───────────┘         └─────────────┘

                         ┌──────────────┐
                         │  PostgreSQL  │
                         └──────────────┘
```

## Rationale
- Centralized API entry point.
- Dynamic service discovery.
- Centralized configuration.
- Independent deployment and scaling of services.
- Clear separation between application services and infrastructure.