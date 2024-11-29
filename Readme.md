# Fourthwall

----

## Prerequisites

Before you begin setting up the project, ensure you have the following software installed on your system:
Software Requirements

1. **Java**:
    You need to have Java Development Kit (JDK) installed. You can download it from the official Oracle website or use a package manager like apt, brew, or sdkman.


2. **Gradle**:
    Gradle is required for building the project. You can install it via the Gradle website or through a package manager.


3. **Docker**:
        Docker is necessary for containerization. Follow the instructions on the Docker website to install Docker on your machine.

4. **Docker Compose:**
        Docker Compose is needed to manage multi-container Docker applications. It is usually included with Docker Desktop. If you need to install it separately, check the Docker Compose installation guide.

## Environment Variables

After installing the required software, you need to set up the following environment variables. Replace xxx with your actual API keys and passwords:

```bash
export OMDBAPI_KEY=xxx
export CINEMAMANAGER_USER_PASSWORD=xxx
export MOVIESLISTING_USER_PASSWORD=xxx
export EVENTBUS_USER_PASSWORD=qw
```

---

## Documentation Decision

### Overview

This document outlines the architectural decisions made for the project, focusing on the design of services, communication methods, and code modularization. These decisions aim to enhance scalability, maintainability, and clarity within the system.

#### Service Architecture
Decision: Implement Two Distinct Services

   - Cinema Manager: This service acts as the backend administration tool specifically designed for cinema managers. It handles administrative tasks, user management, and data management related to cinema operations.
  
   - Movies Listing: This service serves as the frontend API for movie listings. It provides an interface for users to access and interact with movie data, including searching, filtering, and retrieving information.

#### Rationale

The decision to implement two separate services allows for a clear separation of concerns, enabling each service to focus on its specific functionality. This decoupling enhances the scalability of the system, as each service can evolve independently based on its requirements. 

#### Communication Method
Decision: Use Event-Driven Communication

   - EventBus: All communication between the Cinema Manager and Movies Listing services is handled via an event-driven architecture implemented through an EventBus. This approach facilitates asynchronous communication, allowing services to react to events without direct dependencies on each other.

#### Rationale

Using an event-driven model promotes loose coupling between services, which enhances system resilience and flexibility. It allows for easier scaling and integration of additional services in the future. 

#### Code Modularization
Decision: Modularize Code into Packges

   - Infrastructure Setup: The setup for edge components is organized into a single module. This module handles all infrastructure-related configurations, ensuring a clear entry point for managing the system's external interfaces.

   - Domain Separation: The codebase is divided into separate domains corresponding to significant functionalities:
     - Managing: This domain encompasses all functionalities related to administration and management of cinema operations.
     - Listing: This domain focuses on functionalities related to movie listings and user interactions.
     - Administration: This domain manages administrative tasks and user access controls.

#### Rationale

Modularizing the codebase into distinct sections enhances maintainability and readability. Each module can be developed, tested, and deployed independently, which aligns with best practices in software design. This structure also simplifies the onboarding process for new developers by providing clear boundaries and responsibilities for each module.

#### Conclusion

These architectural decisions are aimed at creating a robust, scalable, and maintainable system. By implementing distinct services, employing an event-driven communication model, and modularizing the codebase, we aim to facilitate future growth and adaptability of the application while ensuring clarity in development and operations.

### API Documentation
Decision: Expose API Endpoints with Swagger UI and OpenAPI Specifications

   - Swagger UI: All API endpoints will be documented using Swagger UI, providing an interactive interface for developers to explore and test the APIs.

   - OpenAPI Specification: The API will be described using OpenAPI specifications, ensuring that the documentation is standardized and easily consumable by various tools and libraries.

#### Rationale

By exposing API endpoints with Swagger UI and OpenAPI specifications, we enhance the developer experience by providing clear, interactive documentation. This approach promotes better understanding and usage of the APIs, facilitates integration with other systems, and supports automated documentation generation.

Certainly! Here's an elaboration on the current state of the project, focusing on the missing components and areas that require further development:
Current State of the Project

Despite the initial aim of delivering a full solution, several critical components remain incomplete or underdeveloped. Below is a detailed overview of the project’s current status, highlighting the implemented features and the specific areas that require further attention.
Implemented Features

The project has successfully established several foundational elements:

- Decoupled Services: The architecture is designed with two distinct services: the Cinema Manager for backend administration and the Movies Listing service for frontend API access. This separation promotes maintainability and scalability.
- Event-Driven Communication: Services communicate through an EventBus, allowing for asynchronous messaging and reducing direct dependencies between components.
- Modular Code Structure: The codebase is organized into well-defined modules, facilitating easier maintenance and independent development of different functionalities.
- API Documentation: All API endpoints are documented using Swagger UI and OpenAPI specifications, providing a clear and interactive interface for developers.

### Areas for Improvement

While the project has made significant strides, several key components are either missing or inadequately addressed:

#### Simple Authorization

   Current State: The project lacks a comprehensive authorization mechanism. While basic user management may exist, there is no structured approach to defining user roles and permissions.

   Next Steps: Implement a robust authorization framework that includes role-based access control (RBAC) and token-based authentication (e.g., JWT). This will ensure that users can only access functionalities appropriate to their roles, thereby enhancing security.

#### Naive Validation

   Current State: Input validation is currently minimal and lacks depth, which poses risks to data integrity and security. The absence of thorough validation may allow invalid or malicious data to enter the system.

   Next Steps: Enhance input validation across all API endpoints. This could involve implementing server-side validation rules, using libraries for schema validation, and ensuring that every incoming request is checked against defined criteria to prevent issues such as SQL injection or data corruption.

#### Anemic Database Models

   Current State: The database models are primarily anemic, serving mainly as data containers without encapsulating business logic or relevant behavior. This can complicate maintenance and hinder the ability to adapt to evolving business requirements.

   Next Steps: Revise the database model design to incorporate richer domain models that encapsulate business logic. Adopting practices from Domain-Driven Design (DDD) can help ensure that models are responsible for their own behavior, improving cohesion and maintainability.

#### Full Error Handling (Database Errors)

   Current State: The project currently lacks comprehensive error handling, particularly for database-related errors. Without proper error handling, the application may fail to provide meaningful feedback to users or developers when issues arise, leading to a poor user experience and difficulties in debugging.

   Next Steps: Implement a full error handling strategy that captures and manages database errors gracefully. This includes logging errors, providing meaningful error messages to users, and ensuring that the application can recover from failures without crashing. Consider implementing centralized error handling middleware to streamline this process across the application.

Conclusion

In conclusion, while the project has laid a strong foundation with its architectural design and implemented features, several critical components need further development. By addressing the missing elements of simple authorization, enhanced validation, enriched database models, and comprehensive error handling, we can significantly improve the robustness, security, and maintainability of the application. This will ultimately lead to a more complete and effective solution that meets the needs of its users and stakeholders.