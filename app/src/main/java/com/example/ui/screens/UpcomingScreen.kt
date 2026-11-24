package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.TournamentEntity
import com.example.data.local.TournamentStatus
import com.example.ui.components.CountdownBadge
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.EmptyArenaState
import com.example.ui.components.EsportsBannerCanvas
import com.example.ui.components.SlotProgressBar
import com.example.ui.components.StatusPill
import com.example.ui.components.TournamentStatusPill
import com.example.ui.components.formatDateTime
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.TrophyGold
import com.example.ui.viewmodel.ArenaUiState

@Composable
fun UpcomingScreen(
    uiState: ArenaUiState,
    onJoinTournament: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var modeFilter by remember { mutableStateOf("All") }
    var detailTournament by remember { mutableStateOf<TournamentEntity?>(null) }

    val openTournaments = remember(uiState.tournaments, modeFilter) {
        uiState.tournaments.filter { t ->
            t.status != TournamentStatus.COMPLETED &&
                (modeFilter == "All" || t.mode.equals(modeFilter, ignoreCase = true))
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("upcoming_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            DemoModeBanner("All upcoming tournaments are FREE DEMO brackets with sample Demo Points reward pools.")
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Solo", "Duo", "Squad").forEach { mode ->
                    FilterChip(
                        selected = modeFilter == mode,
                        onClick = { modeFilter = mode },
                        label = { Text(mode) },
                        modifier = Modifier.testTag("upcoming_filter_${mode.lowercase()}")
                    )
                }
            }
        }

        if (openTournaments.isEmpty()) {
            item {
                EmptyArenaState(
                    icon = Icons.Filled.Schedule,
                    title = "No Upcoming Brackets",
                    message = "No open $modeFilter demo tournaments right now. Switch filters or create one from Admin Dashboard."
                )
            }
        } else {
            items(openTournaments, key = { it.id }) { tournament ->
                val alreadyJoined = uiState.myRegistrations.any { it.tournamentId == tournament.id }
                UpcomingTournamentCard(
                    tournament = tournament,
                    alreadyJoined = alreadyJoined,
                    onViewDetails = { detailTournament = tournament },
                    onJoinClick = { onJoinTournament(tournament.id) }
                )
            }
        }
    }

    detailTournament?.let { t ->
        AlertDialog(
            onDismissRequest = { detailTournament = null },
            title = { Text(t.title, style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Game: ${t.gameTitle}", style = MaterialTheme.typography.titleSmall, color = ElectricBlue)
                    Text("Mode: ${t.mode} (Team size: ${t.teamSize})", style = MaterialTheme.typography.bodyMedium)
                    Text("Arena Map: ${t.mapName}", style = MaterialTheme.typography.bodyMedium)
                    Text("Scheduled: ${formatDateTime(t.startTimeMillis)}", style = MaterialTheme.typography.bodyMedium)
                    Text("Demo Reward Pool: ${t.demoRewardPoolPoints} DEMO PTS (Not real money)", style = MaterialTheme.typography.bodyMedium, color = TrophyGold)
                    Text("Entry Fee: FREE (Local Demo)", style = MaterialTheme.typography.bodyMedium, color = StatusEmerald)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Rules & Match Info", style = MaterialTheme.typography.titleSmall)
                    Text(t.rulesSummary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                val joined = uiState.myRegistrations.any { it.tournamentId == t.id }
                Button(
                    onClick = {
                        detailTournament = null
                        if (!joined) onJoinTournament(t.id)
                    },
                    enabled = !joined && t.filledSlots < t.maxSlots
                ) {
                    Text(if (joined) "Already Joined" else "Register Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { detailTournament = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun UpcomingTournamentCard(
    tournament: TournamentEntity,
    alreadyJoined: Boolean,
    onViewDetails: () -> Unit,
    onJoinClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                EsportsBannerCanvas(
                    style = tournament.bannerStyle,
                    contentDescriptionText = "${tournament.title} banner"
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TournamentStatusPill(tournament.status)
                        StatusPill(
                            label = "DEMO POOL: ${tournament.demoRewardPoolPoints} PTS",
                            containerColor = Color(0xCC0A0C12),
                            contentColor = TrophyGold
                        )
                    }
                    Column {
                        Text(tournament.gameTitle, style = MaterialTheme.typography.labelMedium, color = ElectricBlue)
                        Text(tournament.title, style = MaterialTheme.typography.titleLarge, color = Color.White)
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CountdownBadge(tournament.startTimeMillis, tournament.status)
                    Text(
                        text = formatDateTime(tournament.startTimeMillis),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Groups, contentDescription = "Team size", tint = ElectricPurple, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("${tournament.mode} (${tournament.teamSize}P)", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Map, contentDescription = "Map", tint = ElectricBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(tournament.mapName, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Text(
                    text = tournament.rulesSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                SlotProgressBar(filled = tournament.filledSlots, max = tournament.maxSlots)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewDetails,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("upcoming_details_${tournament.id}"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Rules & Info")
                    }
                    Button(
                        onClick = onJoinClick,
                        enabled = !alreadyJoined && tournament.filledSlots < tournament.maxSlots,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("upcoming_join_${tournament.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple)
                    ) {
                        Text(
                            when {
                                alreadyJoined -> "Joined"
                                tournament.filledSlots >= tournament.maxSlots -> "Full"
                                else -> "Join Free"
                            }
                        )
                    }
                }
            }
        }
    }
}
