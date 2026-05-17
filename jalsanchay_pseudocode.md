# Jal-Sanchay Tracker — Complete Pseudocode

## 1. Application Entry Point

```
APPLICATION JalSanchayApp
    INITIALIZE Hilt dependency injection
    CREATE Room database singleton
    SEED demo data on first launch
END APPLICATION

ACTIVITY MainActivity
    INJECT UserPreferences (DataStore)
    
    ON CREATE:
        darkMode ← READ preferences.darkMode
        authState ← CHECK saved session (userId from DataStore)
        
        IF session exists AND user found in DB:
            IF user has TankSetup → START at DashboardScreen
            ELSE → START at SetupScreen
        ELSE:
            START at LoginScreen
        
        RENDER NavGraph(startDestination)
END ACTIVITY
```

---

## 2. Data Layer

### 2.1 Entities (Room Database Tables)

```
ENTITY User
    id          : INT (auto-generated, primary key)
    name        : STRING
    email       : STRING (unique)
    passwordHash: STRING (SHA-256)
    city        : STRING
    householdSize: STRING ("1-2", "3-4", "5+")
    createdAt   : LONG (timestamp)

ENTITY TankSetup
    id                    : INT (auto-generated, primary key)
    userId                : INT (foreign key → User)
    roofAreaM2            : FLOAT
    roofMaterial          : STRING ("Concrete/Tile", "Metal/GI", etc.)
    runoffCoefficient     : FLOAT (0.40 – 0.90)
    tankCapacityLiters    : FLOAT
    tankMaterial          : STRING ("Plastic", "Concrete", etc.)
    currentWaterLevelLiters: FLOAT

ENTITY RainfallEntry
    id                  : INT (auto-generated, primary key)
    userId              : INT (foreign key → User)
    date                : STRING ("yyyy-MM-dd")
    rainfallMm          : FLOAT
    litersHarvested     : FLOAT (calculated)
    roofAreaUsed        : FLOAT (snapshot)
    runoffUsed          : FLOAT (snapshot)
    tankLevelAfterLiters: FLOAT
    source              : STRING ("Manual", "Weather App", "Estimated")
    notes               : STRING
```

### 2.2 DAOs (Data Access Objects)

```
DAO UserDao
    FUNCTION insertUser(user) → Long (returns new ID)
    FUNCTION getUserByEmail(email) → User?
    FUNCTION getUserById(id) → Flow<User?>
    FUNCTION updateUser(user)
    FUNCTION deleteUser(user)

DAO TankSetupDao
    FUNCTION insertSetup(setup) → Long
    FUNCTION getSetupByUserId(userId) → Flow<TankSetup?>
    FUNCTION updateSetup(setup)
    FUNCTION deleteByUserId(userId)

DAO RainfallEntryDao
    FUNCTION insertEntry(entry) → Long
    FUNCTION getEntriesByUser(userId) → Flow<List<RainfallEntry>>
    FUNCTION getEntriesBetweenDates(userId, from, to) → Flow<List<RainfallEntry>>
    FUNCTION getRecentEntries(userId, limit=5) → Flow<List<RainfallEntry>>
    FUNCTION getTotalHarvest(userId) → Flow<Float>
    FUNCTION updateEntry(entry)
    FUNCTION deleteEntry(entry)
    FUNCTION deleteAllByUser(userId)
```

### 2.3 Database Seed (Initial Demo Data)

```
ON FIRST DB CREATION:
    INSERT User("Priya Sharma", "priya@demo.com", SHA256("demo123"), "Mumbai")
    INSERT TankSetup(userId=1, roofArea=50, material="Concrete", coeff=0.85, capacity=5000, level=1200)
    INSERT 8 RainfallEntries spanning last 2 months with varied rainfall (5-45mm)
```

### 2.4 Repository

