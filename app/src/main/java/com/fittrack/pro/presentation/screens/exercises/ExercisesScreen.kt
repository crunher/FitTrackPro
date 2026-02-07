package com.fittrack.pro.presentation.screens.exercises

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.fittrack.pro.R
import com.fittrack.pro.data.local.entity.*
import com.fittrack.pro.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    navController: NavController,
    viewModel: ExercisesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Search Bar
        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        
        // Filter Chips
        FilterChipsRow(
            selectedMuscle = uiState.selectedMuscle,
            selectedCategory = uiState.selectedCategory,
            onMuscleSelected = viewModel::onMuscleSelected,
            onCategorySelected = viewModel::onCategorySelected,
            onClearFilters = viewModel::clearFilters
        )
        
        // Exercise Count
        Text(
            text = "${uiState.filteredExercises.size} ćwiczeń",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // Exercise List
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.filteredExercises, key = { it.id }) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        onClick = {
                            // Navigate to exercise detail
                            navController.navigate("exercise/${exercise.id}")
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

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { 
            Text(
                text = stringResource(R.string.search),
                color = TextTertiary
            ) 
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TextSecondary
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = TextSecondary
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = BackgroundElevated,
            focusedContainerColor = BackgroundCard,
            unfocusedContainerColor = BackgroundCard
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
private fun FilterChipsRow(
    selectedMuscle: MuscleGroup?,
    selectedCategory: ExerciseCategory?,
    onMuscleSelected: (MuscleGroup?) -> Unit,
    onCategorySelected: (ExerciseCategory?) -> Unit,
    onClearFilters: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Clear filter chip
        if (selectedMuscle != null || selectedCategory != null) {
            FilterChip(
                selected = false,
                onClick = onClearFilters,
                label = { Text("Wyczyść") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
        
        // Muscle filter dropdown
        var muscleDropdownExpanded by remember { mutableStateOf(false) }
        Box {
            FilterChip(
                selected = selectedMuscle != null,
                onClick = { muscleDropdownExpanded = true },
                label = { 
                    Text(selectedMuscle?.getDisplayName() ?: "Mięsień") 
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            )
            
            DropdownMenu(
                expanded = muscleDropdownExpanded,
                onDismissRequest = { muscleDropdownExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Wszystkie") },
                    onClick = {
                        onMuscleSelected(null)
                        muscleDropdownExpanded = false
                    }
                )
                MuscleGroup.entries.forEach { muscle ->
                    DropdownMenuItem(
                        text = { Text(muscle.getDisplayName()) },
                        onClick = {
                            onMuscleSelected(muscle)
                            muscleDropdownExpanded = false
                        }
                    )
                }
            }
        }
        
        // Category filter dropdown
        var categoryDropdownExpanded by remember { mutableStateOf(false) }
        Box {
            FilterChip(
                selected = selectedCategory != null,
                onClick = { categoryDropdownExpanded = true },
                label = { 
                    Text(selectedCategory?.getDisplayName() ?: "Kategoria") 
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            )
            
            DropdownMenu(
                expanded = categoryDropdownExpanded,
                onDismissRequest = { categoryDropdownExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Wszystkie") },
                    onClick = {
                        onCategorySelected(null)
                        categoryDropdownExpanded = false
                    }
                )
                ExerciseCategory.entries.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.getDisplayName()) },
                        onClick = {
                            onCategorySelected(category)
                            categoryDropdownExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundCard
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Muscle indicator
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(getMuscleColor(exercise.mainMuscle).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = getMuscleColor(exercise.mainMuscle),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = exercise.namePl ?: exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = exercise.mainMuscle.getDisplayName(),
                        style = MaterialTheme.typography.bodySmall,
                        color = getMuscleColor(exercise.mainMuscle)
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                    Text(
                        text = exercise.category.getDisplayName(),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextTertiary
            )
        }
    }
}

private fun getMuscleColor(muscle: MuscleGroup): androidx.compose.ui.graphics.Color {
    return when (muscle) {
        MuscleGroup.CHEST -> MuscleChest
        MuscleGroup.BACK -> MuscleBack
        MuscleGroup.SHOULDERS -> MuscleShoulders
        MuscleGroup.BICEPS, MuscleGroup.TRICEPS, MuscleGroup.FOREARMS -> MuscleArms
        MuscleGroup.QUADS, MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES, MuscleGroup.CALVES -> MuscleLegs
        MuscleGroup.ABS, MuscleGroup.CORE -> MuscleCore
        else -> Primary
    }
}
