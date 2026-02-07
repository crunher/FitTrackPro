# FitTrack Pro - Workout Application

Aplikacja treningowa na Android

## Wymagania
- Android Studio Hedgehog lub nowszy
- JDK 17
- Android SDK 35

## Otwieranie projektu
1. Otwórz Android Studio
2. File -> Open -> wybierz folder `WorkoutApp`
3. Poczekaj na synchronizację Gradle

## Uruchomienie
1. Podłącz telefon Android lub uruchom emulator
2. Kliknij "Run" (Ctrl+F5)

## Struktura projektu
```
app/src/main/java/com/fittrack/pro/
├── di/                  # Hilt DI modules
├── data/
│   ├── local/          # Room database
│   │   ├── entity/     # Database entities
│   │   └── dao/        # Data Access Objects
│   └── repository/     # Repository implementations
├── domain/
│   └── repository/     # Repository interfaces
├── presentation/
│   ├── theme/          # Compose theme
│   ├── navigation/     # Navigation setup
│   ├── components/     # Reusable UI components
│   └── screens/        # App screens
└── service/            # Background services
```
