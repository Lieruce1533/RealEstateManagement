# AI Assistant Progress Log

## Date: 2026-09-18

### Summary of Work Done
1.  **Database Schema Export**:
    *   Configured Room schema export (`exportSchema = true` and `room.schemaLocation`) in `AppDatabase.kt` and `app/build.gradle` to generate version-controlled JSON schema files for documentation and presentation graphs.
    *   Updated Android Gradle Plugin (AGP) to `9.4.1` in `libs.versions.toml`.

## Date: 2026-09-17

### Summary of Work Done
1.  **Sample Data Seeding**:
    *   Designed a robust sample data generator to seed 15 realistic properties across 3 agents ("Agent Smith", "Agent Jane", "Agent Dupont") with varied real estate types, prices, surfaces, amenities, points of interest, GPS coordinates, and real image URLs.

## Date: 2026-09-16

### Summary of Work Done
1.  **Currency Repository & Live Rates**:
    *   Implemented `CurrencyRepository` using Coroutines (`Dispatchers.IO`) and `HttpURLConnection` to fetch live USD-to-EUR rates from the network.
    *   Enhanced `Utils.java` with dynamic rate setters and automatic reverse rate calculation (`convertEuroToDollar`).
    *   Triggered background rate fetching on app launch inside `MainActivity.kt`.
2.  **Java 8+ API Desugaring (`java.time`)**:
    *   Configured Java 8+ API Desugaring (`coreLibraryDesugaring`) in `app/build.gradle` and `gradle/libs.versions.toml`.
    *   Enabled the use of modern `java.time` (`LocalDate`, `DateTimeFormatter`) across all Android API levels (23+).

## Date: 2026-09-13

### Summary of Work Done
1.  **Architecture Discussion & Mentorship**:
    *   Explained Jetpack Compose **Single-Activity Architecture** (moving away from legacy multi-activity / multi-fragment designs).
    *   Clarified that our screens are **Composable functions** (Screen Composables) rather than Fragments or Activities.

## Date: 2026-09-12

### Summary of Work Done
1.  **Project Review & Mentorship**:
    *   Reviewed earlier work (Kotlin basics, Room architecture, MVVM, Navigation 3).
    *   Clarified `PropertyProvider` UriMatcher paths and the role of the `/#` wildcard.
    *   Clarified Room database entity relationships between `RealEstateItem` and `PropertyPicture`, foreign keys, and CASCADE deletion.
    *   Clarified Navigation 3 `@Serializable` keys as the modern equivalent of `Parcelable`.
    *   Added new collaboration rule: **Comprehensive Code Commenting** (adding detailed comments to all new/modified code).
    *   Updated `AGENT_RULES.md` and committed changes.

## Date: 2026-08-11

### Summary of Work Done
1.  **Project Modernization & Cleanup**:
    *   Updated Gradle to **9.5.0** and Android Gradle Plugin to **9.3.1**.
    *   Migrated the legacy project to **AndroidX**.
    *   Updated `compileSdk` to **37** and `minSdkVersion` to **23**.
    *   Cleaned up legacy Java/XML code and implemented modern `MainActivity.kt` with Jetpack Compose.
    *   Renamed package from `com.lieruce` to `com.lieruce`.
2.  **Git & Infrastructure**:
    *   Initialized **Git** repository with a modern `.gitignore`.
    *   Created and switched to the **`develop`** branch.
    *   Set up type-safe navigation keys and screen stubs.
3.  **Data Layer**:
    *   Implemented **Room** database with `RealEstateItem`, `PropertyPicture`, and `PropertyWithPictures`.
    *   Created **PropertyDao** and **PropertyRepository**.
    *   Implemented a **Content Provider** (`PropertyProvider`) for external data access.
4.  **UI & Navigation**:
    *   Set up **Navigation 3** with an **Adaptive NavGraph** (supports Phone and Tablet side-by-side).
    *   Created a modern **Material 3 Theme** (`ui/theme`).
    *   Developed screen stubs for List, Detail, Map, Add/Edit, and Search.

### Next Steps
*   Start implementing the visual design for `PropertyListScreen`.
*   Implement `PropertyViewModel` to connect Room data to the UI.
*   Implement `PropertyDetailScreen` with functional **osmdroid** map integration.
*   Added necessary permissions (Internet, Network State) for map functionality.
*   Updated `addTestProperty` with GPS coordinates for verification.
