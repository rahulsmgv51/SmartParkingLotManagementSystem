# Parking Lot Code Flow Diagram

This document contains the request flow for the Parking Lot Management System.

## Mermaid Flowchart

```mermaid
flowchart TD
    A[HTTP Request] --> B[CorrelationIdFilter]
    B -->|set MDC and response headers| C[Controller]
    C --> D{Request type}
    D -->|POST /api/v1/parking/entry| E[ParkingController.parkVehicle]
    D -->|POST /api/v1/parking/exit| F[ParkingController.exitVehicle]
    D -->|GET /api/v1/dashboard/summary| G[DashboardController.getSummary]
    D -->|GET /api/v1/dashboard/active-vehicles| H[DashboardController.getActiveVehicles]

    E --> I[ParkingServiceImpl.parkVehicle]
    I --> J[Find active ticket by vehicle number]
    J -->|found| K[Throw ParkingException: vehicle already parked]
    J -->|not found| L[Find available spot by vehicle type]
    L -->|empty| M[Throw ParkingException: no parking spot available]
    L -->|found| N[Mark spot occupied and save]
    N --> O[Save ticket]
    O --> P[Publish VehicleParkedEvent]
    P --> Q[Increment OCCUPIED_SPOTS]
    Q --> R[Increment ACTIVE_VEHICLES]
    R --> S[Decrement available spots counter]
    S --> T[Increment entry metric]
    T --> U[Return EntryResponse]

    F --> V[ParkingServiceImpl.exitVehicle]
    V --> W[Find active ticket by ticket id]
    W -->|empty| X[Throw ParkingException: ticket not found]
    W -->|found| Y[Calculate parking fee]
    Y --> Z[Find parking spot by id]
    Z -->|empty| AA[Throw ParkingException: spot not found]
    Z -->|found| AB[Mark spot free and save]
    AB --> AC[Complete ticket and save]
    AC --> AD[Publish VehicleExitedEvent]
    AD --> AE[Decrement OCCUPIED_SPOTS]
    AE --> AF[Decrement ACTIVE_VEHICLES]
    AF --> AG[Increment available spots counter]
    AG --> AH[Increment exit metric]
    AH --> AJ[Return ExitResponse]

    G --> AK[DashboardServiceImpl.getSummary]
    AK --> AL[Read TOTAL_SPOTS counter]
    AL --> AM[Read OCCUPIED_SPOTS counter]
    AM --> AN[Read ACTIVE_VEHICLES counter]
    AN --> AO[Compute available spots]
    AO --> AP[Return DashboardResponse]

    H --> AQ[DashboardServiceImpl.getActiveVehicles]
    AQ --> AR[Find active tickets]
    AR --> AS[Map to active vehicle responses]
    AS --> AT[Return list]

    K --> AU[GlobalExceptionHandler.handleParkingException]
    M --> AU
    X --> AU
    AA --> AU
    AU --> AV[HTTP 400 Bad Request]
```

## Notes

- `CorrelationIdFilter` adds trace metadata before the request reaches controllers.
- `ParkingController` and `DashboardController` delegate business logic to services.
- `ParkingServiceImpl` handles both entry and exit scenarios with event publishing and cache updates.
- `DashboardServiceImpl` uses Redis counters for fast summary responses.
- `GlobalExceptionHandler` converts `ParkingException` into `HTTP 400 Bad Request` responses.
