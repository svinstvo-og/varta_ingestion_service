# Varta Ingestion Service
## Overview
The **Varta Ingestion Service** is a Spring Boot application designed to ingest, normalize, and process financial and credit transaction data. It serves as an ETL (Extract, Transform, Load) pipeline, moving data from a raw MySQL source database to a structured PostgreSQL destination database using Spring Batch.
Additionally, the service includes capabilities for transaction enrichment (calculating risk features) and publishing processed data to Kafka.
## Tech Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.5.6
- **Batch Processing**: Spring Batch
- **Data Access**: Spring Data JPA, Hibernate
- **Databases**:
  - **Source**: MySQL (`jdbc:mysql://localhost:3307/sf_web_001`) - *Read Only*
  - **Destination**: PostgreSQL (`jdbc:postgresql://localhost:5433/ingestion-service-db`)
- **Messaging**: Apache Kafka (Fat & Credit Transactions)
- **Caching**: Redis, Caffeine
## Architecture
The service operates primarily as a batch processor.
1. **Readers**: Read raw transaction data from MySQL tables (`varta.model.mysql`).
2. **Processors**: Transform data, map relationships, and perform lookups (e.g., resolving `internal_id` from external IDs).
3. **Writers**: Write normalized data into PostgreSQL tables (`varta.model.pgsql`).
### Data Flow
`MySQL (Raw Data)` -> `Spring Batch Job` -> `PostgreSQL (Normalized Data)`
## Jobs
The application defines several batch jobs to handle different entities:
| Job Name | Trigger Component | Description |
|----------|-------------------|-------------|
| `CreditUserJob` | `NormalizationService` | Migrates user data. |
| `CreditStoreJob` | `NormalizationService` | Migrates merchant/store data. |
| `CreditCardJob` | `NormalizationService` | Migrates credit card information. |
| `FinancialTransactionJob` | `NormalizationService` | Processes general financial transactions. |
| `CreditTransactionJob` | `NormalizationService` | Processes credit-specific transactions. |
## API Reference
The service exposes REST endpoints to trigger batch jobs manually.
**Base URL**: `http://localhost:8080/api`
### Ingestion Controller
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/ingestion/launch` | Triggers **ALL** normalization jobs in sequence. |
### Job Trigger Controller
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/job/start/credit-user` | Starts `CreditUserJob` |
| POST | `/job/start/credit-store` | Starts `CreditStoreJob` |
| POST | `/job/start/credit-card` | Starts `CreditCardJob` |
| POST | `/job/start/financial-transaction` | Starts `FinancialTransactionJob` |
| POST | `/job/start/credit-transaction` | Starts `CreditTransactionJob` |
## Configuration
Key configuration is found in `application.yml`.
- **MySQL (Source)**: Configured on port `3307`.
- **PostgreSQL (Destination)**: Configured on port `5433`.
- **Kafka**: Bootstrap server at `localhost:9092`.
- **Redis**: Host `localhost`, port `6379`.
### Note on Database Initialization
The `spring.sql.init.mode` is set to `never` by default to avoid accidental write attempts to the read-only MySQL source. 
> **Important**: "set this to always after dropping pgsql db, then set back to never" (from code comments).
## Key Features
### Enrichment Service
- Calculates transaction velocity (1H, 24H).
- Computes Z-Scores and ratio to median for monetary values.
- Identifies abnormal states in transactions.
### Kafka Publishing
- **Topics**: 
  - `fat-transactions`: Publishes `FatTransactionDto` (Enriched transaction data).
  - `credit-transaction`: Publishes raw `CreditTransaction`.
