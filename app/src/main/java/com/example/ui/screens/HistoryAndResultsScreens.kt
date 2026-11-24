package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.MatchResultEntity
import com.example.data.local.TournamentStatus
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.EmptyArenaState
import com.example.ui.components.RegistrationStatusPill
import com.example.ui.components.StatusPill
import com.example.ui.components.formatDateTime
import com.example.ui.navigation.ArenaRoutes
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.TrophyBronze
import com.example.ui.theme.TrophyGold
import com.example.ui.theme.TrophySilver
import com.example.ui.viewmodel.ArenaUiState

/**
 * Screen 4: HISTORY — Past participation, completed matches, and previously joined tournaments.
 */
@Composable
fun HistoryScreen(
    uiState: ArenaUiState,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tournamentMap = remember(uiState.tournaments) { uiState.tournaments.associateBy { it.id } }
    val completedRegistrations = remember(uiState.myRegistrations, tournamentMap) {
        uiState.myRegistrations.filter { reg ->
            tournamentMap[reg.tournamentId]?.status == TournamentStatus.COMPLETED
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("history_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            DemoModeBanner("Past demo participation and completed tournament records saved locally on your device.")
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onNavigate(ArenaRoutes.GAME_HISTORY) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("history_to_game_stats"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Match-by-Match Stats")
                }
                Button(
                    onClick = { onNavigate(ArenaRoutes.RESULTS) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("history_to_results"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("View Standings")
                }
            }
        }

        if (completedRegistrations.isEmpty()) {
            item {
                EmptyArenaState(
                    icon = Icons.Filled.History,
                    title = "No Completed Tournaments",
                    message = "Once tournaments you joined finish, they will appear here with your final demo placement."
                )
            }
        } else {
            items(completedRegistrations, key = { it.id }) { reg ->
                val result = uiState.myMatchHistory.find { it.tournamentId == reg.tournamentId }
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
                            RegistrationStatusPill(reg.status)
                            StatusPill(
                                label = result?.let { "RANK #${it.rank}" } ?: "COMPLETED",
                                containerColor = TrophyGold.copy(alpha = 0.16f),
                                contentColor = TrophyGold
                            )
                        }
                        Text(reg.tournamentTitle, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${reg.mode} Mode • Joined ${formatDateTime(reg.registeredAtMillis)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (result != null) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Kills: ${result.kills}", style = MaterialTheme.typography.labelLarge, color = ElectricBlue)
                                Text("Total: ${result.totalPoints} pts", style = MaterialTheme.typography.labelLarge, color = ElectricPurple)
                                Text("+${result.demoPointsAwarded} Demo Pts", style = MaterialTheme.typography.labelLarge, color = StatusEmerald)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Screen 5: RESULTS — Verified demo results, winner rankings, kills, placement points & standings.
 * Read-only for ordinary players (only Admin Dashboard can publish results).
 */
@Composable
fun ResultsScreen(
    uiState: ArenaUiState,
    modifier: Modifier = Modifier
) {
    val groupedByTournament = remember(uiState.allResults) {
        uiState.allResults.groupBy { it.tournamentTitle }
    }
    val tournamentTitles = remember(groupedByTournament) { groupedByTournament.keys.toList() }
    var selectedTitle by remember(tournamentTitles) {
        mutableStateOf(tournamentTitles.firstOrNull().orEmpty())
    }

    val currentStandings = remember(groupedByTournament, selectedTitle) {
        (groupedByTournament[selectedTitle] ?: emptyList()).sortedBy { it.rank }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("results_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            DemoModeBanner("Verified DEMO results & standings. Read-only for players — only Admin can publish official demo results.")
        }

        if (tournamentTitles.isEmpty()) {
            item {
                EmptyArenaState(
                    icon = Icons.Filled.EmojiEvents,
                    title = "No Published Results Yet",
                    message = "Published demo standings and podium rankings will appear here."
                )
            }
        } else {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tournamentTitles) { title ->
                        FilterChip(
                            selected = selectedTitle == title,
                            onClick = { selectedTitle = title },
                            label = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        )
                    }
                }
            }

            // Podium highlight card
            item {
                val winner = currentStandings.firstOrNull()
                if (winner != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, TrophyGold.copy(alpha = 0.55f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Verified, contentDescription = "Verified demo result", tint = StatusEmerald, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("VERIFIED DEMO CHAMPION", style = MaterialTheme.typography.labelMedium, color = StatusEmerald)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(winner.playerNickname, style = MaterialTheme.typography.headlineSmall, color = TrophyGold)
                                    Text(
                                        text = "UID: ${winner.gameUid} • ${winner.mode}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${winner.totalPoints} PTS", style = MaterialTheme.typography.titleLarge, color = ElectricBlue)
                                    Text("+${winner.demoPointsAwarded} Demo Pts", style = MaterialTheme.typography.labelMedium, color = StatusEmerald)
                                }
                            }
                        }
                    }
                }
            }

            // Full Standings Rows
            items(currentStandings, key = { it.id }) { row ->
                StandingRowCard(row)
            }
        }
    }
}

@Composable
private fun StandingRowCard(result: MatchResultEntity) {
    val rankColor = when (result.rank) {
        1 -> TrophyGold
        2 -> TrophySilver
        3 -> TrophyBronze
        else -> ElectricBlue
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (result.isCurrentPlayer) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (result.isCurrentPlayer) ElectricPurple else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(rankColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("#${result.rank}", style = MaterialTheme.typography.labelLarge, color = rankColor)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(result.playerNickname, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            if (result.isCurrentPlayer) {
                                Spacer(modifier = Modifier.width(6.dp))
                                StatusPill("YOU", ElectricPurple.copy(alpha = 0.25f), ElectricPurple)
                            }
                        }
                        Text(
                            text = "UID: ${result.gameUid}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = "${result.totalPoints} pts",
                    style = MaterialTheme.typography.titleLarge,
                    color = rankColor
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Kills: ${result.kills}", style = MaterialTheme.typography.bodySmall, color = ElectricBlue)
                Text("Placement Pts: ${result.placementPoints}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Demo Award: +${result.demoPointsAwarded}", style = MaterialTheme.typography.bodySmall, color = StatusEmerald)
            }
        }
    }
}
