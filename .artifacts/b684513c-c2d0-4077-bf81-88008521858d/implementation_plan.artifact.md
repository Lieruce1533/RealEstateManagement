# Implementation Plan - Real Estate Manager

This plan outlines the transformation of the current project into a modern Android application for real estate agents. We will transition to Kotlin and Jetpack Compose while maintaining the existing `Utils.java` class.

## User Review Required

> [!IMPORTANT]
> **Zero-Cost Map Solution**: To ensure you **never receive a bill** and don't need to provide a credit card to Google, we will use:
> 1. **osmdroid (OpenStreetMap)** for displaying maps. It is completely open-source and free for mobile apps.
> 2. **Android Native `Geocoder`** for converting addresses to coordinates. This is a built-in system service that is free for developers.

> [!IMPORTANT]
> **Optional Feature Priority**: Which feature would you like to prioritize for the final stage: **Loan Simulator**, **Video support**, or **Firebase Synchronization**? (Note: Firebase has a generous free tier, but we can start with the Loan Simulator for a 100% local/free experience).

## Educational Approach

Since this is your first experience with Kotlin and Jetpack Compose, I will follow these rules:
- **Kotlin 101**: For every new Kotlin concept (Data Classes, Coroutines, Flow), I will provide a brief "Why and How" explanation.
- **Compose logic**: I will explain the "Declarative" nature of Compose (UI as a function of state) and how "Recomposition" works.
- **Adaptive UI**: I will explain how we handle different screen sizes (Mobile vs Tablet) using a single codebase.

## Proposed Changes

### 1. Project Groundwork & Configuration
- **Kotlin Support**: Add Kotlin Gradle plugin and standard libraries.
- **Jetpack Compose**: Add Compose libraries, Compiler, and Material 3.
- **Room Database**: Add Room dependencies for persistence.
- **Adaptive Libraries**: Add Material 3 Adaptive and Navigation 3 for multi-pane support.

### 2. Architecture (MVVM)
We will use **Model-View-ViewModel (MVVM)**:
- **Model**: Data classes and Room entities.
- **View**: Jetpack Compose functions (screens).
- **ViewModel**: Manages the UI state and interacts with the Repository.

### 3. Data Layer (The Foundation)
- **Room Entity**: A `RealEstateItem` class containing all requested fields (Price, Surface, etc.).
- **Room DAO**: Interface defining database operations (Insert, Update, Delete, Query with filters).
- **Repository**: A bridge that handles data fetching logic and provides a unified API to the ViewModels.
- **Content Provider**: A class to expose the Real Estate data to other applications.

### 4. UI Layer (Compose & Navigation)
- **Theme**: Define colors and shapes using Material 3.
- **Adaptive Navigation**:
    - **Mobile**: Single-pane navigation (List -> Detail -> Map).
    - **Tablet**: Split-pane navigation (List on left, Detail on right).
- **Screens**:
    - **List Screen**: Scrollable list of properties.
    - **Detail Screen**: Full property info + Map.
    - **Edit/Add Screen**: Form with image picking (Camera/Gallery) and validation.
    - **Map Screen**: Fullscreen map with nearby properties.
    - **Filter Screen**: Advanced search criteria.

### 5. Features & Logic
- **Offline Mode**: All data is stored locally in Room; the app works without internet.
- **Geocoding**: Automatically convert addresses to GPS coordinates using Android's Geocoder or a web service.
- **Notifications**: Trigger a system notification after successful property creation.
- **Image Handling**: Use modern `ActivityResultContracts` to pick images from the gallery or capture via camera.

## Verification Plan

### Automated Tests
- **Unit Tests**: Test the Room DAO and Repository logic.
- **UI Tests**: Verify the Compose screens and navigation flow.

### Manual Verification
- Deploy to a phone (Mobile layout check).
- Deploy to a tablet/emulator (Tablet layout check).
- Verify offline functionality by disabling data/wifi.
- Verify Content Provider by reading data from a test tool.

## Open Questions
- Do you have a preference for the Map solution (Google Maps vs OpenStreetMap)?
- For the Tablet layout, should the "Map View" be a third pane or replace the detail/list?
- Which optional feature (Loan Simulator, Video, or Firebase) would you like to prioritize for the final stage?
