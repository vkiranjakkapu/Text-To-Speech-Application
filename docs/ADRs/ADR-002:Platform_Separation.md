# ADR-002: Platform Separation

#### **Status: Accepted**

## Context

Multiple services require common technical capabilities such as security, logging, HTTP communication, and web infrastructure. Duplicating these capabilities across services would increase maintenance and create inconsistent implementations.

## Decision

Separate reusable cross-cutting infrastructure into a dedicated platform layer, independently from business-oriented services.
```
platform/
├── security
├── logging
├── restclient
└── web

services/
├── identity
├── transform
└── reports
```

## Platform Responsibilities

### Security

JWT authentication
Authorization
Current-user abstraction

### Logging

Correlation ID
MDC
Request logging
Exception logging

### RestClient

Inter-service HTTP communication
Header/context propagation

### Web

Common web infrastructure
Shared exception handling and web concerns

## Rationale
Avoids duplication across services.
Provides consistent cross-cutting behavior.
Keeps business services focused on business functionality.
Allows platform capabilities to be reused by future services.