```
CLASS JalRepository
    INJECT userDao, tankSetupDao, rainfallEntryDao

    FUNCTION calculateHarvest(roofAreaM2, rainfallMm, runoffCoefficient) → Float
        RETURN roofAreaM2 × rainfallMm × 0.0929 × runoffCoefficient
    END

    // Delegates all DAO calls with suspend/Flow wrappers
    FUNCTION login(email, password) → User?
    FUNCTION register(name, email, password, city, household) → Long
    FUNCTION saveSetup(tankSetup) → Long
    FUNCTION logRainfall(entry) → Long
    FUNCTION updateTankLevel(userId, newLevel)
    // ... etc for all CRUD operations
END CLASS
```

### 2.5 DataStore (UserPreferences)

```
DATASTORE UserPreferences
    STORED VALUES:
        userId       : INT?     (active session)
        darkMode     : BOOLEAN  (default: false)
        waterRate    : FLOAT    (default: 0.05 ₹/liter)
        dailyNeed    : FLOAT    (default: 135 L/person/day)
        fontSize     : STRING   (Small/Medium/Large)
        dailyReminder: BOOLEAN  (default: false)
        reminderTime : STRING   (default: "08:00")
        rainThreshold: INT      (default: 80%)
        units        : STRING   (Metric/Imperial)
        colorTheme   : STRING   (Blue/Green/Purple)

    FUNCTIONS: get/set for each value via Flow
    FUNCTION clearSession() → removes userId
END DATASTORE
```

---

## 3. ViewModels

### 3.1 AuthViewModel

```
VIEWMODEL AuthViewModel
    STATE authState: Idle | Loading | Success(userId, hasSetup) | Error(msg)
    STATE sessionChecked: Boolean

    ON INIT:
        savedUserId ← READ preferences.userId
        IF savedUserId exists:
            user ← repository.getUserById(savedUserId)
            setup ← repository.getSetup(savedUserId)
            IF user found:
                authState ← Success(userId, hasSetup = setup != null)
        sessionChecked ← true

    FUNCTION loginUser(email, password):
        authState ← Loading
        hash ← SHA256(password)
        user ← repository.getUserByEmail(email)
        IF user exists AND user.passwordHash == hash:
            SAVE userId to preferences
            setup ← repository.getSetup(user.id)
            authState ← Success(user.id, setup != null)
        ELSE:
            authState ← Error("Invalid credentials")

    FUNCTION registerUser(name, email, password, city, household):
        authState ← Loading
        IF email already exists → Error("Email taken")
        hash ← SHA256(password)
        newId ← repository.insertUser(User(name, email, hash, city, household))
        SAVE newId to preferences
        authState ← Success(newId, hasSetup=false)

    FUNCTION logout():
        CLEAR session from preferences
        authState ← Idle
END VIEWMODEL
```

### 3.2 SetupViewModel

```
VIEWMODEL SetupViewModel
    STATE setupState: Idle | Loading | Success | Error(msg)
    STATE userId, existingSetup

    FUNCTION loadExistingSetup(userId):
        existingSetup ← repository.getSetup(userId)

    FUNCTION saveSetup(userId, roofArea, material, coeff, capacity, tankMat, level):
        setupState ← Loading
        setup ← TankSetup(userId, roofArea, material, coeff, capacity, tankMat, level)
        IF existingSetup exists:
            repository.updateSetup(setup)
        ELSE:
            repository.insertSetup(setup)
        setupState ← Success
END VIEWMODEL
```

### 3.3 DashboardViewModel

```
VIEWMODEL DashboardViewModel
    STATE user, tankSetup, recentEntries, totalHarvest
    STATE todayHarvest, weekHarvest, monthHarvest, darkMode

    FUNCTION loadDashboard(userId):
        user         ← OBSERVE repository.getUser(userId)
        tankSetup    ← OBSERVE repository.getSetup(userId)
        recentEntries← OBSERVE repository.getRecent(userId, 5)
        totalHarvest ← OBSERVE repository.getTotalHarvest(userId)
        
        today ← today's date
        todayHarvest ← SUM entries WHERE date == today
        weekHarvest  ← SUM entries WHERE date in last 7 days
        monthHarvest ← SUM entries WHERE date in current month
END VIEWMODEL
```

