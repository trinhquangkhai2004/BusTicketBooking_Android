package com.khaiqueng_finalterm.busticketbooking.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.khaiqueng_finalterm.busticketbooking.ui.screens.BusListScreen
import com.khaiqueng_finalterm.busticketbooking.ui.screens.CheckoutScreen
import com.khaiqueng_finalterm.busticketbooking.ui.screens.HomeScreen
import com.khaiqueng_finalterm.busticketbooking.ui.screens.LoginScreen
import com.khaiqueng_finalterm.busticketbooking.ui.screens.PaymentScreen
import com.khaiqueng_finalterm.busticketbooking.ui.screens.RegisterScreen
import com.khaiqueng_finalterm.busticketbooking.ui.screens.SeatSelectionScreen
import com.khaiqueng_finalterm.busticketbooking.ui.screens.TicketDetailsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable("home") {
            HomeScreen(
                onSearchClick = { from, to, date ->
                    navController.navigate("busList/$from/$to/$date")
                },
                onLogoutClick = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "busList/{from}/{to}/{date}",
            arguments = listOf(
                navArgument("from") { type = NavType.StringType },
                navArgument("to") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val from = backStackEntry.arguments?.getString("from") ?: "Đà Nẵng"
            val to = backStackEntry.arguments?.getString("to") ?: "Huế"
            val date = backStackEntry.arguments?.getString("date") ?: "09 Th04"
            BusListScreen(
                fromLocation = from,
                toLocation = to,
                selectedDate = date,
                onBackClick = { navController.popBackStack() },
                onBusClick = { navController.navigate("checkout") }
            )
        }

        composable("checkout") {
            CheckoutScreen(
                onBackClick = { navController.popBackStack() },
                onSelectSeatsClick = { navController.navigate("seatSelection") }
            )
        }

        composable("seatSelection") {
            SeatSelectionScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = { navController.navigate("payment") }
            )
        }

        composable("payment") {
            PaymentScreen(
                onBackClick = { navController.popBackStack() },
                onPaymentConfirmClick = { navController.navigate("ticketDetails") }
            )
        }

        composable("ticketDetails") {
            TicketDetailsScreen(
                onHomeClick = {
                    navController.navigate("home") { popUpTo("home") { inclusive = true } }
                }
            )
        }
    }
}
