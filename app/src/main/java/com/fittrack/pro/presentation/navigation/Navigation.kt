package com.fittrack.pro.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.fittrack.pro.R
import com.fittrack.pro.presentation.screens.exercises.ExercisesScreen
import com.fittrack.pro.presentation.screens.home.HomeScreen
import com.fittrack.pro.presentation.screens.routines.CreateRoutineScreen
import com.fittrack.pro.presentation.screens.routines.RoutineDetailScreen
import com.fittrack.pro.presentation.screens.routines.RoutinesScreen
import com.fittrack.pro.presentation.screens.workout.ActiveWorkoutScreen
import com.fittrack.pro.presentation.screens.workout.WorkoutSummaryScreen

sealed class Screen(
    val route: String,
    val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : Screen(
        route = "home",
        titleRes = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    
    data object Exercises : Screen(
        route = "exercises",
        titleRes = R.string.nav_exercises,
        selectedIcon = Icons.Filled.FitnessCenter,
        unselectedIcon = Icons.Outlined.FitnessCenter
    )
    
    data object Routines : Screen(
        route = "routines",
        titleRes = R.string.nav_routines,
        selectedIcon = Icons.Filled.List,
        unselectedIcon = Icons.Outlined.List
    )
    
    data object Stats : Screen(
        route = "stats",
        titleRes = R.string.nav_stats,
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart
    )
    
    data object Profile : Screen(
        route = "profile",
        titleRes = R.string.nav_profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
    
    companion object {
        val bottomNavItems = listOf(Home, Exercises, Routines, Stats, Profile)
    }
}

// Other routes without bottom nav
object Routes {
    const val EXERCISE_DETAIL = "exercise/{exerciseId}"
    const val CREATE_ROUTINE = "create_routine"
    const val ROUTINE_DETAIL = "routine/{routineId}"
    const val EDIT_ROUTINE = "edit_routine/{routineId}"
    const val ACTIVE_WORKOUT = "active_workout/{routineId}"
    const val WORKOUT_SUMMARY = "workout_summary/{sessionId}"
    
    fun exerciseDetail(exerciseId: Long) = "exercise/$exerciseId"
    fun routineDetail(routineId: Long) = "routine/$routineId"
    fun editRoutine(routineId: Long) = "edit_routine/$routineId"  
    fun activeWorkout(routineId: Long) = "active_workout/$routineId"
    fun workoutSummary(sessionId: Long) = "workout_summary/$sessionId"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitTrackNavHost(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val showBottomBar = Screen.bottomNavItems.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    Screen.bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { 
                            it.route == screen.route 
                        } == true
                        
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon 
                                                  else screen.unselectedIcon,
                                    contentDescription = stringResource(screen.titleRes)
                                )
                            },
                            label = { Text(stringResource(screen.titleRes)) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Exercises.route) {
                ExercisesScreen(navController = navController)
            }
            composable(Screen.Routines.route) {
                RoutinesScreen(navController = navController)
            }
            composable(Screen.Stats.route) {
                // StatsScreen - placeholder for now
                PlaceholderScreen(title = "Statystyki")
            }
            composable(Screen.Profile.route) {
                // ProfileScreen - placeholder for now
                PlaceholderScreen(title = "Profil")
            }
            
            // Create Routine Screen
            composable(Routes.CREATE_ROUTINE) {
                CreateRoutineScreen(
                    onBack = { navController.popBackStack() },
                    onCreated = { routineId ->
                        navController.popBackStack()
                    }
                )
            }
            
            // Routine Detail Screen
            composable(
                route = Routes.ROUTINE_DETAIL,
                arguments = listOf(navArgument("routineId") { type = NavType.LongType })
            ) { backStackEntry ->
                val routineId = backStackEntry.arguments?.getLong("routineId") ?: 0L
                RoutineDetailScreen(
                    routineId = routineId,
                    onBack = { navController.popBackStack() },
                    onStartWorkout = { id ->
                        navController.navigate(Routes.activeWorkout(id))
                    }
                )
            }
            
            // Active Workout Screen
            composable(
                route = Routes.ACTIVE_WORKOUT,
                arguments = listOf(navArgument("routineId") { type = NavType.LongType })
            ) { backStackEntry ->
                ActiveWorkoutScreen(
                    onFinish = { sessionId ->
                        navController.navigate(Routes.workoutSummary(sessionId)) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            
            // Workout Summary Screen
            composable(
                route = Routes.WORKOUT_SUMMARY,
                arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
                WorkoutSummaryScreen(
                    sessionId = sessionId,
                    onDone = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