### 3.4 LogRainfallViewModel

```
VIEWMODEL LogRainfallViewModel
    STATE logState, tankSetup, userId

    FUNCTION loadSetup(userId):
        tankSetup ← repository.getSetup(userId)

    FUNCTION calculatePreview(rainfallMm) → Float:
        IF tankSetup exists:
            RETURN repository.calculateHarvest(setup.roofArea, rainfallMm, setup.coeff)
        RETURN 0

    FUNCTION saveEntry(userId, date, rainfallMm, source, notes, updateTank):
        logState ← Loading
        setup ← tankSetup
        liters ← calculateHarvest(setup.roofArea, rainfallMm, setup.coeff)
        newTankLevel ← MIN(setup.currentLevel + liters, setup.capacity)
        
        entry ← RainfallEntry(
            userId, date, rainfallMm, liters,
            roofAreaUsed=setup.roofArea, runoffUsed=setup.coeff,
            tankLevelAfter=newTankLevel, source, notes
        )
        repository.insertEntry(entry)
        
        IF updateTank:
            repository.updateTankLevel(userId, newTankLevel)
        
        logState ← Success(liters)
END VIEWMODEL
```

### 3.5 HistoryViewModel

```
VIEWMODEL HistoryViewModel
    STATE entries, selectedFilter, userId

    FUNCTION loadEntries(userId):
        entries ← OBSERVE repository.getAllEntries(userId)

    FUNCTION applyFilter(userId, filter, customFrom?, customTo?):
        selectedFilter ← filter
        SWITCH filter:
            "All"           → entries ← all entries
            "This Week"     → entries ← last 7 days
            "This Month"    → entries ← current month
            "Last 3 Months" → entries ← last 90 days
            "Custom Range"  → entries ← between customFrom..customTo

    FUNCTION deleteEntry(entry):
        repository.deleteEntry(entry)

    FUNCTION updateEntry(entry):
        repository.updateEntry(entry)
END VIEWMODEL
```

### 3.6 ReportViewModel

```
VIEWMODEL ReportViewModel
    STATE currentMonth (YearMonth), monthEntries, prevMonthEntries, userId

    FUNCTION loadMonth(userId):
        monthEntries ← entries in currentMonth
        prevMonthEntries ← entries in (currentMonth - 1)

    FUNCTION previousMonth(userId):
        currentMonth ← currentMonth - 1
        loadMonth(userId)

    FUNCTION nextMonth(userId):
        currentMonth ← currentMonth + 1
        loadMonth(userId)

    FUNCTION generateShareText() → String:
        RETURN formatted summary: month, total rainfall, harvest, avg daily, peak day

    FUNCTION generateCsv() → String:
        RETURN "Date,Rainfall(mm),Harvested(L),Source,Notes\n" + entries as CSV rows
END VIEWMODEL
```

### 3.7 TipsViewModel

```
VIEWMODEL TipsViewModel
    STATE tips (List<Tip>), searchQuery, selectedCategory, bookmarkedIds
    categories = ["All", "Setup", "Maintenance", "Advanced", "Monsoon"]

    ON INIT:
        tips ← HARDCODED list of 10 tips, each with:
            id, title, summary, detail, category, difficulty, timeEstimate

    FUNCTION search(query):
        tips ← allTips.filter { title OR summary CONTAINS query }

    FUNCTION selectCategory(cat):
        IF cat == "All" → tips ← allTips
        ELSE → tips ← allTips.filter { category == cat }

    FUNCTION toggleBookmark(tipId):
        IF tipId in bookmarkedIds → REMOVE it
        ELSE → ADD it
END VIEWMODEL
```

### 3.8 ProfileViewModel

