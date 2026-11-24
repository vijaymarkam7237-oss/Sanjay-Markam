package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.ui.graphics.vector.ImageVector

object ArenaRoutes {
    const val HOME = "home"
    const val MY_MATCHES = "my_matches"
    const val UPCOMING = "upcoming"
    const val HISTORY = "history"
    const val RESULTS = "results"
    const val WALLET = "wallet"
    const val REGISTER_BASE = "register"
    const val REGISTER_PATTERN = "register?tournamentId={tournamentId}"
    const val GAME_HISTORY = "game_history"
    const val PROFILE = "profile"
    const val RULES = "rules"
    const val ACCOUNT = "account"
    const val SETTINGS = "settings"
    const val REWARDS = "rewards"
    const val SUPPORT = "support"
    const val ADMIN = "admin"

    fun registerRoute(tournamentId: Long = -1L): String = "$REGISTER_BASE?tournamentId=$tournamentId"

    val bottomNavRoutes = setOf(HOME, MY_MATCHES, WALLET, PROFILE)

    fun titleForRoute(route: String?): String = when {
        route == HOME -> "Battle Arena"
        route == MY_MATCHES -> "My Matches"
        route == UPCOMING -> "Upcoming Tournaments"
        route == HISTORY -> "Tournament History"
        route == RESULTS -> "Results & Standings"
        route == WALLET -> "Demo Wallet"
        route?.startsWith(REGISTER_BASE) == true -> "Join Tournament"
        route == GAME_HISTORY -> "Game History"
        route == PROFILE -> "Player Profile"
        route == RULES -> "Rules & Fair Play"
        route == ACCOUNT -> "Account"
        route == SETTINGS -> "Settings"
        route == REWARDS -> "Demo Rewards"
        route == SUPPORT -> "Support & Help"
        route == ADMIN -> "Admin Dashboard"
        else -> "Battle Arena"
    }
}

data class BottomNavDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

val arenaBottomNavItems = listOf(
    BottomNavDestination(
        route = ArenaRoutes.HOME,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "bottom_nav_home"
    ),
    BottomNavDestination(
        route = ArenaRoutes.MY_MATCHES,
        label = "Matches",
        selectedIcon = Icons.Filled.SportsEsports,
        unselectedIcon = Icons.Outlined.SportsEsports,
        testTag = "bottom_nav_matches"
    ),
    BottomNavDestination(
        route = ArenaRoutes.WALLET,
        label = "Wallet",
        selectedIcon = Icons.Filled.AccountBalanceWallet,
        unselectedIcon = Icons.Outlined.AccountBalanceWallet,
        testTag = "bottom_nav_wallet"
    ),
    BottomNavDestination(
        route = ArenaRoutes.PROFILE,
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        testTag = "bottom_nav_profile"
    )
)

data class DrawerNavItem(
    val route: String,
    val label: String,
    val subtitle: String,
    val icon: ImageVector,
    val section: String,
    val testTag: String
)

val arenaDrawerItems = listOf(
    // Tournaments & Matches
    DrawerNavItem(ArenaRoutes.HOME, "Home Hub", "Featured brackets & balance", Icons.Filled.Home, "ARENA", "drawer_home"),
    DrawerNavItem(ArenaRoutes.UPCOMING, "Upcoming", "Open brackets & countdowns", Icons.Filled.Schedule, "ARENA", "drawer_upcoming"),
    DrawerNavItem(ArenaRoutes.MY_MATCHES, "My Matches", "Registered rooms & status", Icons.Filled.SportsEsports, "ARENA", "drawer_matches"),
    DrawerNavItem(ArenaRoutes.registerRoute(), "Join Tournament", "Solo, Duo & Squad entry", Icons.Filled.MilitaryTech, "ARENA", "drawer_register"),
    DrawerNavItem(ArenaRoutes.RESULTS, "Results & Standings", "Verified demo rankings", Icons.Filled.EmojiEvents, "ARENA", "drawer_results"),
    DrawerNavItem(ArenaRoutes.HISTORY, "History", "Past joined tournaments", Icons.Filled.History, "ARENA", "drawer_history"),
    DrawerNavItem(ArenaRoutes.GAME_HISTORY, "Game History", "Match-by-match stats", Icons.Filled.Timeline, "ARENA", "drawer_game_history"),
    // Demo Economy
    DrawerNavItem(ArenaRoutes.WALLET, "Demo Wallet", "Demo points & ledger", Icons.Filled.AccountBalanceWallet, "DEMO REWARDS", "drawer_wallet"),
    DrawerNavItem(ArenaRoutes.REWARDS, "Rewards", "Claim demo collectibles", Icons.Filled.CardGiftcard, "DEMO REWARDS", "drawer_rewards"),
    // Player & Info
    DrawerNavItem(ArenaRoutes.PROFILE, "Profile", "Avatar, UID & player stats", Icons.Filled.Person, "PLAYER & APP", "drawer_profile"),
    DrawerNavItem(ArenaRoutes.RULES, "Rules & Fair Play", "Eligibility & dispute guide", Icons.Filled.Gavel, "PLAYER & APP", "drawer_rules"),
    DrawerNavItem(ArenaRoutes.ACCOUNT, "Account", "Demo sign-in & data info", Icons.Filled.ManageAccounts, "PLAYER & APP", "drawer_account"),
    DrawerNavItem(ArenaRoutes.SETTINGS, "Settings", "Theme, alerts & privacy", Icons.Filled.Settings, "PLAYER & APP", "drawer_settings"),
    DrawerNavItem(ArenaRoutes.SUPPORT, "Support", "FAQ & local issue reports", Icons.Filled.SupportAgent, "PLAYER & APP", "drawer_support"),
    // Staff
    DrawerNavItem(ArenaRoutes.ADMIN, "Admin Dashboard", "Demo organizer tools", Icons.Filled.AdminPanelSettings, "ORGANIZER", "drawer_admin")
)
