package com.fittrack.pro.data.local.entity

/**
 * Exercise category - defines equipment type and exercise classification
 */
enum class ExerciseCategory {
    BARBELL,     // Sztanga
    DUMBBELL,    // Hantle
    MACHINE,     // Maszyna
    CABLE,       // Wyciąg
    BODYWEIGHT,  // Masa ciała
    KETTLEBELL,  // Kettlebell
    BAND,        // Guma
    CARDIO,      // Kardio
    TRX,         // TRX / Taśmy
    BALL,        // Piłka
    ROLLER,      // Roller
    // Muscle group based categories (from import)
    SHOULDERS,   // Barki
    LEGS,        // Nogi
    ABS,         // Brzuch
    ARMS,        // Ręce
    CHEST,       // Klatka piersiowa
    BACK,        // Plecy
    COMPOUND,    // Ćwiczenia złożone
    STRETCHING,  // Rozciąganie/Mobilizacja
    OTHER;       // Inne

    fun getDisplayName(): String = when (this) {
        BARBELL -> "Sztanga"
        DUMBBELL -> "Hantle"
        MACHINE -> "Maszyna"
        CABLE -> "Wyciąg"
        BODYWEIGHT -> "Masa ciała"
        KETTLEBELL -> "Kettlebell"
        BAND -> "Guma"
        CARDIO -> "Kardio"
        TRX -> "TRX"
        BALL -> "Piłka"
        ROLLER -> "Roller"
        SHOULDERS -> "Barki"
        LEGS -> "Nogi"
        ABS -> "Brzuch"
        ARMS -> "Ręce"
        CHEST -> "Klatka piersiowa"
        BACK -> "Plecy"
        COMPOUND -> "Ćwiczenia złożone"
        STRETCHING -> "Rozciąganie"
        OTHER -> "Inne"
    }

    fun getDisplayNameEn(): String = when (this) {
        BARBELL -> "Barbell"
        DUMBBELL -> "Dumbbell"
        MACHINE -> "Machine"
        CABLE -> "Cable"
        BODYWEIGHT -> "Bodyweight"
        KETTLEBELL -> "Kettlebell"
        BAND -> "Resistance Band"
        CARDIO -> "Cardio"
        TRX -> "TRX"
        BALL -> "Ball"
        ROLLER -> "Roller"
        SHOULDERS -> "Shoulders"
        LEGS -> "Legs"
        ABS -> "Abs"
        ARMS -> "Arms"
        CHEST -> "Chest"
        BACK -> "Back"
        COMPOUND -> "Compound"
        STRETCHING -> "Stretching"
        OTHER -> "Other"
    }
}

