package com.fittrack.pro.data.local.entity

/**
 * Muscle groups for exercise classification
 */
enum class MuscleGroup {
    CHEST,       // Klatka piersiowa
    BACK,        // Plecy
    SHOULDERS,   // Barki
    BICEPS,      // Biceps
    TRICEPS,     // Triceps
    FOREARMS,    // Przedramiona
    ABS,         // Brzuch
    OBLIQUES,    // Skośne brzucha
    CORE,        // Core
    QUADS,       // Czworogłowy uda
    HAMSTRINGS,  // Dwugłowy uda
    GLUTES,      // Pośladki
    CALVES,      // Łydki
    TRAPS,       // Czworoboczny (mięsień kapturowy)
    LOWER_BACK,  // Dolna część pleców
    ADDUCTORS,   // Przywodziciele
    ABDUCTORS,   // Odwodziciele
    FULL_BODY,   // Całe ciało
    CARDIO,      // Kardio
    OTHER;       // Inne

    fun getDisplayName(): String = when (this) {
        CHEST -> "Klatka piersiowa"
        BACK -> "Plecy"
        SHOULDERS -> "Barki"
        BICEPS -> "Biceps"
        TRICEPS -> "Triceps"
        FOREARMS -> "Przedramiona"
        ABS -> "Brzuch"
        OBLIQUES -> "Skośne brzucha"
        CORE -> "Core"
        QUADS -> "Czworogłowy uda"
        HAMSTRINGS -> "Dwugłowy uda"
        GLUTES -> "Pośladki"
        CALVES -> "Łydki"
        TRAPS -> "Mięsień czworoboczny"
        LOWER_BACK -> "Dolna część pleców"
        ADDUCTORS -> "Przywodziciele"
        ABDUCTORS -> "Odwodziciele"
        FULL_BODY -> "Całe ciało"
        CARDIO -> "Kardio"
        OTHER -> "Inne"
    }

    fun getDisplayNameEn(): String = when (this) {
        CHEST -> "Chest"
        BACK -> "Back"
        SHOULDERS -> "Shoulders"
        BICEPS -> "Biceps"
        TRICEPS -> "Triceps"
        FOREARMS -> "Forearms"
        ABS -> "Abs"
        OBLIQUES -> "Obliques"
        CORE -> "Core"
        QUADS -> "Quadriceps"
        HAMSTRINGS -> "Hamstrings"
        GLUTES -> "Glutes"
        CALVES -> "Calves"
        TRAPS -> "Trapezius"
        LOWER_BACK -> "Lower Back"
        ADDUCTORS -> "Adductors"
        ABDUCTORS -> "Abductors"
        FULL_BODY -> "Full Body"
        CARDIO -> "Cardio"
        OTHER -> "Other"
    }
}

