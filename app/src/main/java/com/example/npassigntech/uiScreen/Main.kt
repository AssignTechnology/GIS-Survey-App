package com.example.npassigntech.uiScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.npassigntech.component.AppDrawer
import com.example.npassigntech.component.AppNavigationBar
import com.example.npassigntech.component.AppTopBar
import com.example.npassigntech.model.responseModel.BottomNav
import com.example.npassigntech.navigation.Screen
import com.example.npassigntech.uiScreen.dashboard.Home
import com.example.npassigntech.uiScreen.dashboard.Services
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(navController: NavHostController) {
    val dashboardNavController = rememberNavController()

    val items = listOf(
        BottomNav("Home", Screen.Home.route, Icons.Filled.Home, Icons.Outlined.Home),
        BottomNav(
            "Services",
            Screen.Services.route,
            Icons.Filled.Dashboard,
            Icons.Outlined.Dashboard
        ),
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { AppDrawer(scope, drawerState, navController) },
        content = {
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    AppTopBar(
                        title = "Nagar Panchayat",
                        scrollBehavior = scrollBehavior,
                        navigationIcon = Icons.Filled.Menu,
                        onNavigationIconClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        },
                        rememberNavController = navController,


                        content = {
                            NavHost(
                                navController = dashboardNavController,
                                startDestination = Screen.Home.route,
                                modifier = Modifier.padding(it)
                            ) {
                                composable(Screen.Home.route) { Home(navController) }
                                composable(Screen.Services.route) { Services(navController) }
                            }
                        }
                    )
                },
                bottomBar = {
                    AppNavigationBar(navController = dashboardNavController, items = items)
                }
            ) { innerPadding ->
                innerPadding.calculateTopPadding()
            }
        }
    )
}

