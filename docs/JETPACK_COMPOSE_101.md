# Jetpack Compose 101: Architectural Cheat Sheet

Welcome to modern Android UI! Jetpack Compose is a **Declarative** UI toolkit. It completely changes where things are stored and how the UI is built.

## 1. Where does everything go? (Architectural Shift)

| UI Component | Old Way (Views/XML) | New Way (Compose) |
| :--- | :--- | :--- |
| **Layouts** | `res/layout/*.xml` | Kotlin files (`Screen.kt`) |
| **Styles/Themes** | `res/values/styles.xml` | `ui/theme/Theme.kt` |
| **Colors** | `res/values/colors.xml` | `ui/theme/Color.kt` |
| **Components** | `Button`, `TextView` | `@Composable` functions |
| **State** | `findViewByID` & `setText` | `mutableStateOf(data)` |

> [!NOTE]
> **What still stays in `res/`?**
> You still use the `res/` folder for **Strings** (`strings.xml`), **Images/Icons** (`drawable/`), and **Raw resources** (fonts, sounds).

---

## 2. Core Concepts

### A. The `@Composable` Annotation
This tells the Kotlin compiler: "This function is for drawing a piece of UI."
```kotlin
@Composable
fun PropertyTitle(title: String) {
    Text(text = title, style = MaterialTheme.typography.titleLarge)
}
```

### B. State & Recomposition
The UI **reacts** to data. If you want a button to change something, you update a state variable, and Compose "recomposes" (re-runs) the UI function.

```kotlin
@Composable
fun Counter() {
    // 'remember' makes the state survive UI updates
    var count by remember { mutableIntStateOf(0) }

    Button(onClick = { count++ }) {
        Text("I have been clicked $count times")
    }
}
```

### C. Modifiers: The "Styling" Tool
In XML, you had attributes like `android:padding`. In Compose, we use **`Modifier`**. They are chained together to position and style a component.

```kotlin
Text(
    text = "Modern Real Estate",
    modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .background(Color.LightGray)
)
```

---

## 3. Layout Essentials
Instead of `LinearLayout` or `RelativeLayout`, we use three main containers:

1.  **`Column`**: Stacks items vertically (like `LinearLayout` vertical).
2.  **`Row`**: Stacks items horizontally (like `LinearLayout` horizontal).
3.  **`Box`**: Stacks items on top of each other (like `FrameLayout`).

---

## 4. The "Single Source of Truth" (MVVM)
In this project, we will follow this flow:
1.  **Room DB**: Holds the raw data.
2.  **ViewModel**: Fetches data from Room and converts it into a `State`.
3.  **Compose UI**: "Observes" that state and displays it.

**If you change data in the database, the pipe (Flow) sends it to the ViewModel, which updates the State, which causes Compose to Recompose the screen. Zero manual UI updates!**