```
VIEWMODEL ProfileViewModel
    STATE user, tankSetup, totalHarvest, totalRainfall, entryCount, dailyNeed

    FUNCTION loadProfile(userId):
        user ← repository.getUser(userId)
        tankSetup ← repository.getSetup(userId)
        totalHarvest ← repository.getTotalHarvest(userId)
        totalRainfall ← SUM all entries.rainfallMm
        entryCount ← COUNT all entries

    FUNCTION updateProfile(userId, name, city, household):
        UPDATE user record in DB

    FUNCTION clearAllHistory(userId):
        repository.deleteAllEntries(userId)

    FUNCTION deleteAccount(userId):
        repository.deleteUser(userId)
        CLEAR session
END VIEWMODEL
```

---

## 4. UI Screens (Compose)

### 4.1 LoginScreen

```
SCREEN LoginScreen(onRegister, onLoginSuccess)
    RENDER animated water ripple circles (Canvas)
    RENDER WaterDrop icon + "Jal-Sanchay" title
    
    INPUT email (with validation)
    INPUT password (with toggle visibility)
    
    ON "Login" CLICK:
        VALIDATE fields not empty
        CALL authViewModel.loginUser(email, password)
    
    ON AuthState.Success → onLoginSuccess(hasSetup)
    ON AuthState.Error → SHOW error message
    
    BUTTON "Create Account" → onRegister()
END SCREEN
```

### 4.2 RegisterScreen

```
SCREEN RegisterScreen(onBack, onSuccess)
    INPUT name, email, password, confirmPassword, city
    DROPDOWN householdSize ("1-2", "3-4", "5+")
    
    ON "Create Account" CLICK:
        VALIDATE: name not empty, email valid format,
                  password ≥ 6 chars, passwords match, city not empty
        CALL authViewModel.registerUser(...)
    
    ON AuthState.Success → onSuccess()
END SCREEN
```

### 4.3 SetupScreen

```
SCREEN SetupScreen(onComplete, onSkip)
    SECTION "Roof Setup" (expandable):
        INPUT roofArea (m²)
        DROPDOWN roofMaterial → auto-sets runoffCoefficient
        SHOW coefficient info tooltip (expandable)
    
    SECTION "Tank Setup" (expandable):
        INPUT tankCapacity (liters)
        DROPDOWN tankMaterial
        INPUT currentWaterLevel
    
    SECTION "Formula Preview":
        LIVE COMPUTE: roofArea × 10mm × 0.0929 × coefficient = X liters
    
    BUTTON "Save Setup" → validate & save
    BUTTON "Skip for Now" → go to dashboard
END SCREEN
```

### 4.4 DashboardScreen (with 5 Bottom Tabs)

```
SCREEN DashboardScreen
    TOP BAR: "💧 Jal-Sanchay" + greeting + profile/settings icons
    BOTTOM NAV: [Home | Log Rain | History | Reports | Impact]
    
    TAB 0 - HOME:
        WaterWealthCard (today harvest, total savings, tank %)
        TankVisualization (animated water fill with waves + bubbles)
        QuickStats row (week, month, days of supply)
        "Log Today's Rainfall" button
        Recent entries list (last 5)
    
    TAB 1 - LOG: → LogRainfallScreen (inline)
    TAB 2 - HISTORY: → HistoryScreen (inline)
    TAB 3 - REPORTS: → MonthlyReportScreen (inline)
    TAB 4 - IMPACT: → ImpactScreen (inline)
END SCREEN
```

### 4.5 LogRainfallScreen

```
SCREEN LogRainfallScreen
    DATE PICKER (default: today)
    
    SECTION "Rainfall Input":
        INPUT rainfallMm
        LIVE PREVIEW: "≈ X liters will be harvested"
        DROPDOWN source
        INPUT notes (max 200 chars with counter)
    
    SECTION "Tank Update":
        SWITCH updateTank (on/off)
        IF on: SHOW new tank level + overflow warning if > capacity
    
    SECTION "Formula Breakdown" (expandable):
        SHOW: roofArea × rainfall × 0.0929 × coefficient = result
    
    BUTTON "Save Entry" → validate, compute, save, update tank
END SCREEN
```

