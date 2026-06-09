package com.donor.calendar.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*

sealed class Screen(val route: String, val label: String) {
    object Home    : Screen("home",    "Статус")
    object History : Screen("history", "История")
}

@Composable
fun DonorApp() {
    val navController = rememberNavController()
    val vm: DonorViewModel = viewModel()
    val uiState by vm.uiState.collectAsState()

    val navItems = listOf(Screen.Home, Screen.History)
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDest = backStackEntry?.destination
                navItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = when (screen) {
                                    Screen.Home    -> Icons.Filled.Home
                                    Screen.History -> Icons.Filled.DateRange
                                },
                                contentDescription = screen.label
                            )
                        },
                        label = { Text(screen.label) },
                        selected = currentDest?.hierarchy?.any { it.route == screen.route } == true,
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Добавить") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    uiState = uiState,
                    onGenderChange = { vm.setGender(it) }
                )
            }
            composable(Screen.History.route) {
                HistoryScreen(
                    donations = uiState.donations,
                    onDelete = { vm.deleteDonation(it) }
                )
            }
        }

        if (showAddDialog) {
            AddDonationDialog(
                onDismiss = { showAddDialog = false },
                onSave = { date, type ->
                    vm.addDonation(date, type)
                    showAddDialog = false
                }
            )
        }
    }
}
