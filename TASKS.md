# FitTrack Pro - Master Task List

## Etap 1: Core MVP ✅
- [x] Inicjalizacja projektu Android (Gradle, Clean Architecture)
- [x] Room Database z encjami (Exercise, Routine, Workout, User)
- [x] Podstawowe DAOs i Repositories
- [x] UI: Lista ćwiczeń z wyszukiwaniem
- [x] UI: Lista i tworzenie rutyn
- [x] CreateRoutineScreen z wyborem ćwiczeń
- [x] Polska lokalizacja
- [x] Ciemny motyw

## Etap 2: Aktywny Trening ⏳
- [x] Implement WorkoutRepository (interface + impl)
- [x] ActiveWorkoutScreen z listą ćwiczeń/serii
- [/] Timer główny treningu (Foreground Service)
- [x] Timer przerwy z auto-start
- [x] Input dla wagi/powtórzeń z walidacją
- [x] Sugestie serii bazując na historii
- [ ] Logika ćwiczeń jednostronnych
- [x] Ekran podsumowania treningu

## Etap 2.5: Advanced Workout Features ⏳
- [x] SetType enum rozszerzony do 10 typów (R,D,M,U,N,B,C,P,X,1)
- [x] ActiveWorkoutScreen z SetType picker
- [x] Menu ćwiczenia (notatka, timer, zmień, usuń)
- [x] Progress bar "X/Y ZAPISZ"
- [x] RoutineDetailScreen z zakładkami (Informacje, Statystyki, Historia)
- [x] Menu rutyny (edytuj, duplikuj, udostępnij, archiwizuj)
- [ ] Per-exercise rest timers (R/N) - UI exists, data model pending

## Etap 3: Import Bazy Ćwiczeń ✅
- [x] Parser Excel (exercises.xlsx) → Python script
- [x] Ekstrakcja mięśni z opisu (regex)
- [x] Generowanie JSON (438 ćwiczeń)
- [x] Seed database przy pierwszym uruchomieniu
- [x] Upload mediów do Supabase Storage

## Etap 4: Statystyki i Historia
- [ ] Ekran historii ćwiczenia
- [ ] Wykresy z Vico Charts
- [ ] Filtry czasowe (3m, 6m, 12m, all)
- [ ] Metryki: objętość, 1RM, max ciężar

## Etap 5: AI i Inteligentne Sugestie
- [ ] Rekomendacje ćwiczeń do rutyny
- [ ] Zamiana ćwiczenia (zajęta ławka)
- [ ] Analiza komentarzy użytkownika
- [ ] Progresja treningowa

## Etap 6: Zaawansowane Funkcje
- [ ] Body Measurements
- [ ] Plate Calculator
- [ ] Warm-up Calculator
- [ ] RPE/RIR tracking
- [ ] Udostępnianie rutyn
