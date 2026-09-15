# ADR-003: Azure Cloud Services

#### **Status: Accepted**

## Context

The application requires external cloud capabilities for text-to-speech, AI-powered text processing, and persistent audio storage.

## Decision

Use Microsoft Azure as the cloud provider for these capabilities.

| Requirement             | Azure Service         |
| ----------------------- | --------------------- |
| Text-to-Speech          | Azure Speech Services |
| AI text processing      | Azure AI Foundry      |
| Generated audio storage | Azure Blob Storage    |


## Architecture
```
                    Transform Service
                           │
             ┌─────────────┼─────────────┐
             │             │             │
             ▼             ▼             ▼
      Azure Speech    Azure AI       Azure Blob
       Services       Foundry         Storage
          │              │               │
          ▼              ▼               ▼
        Audio       Enhanced Text    Audio Files
```

## Rationale
- Azure Speech provides multilingual TTS and voice customization.
- Azure AI Foundry provides hosted AI models for text transformation.
- Azure Blob Storage provides durable cloud storage for generated audio.
- Keeps cloud-specific integrations behind application service abstractions where appropriate.
- Provides a clear path toward deploying the complete application on Azure.