### 4.6 HistoryScreen

```
SCREEN HistoryScreen
    FILTER CHIPS: [All | This Week | This Month | Last 3 Months | Custom Range]
    SUMMARY BANNER: "X entries | Y mm rainfall | Z L harvested"
    
    IF no entries → Empty state + "Log First Entry" button
    ELSE → LazyColumn of RainfallHistoryCards
    
    EACH CARD (expandable):
        Date, rainfall mm chip, liters chip
        EXPANDED: tank level bar, source, notes, formula breakdown
        ACTIONS: Edit (bottom sheet) | Delete (confirmation dialog)
    
    EDIT BOTTOM SHEET:
        Modify rainfall, source, notes → recalculate liters
    
    CUSTOM RANGE: Two DatePicker dialogs (from → to)
END SCREEN
```

### 4.7 MonthlyReportScreen

```
SCREEN MonthlyReportScreen
    MONTH SELECTOR: ← [Month Year] →
    
    SUMMARY CARD:
        Total rainfall, total harvest, avg daily, peak day, event count
    
    BAR CHART (Canvas):
        FOR each day in month:
            barHeight ← dailyHarvest / maxDailyValue × chartHeight
        Animate bars growing from bottom
    
    CUMULATIVE LINE CHART (Canvas):
        Plot running total of harvest across days
        Draw line segments + dot markers
    
    COMPARISON CARD ("vs Last Month"):
        Rainfall diff (↑/↓ with percentage)
        Harvest diff (↑/↓ with percentage)
    
    BUTTON "Share Report" → Android Share Intent (text)
    BUTTON "Export as CSV" → Share Intent (CSV format)
END SCREEN
```

### 4.8 ImpactScreen

```
SCREEN ImpactScreen
    HERO: ImpactMeter (arc gauge showing days of water supplied / 365)
    
    IMPACT CARDS (each expandable with detail):
        👥 People Supplied: totalHarvest / 135 L/day
        🍶 Drinking Water: totalHarvest bottles equivalent
        🌱 Garden Watering: totalHarvest / (area × 5 L/m²/day)
        💰 Money Saved: totalHarvest × waterRate
        🌍 CO₂ Saved: totalHarvest × 0.298 / 1000 → tree equivalents
    
    ACHIEVEMENT BADGES (horizontal scroll):
        💧 First Drop (harvest > 0)
        🌊 100 Liters (harvest ≥ 100)
        🏆 1000 Liters (harvest ≥ 1000)
        🌍 Eco Warrior (30 consecutive days)
        ⭐ Water Wise (profile + setup complete)
        Each: tap to reveal requirement / earned status
END SCREEN
```

### 4.9 SettingsScreen

```
SCREEN SettingsScreen
    SECTION "Appearance":
        TOGGLE Dark Mode
        EXPANDABLE Color Theme (Blue/Green/Purple radio)
        EXPANDABLE Font Size (Small/Medium/Large radio)
    
    SECTION "Data & Calculation":
        EXPANDABLE Water Rate (₹/liter input + save)
        EXPANDABLE Daily Need (liters/day input + save)
        EXPANDABLE Units (Metric/Imperial radio)
    
    SECTION "Notifications":
        TOGGLE Daily Reminder
        EXPANDABLE Reminder Time
        EXPANDABLE Rain Alert Threshold (% input + save)
    
    SECTION "Data Management":
        CLICK Export All Data → Share Intent
        CLICK Edit Tank Setup → navigate to SetupScreen
        CLICK Clear History → confirmation dialog
    
    SECTION "About":
        EXPANDABLE About Jal-Sanchay (version info)
        CLICK Water Harvesting Tips → TipsScreen
        EXPANDABLE About Rainwater Harvesting (educational text)
    
    SECTION "Account":
        CLICK View Profile → ProfileScreen
        CLICK Logout → confirmation → clear session → LoginScreen
END SCREEN
```

