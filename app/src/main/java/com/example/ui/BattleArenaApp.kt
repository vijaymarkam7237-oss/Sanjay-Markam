package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.R
import com.example.ui.components.PlayerEmblemAvatar
import com.example.ui.components.StatusPill
import com.example.ui.navigation.ArenaRoutes
import com.example.ui.navigation.arenaBottomNavItems
import com.example.ui.navigation.arenaDrawerItems
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.GameHistoryScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyMatchesScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RegistrationScreen
import com.example.ui.screens.ResultsScreen
import com.example.ui.screens.RewardsScreen
import com.example.ui.screens.RulesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SupportScreen
import com.example.ui.screens.UpcomingScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.TrophyGold
import com.example.ui.viewmodel.ArenaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattleArenaApp(viewModel: ArenaViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var lastSnackbarWasError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.feedback.collect { feedback ->
            lastSnackbarWasError = feedback.isError
            snackbarHostState.showSnackbar(
                message = feedback.message,
                duration = SnackbarDuration.Short
            )
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isBottomDestination = currentRoute in ArenaRoutes.bottomNavRoutes

    fun navigateTo(route: String) {
        if (route == currentRoute) return
        if (route in ArenaRoutes.bottomNavRoutes) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        } else {
            navController.navigate(route) {
                launchSingleTop = true
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .widthIn(max = 310.dp)
                    .testTag("arena_navigation_drawer")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .statusBarsPadding()
                        .padding(bottom = 24.dp)
                ) {
                    // Branded Esports Drawer Header
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, ElectricPurple.copy(alpha = 0.5f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF1A1038), Color(0xFF0E2036))
                                    )
                                )
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    PlayerEmblemAvatar(
                                        avatarKey = uiState.profile?.avatarKey ?: "CYBER_KNIGHT",
                                        size = 48.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = uiState.profile?.nickname ?: "Challenger",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "UID: ${uiState.profile?.gameUid ?: "DEMO"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ElectricBlue
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StatusPill(
                                        label = "LOCAL DEMO",
                                        containerColor = ElectricPurple.copy(alpha = 0.25f),
                                        contentColor = Color.White
                                    )
                                    Text(
                                        text = "${uiState.profile?.demoPointsBalance ?: 0} DEMO PTS",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TrophyGold
                                    )
                                }
                            }
                        }
                    }

                    val grouped = arenaDrawerItems.groupBy { it.section }
                    grouped.forEach { (section, items) ->
                        Text(
                            text = section,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                        items.forEach { item ->
                            val selected = currentRoute == item.route ||
                                (item.route.startsWith(ArenaRoutes.REGISTER_BASE) && currentRoute?.startsWith(ArenaRoutes.REGISTER_BASE) == true)
                            NavigationDrawerItem(
                                label = {
                                    Column {
                                        Text(item.label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            item.subtitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                },
                                selected = selected,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    navigateTo(item.route)
                                },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 2.dp)
                                    .testTag(item.testTag)
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = ArenaRoutes.titleForRoute(currentRoute),
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    navigationIcon = {
                        if (isBottomDestination || currentRoute == null) {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("top_bar_menu_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = stringResource(R.string.cd_open_drawer)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { navController.navigateUp() },
                                modifier = Modifier.testTag("top_bar_back_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_navigate_back)
                                )
                            }
                        }
                    },
                    actions = {
                        StatusPill(
                            label = stringResource(R.string.demo_badge),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        if (!isBottomDestination && currentRoute != null) {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("top_bar_sub_menu_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = stringResource(R.string.cd_open_drawer)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    windowInsets = WindowInsets.navigationBars,
                    tonalElevation = 4.dp
                ) {
                    arenaBottomNavItems.forEach { dest ->
                        val selected = currentRoute == dest.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navigateTo(dest.route) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                    contentDescription = dest.label
                                )
                            },
                            label = { Text(dest.label, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ElectricBlue,
                                selectedTextColor = ElectricBlue,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag(dest.testTag)
                        )
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = if (lastSnackbarWasError) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        contentColor = if (lastSnackbarWasError) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        ) { innerPadding ->
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = ElectricPurple)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Loading Battle Arena Demo...", style = MaterialTheme.typography.titleMedium)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.TopCenter
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = ArenaRoutes.HOME,
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 760.dp)
                    ) {
                        composable(ArenaRoutes.HOME) {
                            HomeScreen(
                                uiState = uiState,
                                onNavigate = ::navigateTo,
                                onJoinTournament = { id -> navigateTo(ArenaRoutes.registerRoute(id)) }
                            )
                        }
                        composable(ArenaRoutes.MY_MATCHES) {
                            MyMatchesScreen(
                                uiState = uiState,
                                onNavigateToJoin = { navigateTo(ArenaRoutes.registerRoute()) },
                                onCopyMessage = { msg ->
                                    scope.launch {
                                        lastSnackbarWasError = false
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                            )
                        }
                        composable(ArenaRoutes.UPCOMING) {
                            UpcomingScreen(
                                uiState = uiState,
                                onJoinTournament = { id -> navigateTo(ArenaRoutes.registerRoute(id)) }
                            )
                        }
                        composable(ArenaRoutes.HISTORY) {
                            HistoryScreen(
                                uiState = uiState,
                                onNavigate = ::navigateTo
                            )
                        }
                        composable(ArenaRoutes.RESULTS) {
                            ResultsScreen(uiState = uiState)
                        }
                        composable(ArenaRoutes.WALLET) {
                            WalletScreen(
                                uiState = uiState,
                                onNavigate = ::navigateTo,
                                onClaimReward = viewModel::claimReward
                            )
                        }
                        composable(
                            route = ArenaRoutes.REGISTER_PATTERN,
                            arguments = listOf(
                                navArgument("tournamentId") {
                                    type = NavType.LongType
                                    defaultValue = -1L
                                }
                            )
                        ) { entry ->
                            val preselectedId = entry.arguments?.getLong("tournamentId") ?: -1L
                            RegistrationScreen(
                                uiState = uiState,
                                preselectedTournamentId = preselectedId,
                                onSubmitRegistration = viewModel::registerForTournament,
                                onGoToMyMatches = { navigateTo(ArenaRoutes.MY_MATCHES) }
                            )
                        }
                        composable(ArenaRoutes.GAME_HISTORY) {
                            GameHistoryScreen(uiState = uiState)
                        }
                        composable(ArenaRoutes.PROFILE) {
                            ProfileScreen(
                                uiState = uiState,
                                onSaveProfile = viewModel::updateProfile,
                                onNavigate = ::navigateTo
                            )
                        }
                        composable(ArenaRoutes.RULES) {
                            RulesScreen()
                        }
                        composable(ArenaRoutes.ACCOUNT) {
                            AccountScreen(
                                uiState = uiState,
                                onSetSignedIn = viewModel::setDemoSignedIn,
                                onResetLocalDemoData = viewModel::resetDemoData
                            )
                        }
                        composable(ArenaRoutes.SETTINGS) {
                            SettingsScreen(
                                uiState = uiState,
                                onToggleDarkTheme = viewModel::setDarkTheme,
                                onToggleMatchReminders = viewModel::setMatchReminders,
                                onToggleRoomAlerts = viewModel::setRoomAlerts,
                                onToggleAnnouncements = viewModel::setAnnouncementAlerts,
                                onSelectLanguage = viewModel::setLanguage,
                                onNavigate = ::navigateTo
                            )
                        }
                        composable(ArenaRoutes.REWARDS) {
                            RewardsScreen(
                                uiState = uiState,
                                onClaimReward = viewModel::claimReward
                            )
                        }
                        composable(ArenaRoutes.SUPPORT) {
                            SupportScreen(
                                uiState = uiState,
                                onSubmitTicket = viewModel::submitSupportTicket
                            )
                        }
                        composable(ArenaRoutes.ADMIN) {
                            AdminDashboardScreen(
                                uiState = uiState,
                                onUnlockAdmin = { key -> viewModel.unlockAdmin(key) },
                                onLockAdmin = viewModel::lockAdmin,
                                onSaveTournament = viewModel::adminSaveTournament,
                                onPublishRoomSchedule = viewModel::adminPublishRoomSchedule,
                                onUpdateRegistration = viewModel::adminUpdateRegistration,
                                onPublishResult = viewModel::adminPublishResult,
                                onReviewRewardClaim = viewModel::adminReviewRewardClaim,
                                onTogglePlayerSuspension = viewModel::adminTogglePlayerSuspension
                            )
                        }
                    }
                }
            }
        }
    }
}
