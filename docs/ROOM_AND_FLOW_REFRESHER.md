# Room & Flow Refresher

## 1. The Room Architecture (The "Big Three")

Imagine the database as a **Library**:

*   **The Model (`@Entity`)**: These are the **Books**. 
    *   In your project, `RealEstateItem` and `PropertyPicture` are the entities. 
    *   Each class represents a **Table** in SQLite, and each property in the class is a **Column**.
*   **The DAO (`@Dao`)**: This is the **Librarian**. 
    *   It’s an interface where you define *how* to access the data (Insert, Update, Delete, Query). 
    *   Room generates the actual code to talk to the database based on your interface.
*   **The Database (`@Database`)**: This is the **Library Building**. 
    *   It holds the connection to the SQLite file and provides the "Librarian" (DAO) to the rest of the app.

---

## 2. How they are linked (Relationships)

In your app, one **Property** can have many **Pictures**. This is a "One-to-Many" relationship. We set it up using two layers of protection:

### Layer 1: The Database Link (`@ForeignKey`)
In `PropertyPicture.kt`, we use a `ForeignKey`.
*   **Logic**: It tells SQLite: "The `propertyId` in this picture MUST match a real `id` in the `RealEstateItem` table."
*   **Safety**: If you delete a house, `onDelete = ForeignKey.CASCADE` ensures all its pictures are automatically deleted too. No "ghost" pictures left in the database!

### Layer 2: The UI Link (`PropertyWithPictures`)
Room doesn't allow entities to reference each other directly. Instead, we created `PropertyWithPictures.kt`.
*   **`@Embedded`**: Fetches the `RealEstateItem`.
*   **`@Relation`**: Finds all rows in the `PropertyPicture` table where the `propertyId` matches this property's `id`.

---

## 3. Flow vs LiveData

**Flow** is the modern, more powerful replacement for `LiveData`.

| Feature | LiveData | Flow (StateFlow/SharedFlow) |
| :--- | :--- | :--- |
| **Origin** | Android Jetpack. | Kotlin Coroutines (Language level). |
| **Lifecycle** | Automatically lifecycle-aware. | Manual, but easy in Compose. |
| **Thread** | Main Thread only for observers. | Any thread (Background or Main). |
| **Power** | Basic observation. | Supports many "operators" (Filter, Map, etc.). |

### Logic in Compose
Think of a **Flow** as a water pipe:
*   **DAO**: The Source (pumping data).
*   **Compose UI**: The Faucet. 
*   **collectAsStateWithLifecycle()**: The "Adapter" that connects the pipe to the faucet safely.
