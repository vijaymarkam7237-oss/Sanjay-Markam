package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.ArenaSectionHeader
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.EmptyArenaState
import com.example.ui.components.PlayerEmblemAvatar
import com.example.ui.components.StatTile
import com.example.ui.components.StatusPill
import com.example.ui.components.formatDateTime
import com.example.ui.navigation.ArenaRoutes
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.TrophyGold
import com.example.ui.viewmodel.ArenaUiState

/**
 * Screen 8: GAME HISTORY — Match-by-match player statistics, placements, kills, points, and past records.
 */
@Composable
fun GameHistoryScreen(
    uiState: ArenaUiState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("game_history_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            DemoModeBanner("Your personal match-by-match demo stats and verified placement breakdowns.")
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatTile(
                    label = "Matches",
                    value = "${uiState.totalMatchesPlayed}",
                    icon = Icons.Filled.SportsEsports,
                    accentColor = ElectricBlue,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = "Demo Wins",
                    value = "${uiState.totalWins}",
                    icon = Icons.Filled.EmojiEvents,
                    accentColor = TrophyGold,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatTile(
                    label = "Total Kills",
                    value = "${uiState.totalKills}",
                    icon = Icons.Filled.Whatshot,
                    accentColor = StatusAmber,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = "Avg Points",
                    value = "${uiState.averagePoints}",
                    icon = Icons.Filled.Timeline,
                    accentColor = ElectricPurple,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            ArenaSectionHeader(title = "Match-by-Match Records", subtitle = "Newest demo records first")
        }

        if (uiState.myMatchHistory.isEmpty()) {
            item {
                EmptyArenaState(
                    icon = Icons.Filled.Timeline,
                    title = "No Match Records Yet",
                    message = "When Admin publishes verified demo results for your Game UID, your stats appear here."
                )
            }
        } else {
            items(uiState.myMatchHistory, key = { it.id }) { match ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusPill(
                                label = "PLACE #${match.rank}",
                                containerColor = if (match.rank == 1) TrophyGold.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.16f),
                                contentColor = if (match.rank == 1) TrophyGold else ElectricBlue
                            )
                            Text(
                                text = formatDateTime(match.completedAtMillis),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(match.tournamentTitle, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${match.mode} Mode • UID: ${match.gameUid}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Kills", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${match.kills}", style = MaterialTheme.typography.titleMedium, color = ElectricBlue)
                            }
                            Column {
                                Text("Placement", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${match.placementPoints} pts", style = MaterialTheme.typography.titleMedium)
                            }
                            Column {
                                Text("Total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${match.totalPoints} pts", style = MaterialTheme.typography.titleMedium, color = ElectricPurple)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Demo Award", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("+${match.demoPointsAwarded}", style = MaterialTheme.typography.titleMedium, color = StatusEmerald)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Screen 9: PROFILE — Player avatar, nickname, game UID, stats, participation count, editable basic details.
 */
@Composable
fun ProfileScreen(
    uiState: ArenaUiState,
    onSaveProfile: (String, String, String, String, String, String) -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = uiState.profile
    var isEditing by remember { mutableStateOf(false) }

    var nickname by remember { mutableStateOf(profile?.nickname.orEmpty()) }
    var gameUid by remember { mutableStateOf(profile?.gameUid.orEmpty()) }
    var bio by remember { mutableStateOf(profile?.bio.orEmpty()) }
    var region by remember { mutableStateOf(profile?.region ?: "South Asia") }
    var preferredMode by remember { mutableStateOf(profile?.preferredMode ?: "Squad") }
    var avatarKey by remember { mutableStateOf(profile?.avatarKey ?: "CYBER_KNIGHT") }

    LaunchedEffect(profile) {
        if (!isEditing && profile != null) {
            nickname = profile.nickname
            gameUid = profile.gameUid
            bio = profile.bio
            region = profile.region
            preferredMode = profile.preferredMode
            avatarKey = profile.avatarKey
        }
    }

    val avatarOptions = listOf(
        "CYBER_KNIGHT" to "Cyber",
        "PLASMA_WOLF" to "Plasma",
        "SOLAR_TITAN" to "Solar",
        "EMERALD_GHOST" to "Phantom"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, ElectricPurple.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PlayerEmblemAvatar(avatarKey = avatarKey, size = 84.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = profile?.nickname ?: "Challenger",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    StatusPill(
                        label = "UID: ${profile?.gameUid ?: "DEMO"} • ${profile?.region ?: "Global"}",
                        containerColor = ElectricBlue.copy(alpha = 0.16f),
                        contentColor = ElectricBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = profile?.bio.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = { isEditing = !isEditing },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("profile_edit_toggle")
                    ) {
                        Text(if (isEditing) "Cancel Editing" else "Edit Basic Details")
                    }
                }
            }
        }

        // Participation & Statistics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatTile(
                    label = "Joined",
                    value = "${uiState.myRegistrations.size}",
                    icon = Icons.Filled.MilitaryTech,
                    accentColor = ElectricPurple,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = "Wins",
                    value = "${uiState.totalWins}",
                    icon = Icons.Filled.EmojiEvents,
                    accentColor = TrophyGold,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = "Kills",
                    value = "${uiState.totalKills}",
                    icon = Icons.Filled.Whatshot,
                    accentColor = ElectricBlue,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (isEditing) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Edit Demo Profile Details", style = MaterialTheme.typography.titleMedium)

                        Text("Choose Emblem Style", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            avatarOptions.forEach { (key, label) ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable { avatarKey = key }
                                ) {
                                    PlayerEmblemAvatar(avatarKey = key, size = 48.dp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (avatarKey == key) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (avatarKey == key) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = nickname,
                            onValueChange = { nickname = it },
                            label = { Text("Player Nickname") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_nickname_input")
                        )

                        OutlinedTextField(
                            value = gameUid,
                            onValueChange = { gameUid = it },
                            label = { Text("Game UID") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_uid_input")
                        )

                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            label = { Text("Player Bio") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Preferred Mode", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Solo", "Duo", "Squad").forEach { mode ->
                                FilterChip(
                                    selected = preferredMode == mode,
                                    onClick = { preferredMode = mode },
                                    label = { Text(mode) }
                                )
                            }
                        }

                        Text("Region", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("South Asia", "SEA", "Europe", "Americas").forEach { reg ->
                                FilterChip(
                                    selected = region == reg,
                                    onClick = { region = reg },
                                    label = { Text(reg) }
                                )
                            }
                        }

                        Button(
                            onClick = {
                                onSaveProfile(nickname, gameUid, bio, region, preferredMode, avatarKey)
                                isEditing = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_save_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save Profile Locally")
                        }
                    }
                }
            }
        }

        // Quick Links from Profile
        item {
            ArenaSectionHeader(title = "Account & More", subtitle = "Manage your local demo experience")
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ProfileLinkRow("Game History & Stats", "Match-by-match kills & placements", Icons.Filled.Timeline, "profile_link_game_history") {
                    onNavigate(ArenaRoutes.GAME_HISTORY)
                }
                ProfileLinkRow("Account Status", "Local demo sign-in & deletion info", Icons.Filled.ManageAccounts, "profile_link_account") {
                    onNavigate(ArenaRoutes.ACCOUNT)
                }
                ProfileLinkRow("App Settings", "Theme, notifications & privacy", Icons.Filled.Settings, "profile_link_settings") {
                    onNavigate(ArenaRoutes.SETTINGS)
                }
                ProfileLinkRow("Rules & Fair Play", "Tournament procedures & disputes", Icons.Filled.Gavel, "profile_link_rules") {
                    onNavigate(ArenaRoutes.RULES)
                }
                ProfileLinkRow("Support & FAQ", "Help guide & local problem report", Icons.Filled.SupportAgent, "profile_link_support") {
                    onNavigate(ArenaRoutes.SUPPORT)
                }
            }
        }
    }
}

@Composable
private fun ProfileLinkRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = title, tint = ElectricBlue, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
