# Experimental APIs in Jetpack Compose & Material 3

As we build the "Real Estate Manager," you will notice the `@OptIn(ExperimentalMaterial3Api::class)` annotation in our code. Here is why we use it and what it means for the project.

## 1. What does "Experimental" mean in this context?
In the Android ecosystem (specifically for Compose and Material 3), "Experimental" **does not** mean "unstable" or "buggy." 

It means that the **API surface** (the names of functions and parameters) is not yet "locked." Google reserves the right to change a parameter name or structure in a future library update based on developer feedback. The actual UI behavior and performance are usually very solid.

## 2. Why are we using them?
We use these APIs for two critical reasons:

### A. Essential Modern UI Components
Many standard components in **Material 3** (the latest design system) are still technically marked as experimental. This includes:
*   `TopAppBar` (The header of our app)
*   `FloatingActionButton` (The "Add" button)
*   `Scaffold` behaviors

Using these ensures the app looks modern and follows the latest Android design guidelines.

### B. Adaptive & Tablet Support
You requested an app that works beautifully on both phones and tablets. The tools to achieve this (`androidx.compose.material3.adaptive`) are the newest innovations from Google. Because they are the "future" of multi-pane navigation, they are currently in the experimental phase while Google finalizes the best way to handle different screen sizes.

## 3. Production Readiness
Despite the label, these APIs are considered **industry standard**. Major apps (including Google's own) use these exact same experimental components to provide a high-quality user experience.

## 4. The Maintenance Trade-off
The only downside is a small maintenance task: when we update our Gradle libraries in the future, we might occasionally need to rename a parameter in our code to match the new "Stable" version. 

**Conclusion**: By opting in today, we ensure the "Real Estate Manager" is built on a modern, future-proof foundation rather than using outdated technologies (like Material 2 or legacy XML) that would require a complete rewrite later.
