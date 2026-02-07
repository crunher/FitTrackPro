package com.fittrack.pro.presentation.screens.workout

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fittrack.pro.data.local.entity.SetType
import com.fittrack.pro.data.local.entity.WorkoutSetEntity
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    onFinish: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: ActiveWorkoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showFinishDialog by remember { mutableStateOf(false) }
    var showRoutineMenu by remember { mutableStateOf(false) }
    var comment by remember { mutableStateOf("") }

    // Calculate progress
    val totalSets = state.exercises.sumOf { it.plannedSets }
    val completedSets = state.exercises.sumOf { e -> e.sets.count { it.completed } }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ActiveWorkoutEvent.WorkoutFinished -> onFinish(event.sessionId)
                is ActiveWorkoutEvent.RestTimerFinished -> { /* Can show snackbar */ }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.Close, contentDescription = "Zamknij")
                        }
                        IconButton(onClick = { /* Download/export */ }) {
                            Icon(Icons.Default.Download, contentDescription = "Eksportuj")
                        }
                    }
                },
                actions = {
                    // Elapsed time in center-ish
                    Text(
                        formatElapsedTime(state.elapsedSeconds),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    // Audio controls
                    IconButton(onClick = { /* Toggle voice note */ }) {
                        Icon(Icons.Default.Mic, contentDescription = "Notatka głosowa")
                    }
                }
            )
        },
        bottomBar = {
            // Progress bar with ZAPISZ button
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = { showFinishDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "$completedSets/$totalSets  ZAPISZ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Routine Name with menu
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            state.routineName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Box {
                            Surface(
                                onClick = { showRoutineMenu = true },
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.MoreHoriz,
                                    contentDescription = "Menu",
                                    modifier = Modifier.padding(4.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            
                            DropdownMenu(
                                expanded = showRoutineMenu,
                                onDismissRequest = { showRoutineMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Dodaj notatkę") },
                                    onClick = { showRoutineMenu = false },
                                    leadingIcon = { Icon(Icons.Default.Edit, null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Zmień czas") },
                                    onClick = { showRoutineMenu = false },
                                    leadingIcon = { Icon(Icons.Default.Schedule, null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Oznacz wszystkie jako roztrenowanie") },
                                    onClick = { 
                                        showRoutineMenu = false
                                        // viewModel.markAllAsWarmup()
                                    },
                                    leadingIcon = { Icon(Icons.Default.FitnessCenter, null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Zmień nazwę") },
                                    onClick = { showRoutineMenu = false },
                                    leadingIcon = { Icon(Icons.Default.Edit, null) }
                                )
                            }
                        }
                    }

                    // Rest Timer Banner
                    AnimatedVisibility(
                        visible = state.isRestTimerRunning,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        RestTimerBanner(
                            seconds = state.restTimerSeconds,
                            onSkip = { viewModel.skipRestTimer() },
                            onAddTime = { viewModel.addRestTime(30) }
                        )
                    }

                    // Exercise List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(state.exercises) { exerciseIndex, exerciseWithSets ->
                            ExerciseCard(
                                exerciseWithSets = exerciseWithSets,
                                warmupRestTime = "01:00",
                                workingRestTime = "03:00",
                                onAddSet = { viewModel.addSet(exerciseIndex) },
                                onUpdateSet = { setIndex, weight, reps ->
                                    viewModel.updateSet(exerciseIndex, setIndex, weight, reps)
                                },
                                onCompleteSet = { setIndex ->
                                    viewModel.completeSet(exerciseIndex, setIndex)
                                },
                                onDeleteSet = { setIndex ->
                                    viewModel.deleteSet(exerciseIndex, setIndex)
                                },
                                onSetTypeChange = { setIndex, setType ->
                                    viewModel.setSetType(exerciseIndex, setIndex, setType)
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }

    // Finish Dialog
    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Zakończ trening") },
            text = {
                Column {
                    Text("Dodaj komentarz do treningu (opcjonalnie):")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Jak poszło?") },
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFinishDialog = false
                        viewModel.finishWorkout(comment.takeIf { it.isNotBlank() })
                    }
                ) {
                    Text("Zapisz")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
}

@Composable
private fun RestTimerBanner(
    seconds: Int,
    onSkip: () -> Unit,
    onAddTime: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Przerwa",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    formatRestTime(seconds),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onAddTime) {
                    Text("+30s")
                }
                Button(onClick = onSkip) {
                    Text("Pomiń")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseCard(
    exerciseWithSets: ExerciseWithSets,
    warmupRestTime: String,
    workingRestTime: String,
    onAddSet: () -> Unit,
    onUpdateSet: (Int, Double?, Int?) -> Unit,
    onCompleteSet: (Int) -> Unit,
    onDeleteSet: (Int) -> Unit,
    onSetTypeChange: (Int, SetType) -> Unit
) {
    var showExerciseMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Exercise Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    exerciseWithSets.exercise.namePl ?: exerciseWithSets.exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Box {
                    Surface(
                        onClick = { showExerciseMenu = true },
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreHoriz,
                            contentDescription = "Menu ćwiczenia",
                            modifier = Modifier.padding(4.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    
                    // Exercise menu dropdown
                    DropdownMenu(
                        expanded = showExerciseMenu,
                        onDismissRequest = { showExerciseMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Dodaj notatkę") },
                            onClick = { showExerciseMenu = false },
                            leadingIcon = { Icon(Icons.Default.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Minutnik przerwy") },
                            onClick = { showExerciseMenu = false },
                            leadingIcon = { Icon(Icons.Default.Timer, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Zmień ćwiczenie") },
                            onClick = { showExerciseMenu = false },
                            leadingIcon = { Icon(Icons.Default.SwapHoriz, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Oznacz jako roztrenowanie") },
                            onClick = { showExerciseMenu = false },
                            leadingIcon = { Icon(Icons.Default.FitnessCenter, null) }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("Usuń", color = MaterialTheme.colorScheme.error) },
                            onClick = { showExerciseMenu = false },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Rest timer badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    color = Color(0xFF4DB6AC),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "R $warmupRestTime",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
                Surface(
                    color = Color(0xFF78909C),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "N $workingRestTime",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sets Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Seria", style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(44.dp))
                Text("Poprzednio", style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(80.dp))
                Text("kg", style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
                Text("Powt.", style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(50.dp), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.width(40.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sets List
            exerciseWithSets.sets.forEachIndexed { index, set ->
                SetRow(
                    set = set,
                    setIndex = index,
                    previousSet = exerciseWithSets.historicalSets.getOrNull(index),
                    onUpdate = { weight, reps -> onUpdateSet(index, weight, reps) },
                    onComplete = { onCompleteSet(index) },
                    onDelete = { onDeleteSet(index) },
                    onTypeChange = { onSetTypeChange(index, it) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Add Set Button
            OutlinedButton(
                onClick = onAddSet,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Dodaj Serię")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetRow(
    set: WorkoutSetEntity,
    setIndex: Int,
    previousSet: WorkoutSetEntity?,
    onUpdate: (Double?, Int?) -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    onTypeChange: (SetType) -> Unit
) {
    var weightText by remember(set.id, set.weight) { 
        mutableStateOf(set.weight?.toString() ?: "") 
    }
    var repsText by remember(set.id, set.reps) { 
        mutableStateOf(set.reps?.toString() ?: "") 
    }
    var showTypePicker by remember { mutableStateOf(false) }

    val setTypeColor = Color(set.setType.colorHex)
    val backgroundColor = when {
        set.completed -> setTypeColor.copy(alpha = 0.15f)
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set Number & Type - Clickable to change type
        Box {
            Surface(
                onClick = { showTypePicker = true },
                color = setTypeColor,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.width(44.dp)
            ) {
                Text(
                    set.setType.getShortLabel().ifEmpty { (setIndex + 1).toString() },
                    modifier = Modifier.padding(vertical = 8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            // SetType Picker Dropdown
            DropdownMenu(
                expanded = showTypePicker,
                onDismissRequest = { showTypePicker = false }
            ) {
                SetType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.getDisplayName()) },
                        onClick = {
                            onTypeChange(type)
                            showTypePicker = false
                        },
                        leadingIcon = {
                            Surface(
                                color = Color(type.colorHex),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        type.getShortLabel().ifEmpty { "1" },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }

        // Previous
        Text(
            previousSet?.let { "${it.weight?.toInt() ?: "-"} kg x ${it.reps ?: "-"}" } ?: "-",
            modifier = Modifier.width(80.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Weight Input
        OutlinedTextField(
            value = weightText,
            onValueChange = { 
                weightText = it
                onUpdate(it.toDoubleOrNull(), repsText.toIntOrNull())
            },
            modifier = Modifier.width(60.dp).height(48.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            enabled = !set.completed,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        // Reps Input
        OutlinedTextField(
            value = repsText,
            onValueChange = { 
                repsText = it
                onUpdate(weightText.toDoubleOrNull(), it.toIntOrNull())
            },
            modifier = Modifier.width(50.dp).height(48.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = !set.completed,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        // Complete Checkbox
        Checkbox(
            checked = set.completed,
            onCheckedChange = { 
                if (!set.completed && weightText.isNotBlank() && repsText.isNotBlank()) {
                    onComplete()
                }
            },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

private fun formatElapsedTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%02d:%02d", minutes, secs)
    }
}

private fun formatRestTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%d:%02d", minutes, secs)
}
