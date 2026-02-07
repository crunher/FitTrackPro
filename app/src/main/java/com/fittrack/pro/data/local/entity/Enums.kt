package com.fittrack.pro.data.local.entity

/**
 * Tracking type - defines how the exercise is measured
 */
enum class TrackingType {
    REPS,                  // Powtórzenia
    WEIGHT_X_REPS,         // Ciężar x Powtórzenia
    TIME,                  // Czas
    WEIGHT_X_TIME,         // Ciężar x Czas
    RESISTANCE_X_REPS,     // Opór x Powtórzenia  
    DISTANCE_X_KCAL_X_TIME; // Dystans x kcal x Czas

    fun getDisplayName(): String = when (this) {
        REPS -> "Powtórzenia"
        WEIGHT_X_REPS -> "Ciężar × Powt."
        TIME -> "Czas"
        WEIGHT_X_TIME -> "Ciężar × Czas"
        RESISTANCE_X_REPS -> "Opór × Powt."
        DISTANCE_X_KCAL_X_TIME -> "Dystans × kcal × Czas"
    }

    fun getDisplayNameEn(): String = when (this) {
        REPS -> "Reps"
        WEIGHT_X_REPS -> "Weight × Reps"
        TIME -> "Time"
        WEIGHT_X_TIME -> "Weight × Time"
        RESISTANCE_X_REPS -> "Resistance × Reps"
        DISTANCE_X_KCAL_X_TIME -> "Distance × kcal × Time"
    }
}

/**
 * Set type - różne typy serii treningowych
 */
enum class SetType(val colorHex: Long) {
    WARMUP(0xFF4DB6AC),      // R - Rozgrzewka (teal)
    WORKING(0xFF78909C),     // 1,2,3... - Normalna (grey-blue)
    DROPSET(0xFFFFB74D),     // D - Dropset (orange)
    MYO_REPS(0xFFBA68C8),    // M - Myo reps (purple)
    TO_FAILURE(0xFFE57373),  // U - Do upadku (red)
    NEGATIVE(0xFF64B5F6),    // N - Negatywna (blue)
    BACK_OFF(0xFF81C784),    // B - Back off (green)
    CLUSTER(0xFFFFD54F),     // C - Cluster set (yellow)
    PARTIAL(0xFFAB47BC),     // P - Częściowe (magenta)
    FAILED(0xFFEF5350);      // X - Niepowodzenie (red)

    fun getDisplayName(): String = when (this) {
        WARMUP -> "Rozgrzewka"
        WORKING -> "Normalna seria"
        DROPSET -> "Dropset"
        MYO_REPS -> "Myo reps"
        TO_FAILURE -> "Do upadku"
        NEGATIVE -> "Negatywna"
        BACK_OFF -> "Back off"
        CLUSTER -> "Cluster set"
        PARTIAL -> "Powtórzenia częściowe"
        FAILED -> "Niepowodzenie"
    }

    fun getShortLabel(): String = when (this) {
        WARMUP -> "R"
        WORKING -> "" // Will show set number instead
        DROPSET -> "D"
        MYO_REPS -> "M"
        TO_FAILURE -> "U"
        NEGATIVE -> "N"
        BACK_OFF -> "B"
        CLUSTER -> "C"
        PARTIAL -> "P"
        FAILED -> "X"
    }
}

/**
 * Side for unilateral exercises
 */
enum class Side {
    LEFT,
    RIGHT,
    BOTH;

    fun getDisplayName(): String = when (this) {
        LEFT -> "Lewa"
        RIGHT -> "Prawa"
        BOTH -> "Obie"
    }
}

/**
 * Days of week for routine assignment
 */
enum class DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    fun getDisplayName(): String = when (this) {
        MONDAY -> "Poniedziałek"
        TUESDAY -> "Wtorek"
        WEDNESDAY -> "Środa"
        THURSDAY -> "Czwartek"
        FRIDAY -> "Piątek"
        SATURDAY -> "Sobota"
        SUNDAY -> "Niedziela"
    }

    fun getShortName(): String = when (this) {
        MONDAY -> "Pon"
        TUESDAY -> "Wt"
        WEDNESDAY -> "Śr"
        THURSDAY -> "Czw"
        FRIDAY -> "Pt"
        SATURDAY -> "Sob"
        SUNDAY -> "Nd"
    }
}
