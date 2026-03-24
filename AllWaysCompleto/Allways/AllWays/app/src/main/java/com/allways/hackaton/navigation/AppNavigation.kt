package com.allways.hackaton.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.allways.hackaton.ui.addinfo.AddInfoScreen
import com.allways.hackaton.ui.auth.LoginScreen
import com.allways.hackaton.ui.home.HomeScreen
import com.allways.hackaton.ui.reviews.ReviewsScreen
import com.allways.hackaton.ui.search.SearchScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Search : Screen("search")
    object Home : Screen("home/{placeId}") {
        fun createRoute(placeId: String) = "home/$placeId"
    }
    object AddInfo : Screen("addinfo/{placeId}") {
        fun createRoute(placeId: String) = "addinfo/$placeId"
    }
    object Reviews : Screen("reviews/{placeId}") {
        fun createRoute(placeId: String) = "reviews/$placeId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Search.route) {
            SearchScreen(navController)
        }

        composable(
            route = Screen.Home.route,
            arguments = listOf(navArgument("placeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString("placeId") ?: ""
            HomeScreen(navController, placeId)
        }

        composable(
            route = Screen.AddInfo.route,
            arguments = listOf(navArgument("placeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString("placeId") ?: ""
            AddInfoScreen(navController, placeId)
        }

        composable(
            route = Screen.Reviews.route,
            arguments = listOf(navArgument("placeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString("placeId") ?: ""
            ReviewsScreen(navController, placeId)
        }
    }
}