### 4.10 ProfileScreen

```
SCREEN ProfileScreen
    AVATAR: Gradient circle with initials
    
    VIEW MODE: name, city, member since, household chip, "Edit" button
    EDIT MODE: name/city inputs, household dropdown, Save/Cancel
    
    STATS CARD: total rainfall, harvest, entries, impact score
    SETUP SUMMARY CARD: roof area, material, capacity, coefficient + "Edit Setup"
    
    DANGER ZONE (expandable):
        "Clear All History" → confirmation dialog → delete entries
        "Delete Account" → confirmation dialog → delete all + logout
END SCREEN
```

---

## 5. Custom Components

### 5.1 TankVisualization (Canvas)

```
COMPONENT TankVisualization(currentLevel, capacity)
    percentage ← currentLevel / capacity × 100
    color ← RED if ≤25%, ORANGE if ≤50%, GREEN if ≤75%, BLUE if >75%
    
    ANIMATE fillFraction from 0 → target (1.5s ease-out)
    ANIMATE waveOffset 0→360° (3s loop, linear)
    ANIMATE bubbleProgress 0→1 (4s loop)
    
    DRAW on Canvas:
        1. Tank outline (rounded rect, wider corners at bottom)
        2. CLIP to tank shape:
           a. Wave path: sin wave at water surface level
           b. Second wave layer (offset, lighter alpha)
           c. 6 bubbles rising from bottom (white circles)
        3. Tank border stroke
        4. Tick marks at 25%, 50%, 75%
    
    OVERLAY: percentage text centered in tank
    BELOW: "X L / Y L capacity" label
END COMPONENT
```

### 5.2 ImpactMeter (Canvas)

```
COMPONENT ImpactMeter(value, maxValue, label, unit)
    progress ← value / maxValue (clamped 0..1)
    ANIMATE progress (1.5s ease-out)
    ANIMATE glowAlpha 0.3↔0.8 (2s pulse)
    
    DRAW 270° arc:
        Background arc (faded)
        Progress arc (green, rounded caps)
        Glow arc (accent, pulsing alpha)
    
    CENTER: value + unit text
    BELOW: label
END COMPONENT
```

---

## 6. Core Formula

```
FUNCTION calculateHarvest(roofAreaM2, rainfallMm, runoffCoefficient):
    ┌─────────────────────────────────────────────────────┐
    │ Liters = Area(m²) × Rainfall(mm) × 0.0929 × Coeff │
    └─────────────────────────────────────────────────────┘
    
    WHERE:
        0.0929 = conversion factor (1mm rain on 1 sq ft = 0.0929 liters)
        Coefficient values:
            Concrete/Tile  → 0.85
            Metal/GI Sheet → 0.90
            Asbestos       → 0.80
            Thatch/Grass   → 0.40
    
    EXAMPLE: 50m² × 20mm × 0.0929 × 0.85 = 79.0 liters
END FUNCTION
```

---

## 7. Navigation Graph

```
NAVGRAPH
    LoginScreen ──→ RegisterScreen (and back)
    LoginScreen ──→ SetupScreen (if no setup)
    LoginScreen ──→ DashboardScreen (if setup exists)
    RegisterScreen ──→ SetupScreen
    SetupScreen ──→ DashboardScreen
    DashboardScreen ──→ SettingsScreen
    DashboardScreen ──→ ProfileScreen
    SettingsScreen ──→ TipsScreen
    SettingsScreen ──→ SetupScreen (edit)
    SettingsScreen ──→ ProfileScreen
    ProfileScreen ──→ SetupScreen (edit)
    Any Screen ──→ LoginScreen (on logout)
END NAVGRAPH
```
