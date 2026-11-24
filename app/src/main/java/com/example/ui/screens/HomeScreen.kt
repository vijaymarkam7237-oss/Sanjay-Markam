package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.AnnouncementEntity
import com.example.data.local.TournamentEntity
import com.example.data.local.TournamentStatus
import com.example.ui.components.ArenaSectionHeader
import com.example.ui.components.CountdownBadge
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.EsportsBannerCanvas
import com.example.ui.components.SlotProgressBar
import com.example.ui.components.StatusPill
import com.example.ui.components.TournamentStatusPill
import com.example.ui.components.formatDateTime
import com.example.ui.navigation.ArenaRoutes
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.TrophyGold
import com.example.ui.viewmodel.ArenaUiState

@Composable
fun HomeScreen(
    uiState: ArenaUiState,
    onNavigate: (String) -> Unit,
    onJoinTournament: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val featuredTournaments = uiState.tournaments.filter { it.isFeatured || it.status != TournamentStatus.COMPLETED }
    val upcomingMatches = uiState.myRegistrations.take(2)
    val recentResults = uiState.allResults.filter { it.rank <= 2 }.take(3)
    val profile = uiState.profile

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            DemoModeBanner()
        }

        // Hero Esports Banner & Demo Points Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, ElectricPurple.copy(alpha = 0.55f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(215.dp)
                ) {
                    EsportsBannerCanvas(
                        style = "CYBER",
                        contentDescriptionText = "Battle Arena Tournaments featured stadium banner"
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusPill(
                                label = "SEASON 1 • LOCAL DEMO",
                                containerColor = ElectricPurple.copy(alpha = 0.28f),
                                contentColor = Color.White
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xCC0D101B),
                                border = BorderStroke(1.dp, TrophyGold.copy(alpha = 0.55f)),
                                modifier = Modifier
                                    .clickable { onNavigate(ArenaRoutes.WALLET) }
                                    .testTag("home_wallet_pill")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AccountBalanceWallet,
                                        contentDescription = "Demo points balance",
                                        tint = TrophyGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${profile?.demoPointsBalance ?: 0} DEMO PTS",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TrophyGold
                                    )
                                }
                            }
                        }

                        Column {
                            Text(
                                text = "Welcome back, ${profile?.nickname ?: "Challenger"}",
                                style = MaterialTheme.typography.labelLarge,
                                color = ElectricBlue
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Battle Arena Tournaments",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Free demo brackets for Solo, Duo & Squad play. Zero entry fees.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFD6E0F5)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { onNavigate(ArenaRoutes.registerRoute()) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricPurple,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("home_join_cta")
                            ) {
                                Icon(Icons.Filled.MilitaryTech, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Join Demo Match")
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xB3121624),
                                border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.55f)),
                                modifier = Modifier
                                    .clickable { onNavigate(ArenaRoutes.UPCOMING) }
                                    .testTag("home_browse_upcoming")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Browse All", style = MaterialTheme.typography.labelLarge, color = ElectricBlue)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Browse upcoming",
                                        tint = ElectricBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Actions Grid
        item {
            ArenaSectionHeader(title = "Quick Actions", subtitle = "Jump straight into arena modules")
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Upcoming",
                        subtitle = "${uiState.tournaments.count { it.status == TournamentStatus.UPCOMING }} Open",
                        icon = Icons.Filled.Schedule,
                        accent = ElectricBlue,
                        testTag = "quick_upcoming",
                        onClick = { onNavigate(ArenaRoutes.UPCOMING) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "Results",
                        subtitle = "Demo Standings",
                        icon = Icons.Filled.EmojiEvents,
                        accent = TrophyGold,
                        testTag = "quick_results",
                        onClick = { onNavigate(ArenaRoutes.RESULTS) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "Rewards",
                        subtitle = "${uiState.rewardItems.size} Items",
                        icon = Icons.Filled.CardGiftcard,
                        accent = ElectricPurple,
                        testTag = "quick_rewards",
                        onClick = { onNavigate(ArenaRoutes.REWARDS) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Game Stats",
                        subtitle = "${uiState.totalMatchesPlayed} Matches",
                        icon = Icons.Filled.SportsEsports,
                        accent = StatusEmerald,
                        testTag = "quick_game_history",
                        onClick = { onNavigate(ArenaRoutes.GAME_HISTORY) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "Rules",
                        subtitle = "Fair Play",
                        icon = Icons.Filled.Gavel,
                        accent = ElectricBlue,
                        testTag = "quick_rules",
                        onClick = { onNavigate(ArenaRoutes.RULES) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "Admin",
                        subtitle = "Organizer",
                        icon = Icons.Filled.AdminPanelSettings,
                        accent = ElectricPurple,
                        testTag = "quick_admin",
                        onClick = { onNavigate(ArenaRoutes.ADMIN) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Featured Tournaments Horizontal Carousel
        item {
            ArenaSectionHeader(
                title = "Featured Demo Brackets",
                subtitle = "Free practice & demo tournaments",
                actionLabel = "See All",
                onActionClick = { onNavigate(ArenaRoutes.UPCOMING) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(featuredTournaments, key = { it.id }) { tournament ->
                    val alreadyJoined = uiState.myRegistrations.any { it.tournamentId == tournament.id }
                    FeaturedTournamentCard(
                        tournament = tournament,
                        alreadyJoined = alreadyJoined,
                        onJoinClick = { onJoinTournament(tournament.id) }
                    )
                }
            }
        }

        // My Registered Upcoming Matches Preview
        item {
            ArenaSectionHeader(
                title = "Your Active Matches",
                subtitle = "Room IDs unlock once officially published",
                actionLabel = "My Matches",
                onActionClick = { onNavigate(ArenaRoutes.MY_MATCHES) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (upcomingMatches.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "No registered matches yet. Tap Join Demo Match above to enter a bracket!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    upcomingMatches.forEach { reg ->
                        val t = uiState.tournaments.find { it.id == reg.tournamentId }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigate(ArenaRoutes.MY_MATCHES) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = reg.tournamentTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${reg.mode} • ${t?.let { formatDateTime(it.startTimeMillis) } ?: "Scheduled"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                StatusPill(
                                    label = reg.status.replace('_', ' '),
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        // Announcements
        item {
            ArenaSectionHeader(
                title = "Arena Announcements",
                subtitle = "Local demo notices & schedule bulletins"
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                uiState.announcements.forEach { announcement ->
                    AnnouncementCard(announcement)
                }
            }
        }

        // Recent Demo Results
        item {
            ArenaSectionHeader(
                title = "Recent Verified Demo Results",
                subtitle = "Top podium finishers from completed brackets",
                actionLabel = "Full Standings",
                onActionClick = { onNavigate(ArenaRoutes.RESULTS) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                recentResults.forEach { res ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(ArenaRoutes.RESULTS) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (res.rank == 1) TrophyGold.copy(alpha = 0.2f)
                                            else ElectricBlue.copy(alpha = 0.16f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "#${res.rank}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (res.rank == 1) TrophyGold else ElectricBlue
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(res.playerNickname, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = "${res.tournamentTitle} • ${res.kills} Kills",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Text(
                                text = "${res.totalPoints} pts",
                                style = MaterialTheme.typography.titleMedium,
                                color = ElectricPurple
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FeaturedTournamentCard(
    tournament: TournamentEntity,
    alreadyJoined: Boolean,
    onJoinClick: () -> Unit
) {
    Card(
        modifier = Modifier.width(285.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
            ) {
                EsportsBannerCanvas(
                    style = tournament.bannerStyle,
                    contentDescriptionText = "${tournament.title} artwork"
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TournamentStatusPill(tournament.status)
                        StatusPill(
                            label = "FREE DEMO",
                            containerColor = Color(0xB30A0C12),
                            contentColor = StatusEmerald
                        )
                    }
                    Text(
                        text = tournament.gameTitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricBlue
                    )
                }
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = tournament.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CountdownBadge(tournament.startTimeMillis, tournament.status)
                    Text(
                        text = "${tournament.mode} • ${tournament.demoRewardPoolPoints} Demo Pts",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                SlotProgressBar(filled = tournament.filledSlots, max = tournament.maxSlots)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onJoinClick,
                    enabled = !alreadyJoined && tournament.filledSlots < tournament.maxSlots && tournament.status != TournamentStatus.COMPLETED,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("featured_join_${tournament.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (alreadyJoined) MaterialTheme.colorScheme.surfaceVariant else ElectricPurple
                    )
                ) {
                    Text(
                        text = when {
                            alreadyJoined -> "Registered (Demo)"
                            tournament.filledSlots >= tournament.maxSlots -> "Slots Full"
                            else -> "Join Free Demo"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnnouncementCard(item: AnnouncementEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(ElectricPurple.copy(alpha = 0.25f), ElectricBlue.copy(alpha = 0.25f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Campaign,
                    contentDescription = "Announcement",
                    tint = ElectricBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusPill(
                        label = item.tag,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = formatDateTime(item.timestampMillis),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
