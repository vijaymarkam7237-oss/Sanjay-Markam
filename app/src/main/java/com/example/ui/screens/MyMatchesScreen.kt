package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.RegistrationEntity
import com.example.data.local.RegistrationStatus
import com.example.data.local.TournamentEntity
import com.example.data.local.TournamentStatus
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.EmptyArenaState
import com.example.ui.components.RegistrationStatusPill
import com.example.ui.components.TournamentStatusPill
import com.example.ui.components.formatDateTime
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusEmerald
import com.example.ui.viewmodel.ArenaUiState

@Composable
fun MyMatchesScreen(
    uiState: ArenaUiState,
    onNavigateToJoin: () -> Unit,
    onCopyMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    val tournamentMap = remember(uiState.tournaments) { uiState.tournaments.associateBy { it.id } }

    val filtered = remember(uiState.myRegistrations, tournamentMap, selectedFilter) {
        uiState.myRegistrations.filter { reg ->
            val t = tournamentMap[reg.tournamentId]
            when (selectedFilter) {
                "ACTIVE" -> t == null || t.status != TournamentStatus.COMPLETED
                "COMPLETED" -> t?.status == TournamentStatus.COMPLETED
                else -> true
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("my_matches_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            DemoModeBanner("Registered demo matches. Official Room ID & Password appear only when Approved and published by Admin.")
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("ALL" to "All (${uiState.myRegistrations.size})", "ACTIVE" to "Active", "COMPLETED" to "Completed")
                    .forEach { (key, label) ->
                        FilterChip(
                            selected = selectedFilter == key,
                            onClick = { selectedFilter = key },
                            label = { Text(label) },
                            modifier = Modifier.testTag("matches_filter_${key.lowercase()}")
                        )
                    }
            }
        }

        if (filtered.isEmpty()) {
            item {
                EmptyArenaState(
                    icon = Icons.Filled.SportsEsports,
                    title = "No Matches Found",
                    message = "You have not registered for any demo matches in this category yet.",
                    actionLabel = "Join a Tournament",
                    onAction = onNavigateToJoin
                )
            }
        } else {
            items(filtered, key = { it.id }) { registration ->
                MyMatchCard(
                    registration = registration,
                    tournament = tournamentMap[registration.tournamentId],
                    onCopyMessage = onCopyMessage
                )
            }
        }
    }
}

@Composable
private fun MyMatchCard(
    registration: RegistrationEntity,
    tournament: TournamentEntity?,
    onCopyMessage: (String) -> Unit
) {
    val clipboard = LocalClipboardManager.current
    val canViewRoom = registration.status == RegistrationStatus.APPROVED &&
        tournament != null &&
        tournament.roomPublished &&
        tournament.roomId.isNotBlank()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RegistrationStatusPill(registration.status)
                if (tournament != null) {
                    TournamentStatusPill(tournament.status)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = registration.tournamentTitle,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = "Scheduled time",
                    tint = ElectricBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${registration.mode} Mode • ${tournament?.let { formatDateTime(it.startTimeMillis) } ?: "Scheduled"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Registered as: ${registration.playerNickname} (UID: ${registration.gameUid})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (registration.teammateNames.isNotBlank()) {
                Text(
                    text = "Teammates: ${registration.teammateNames}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (canViewRoom && tournament != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = StatusEmerald.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, StatusEmerald.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Key,
                                contentDescription = "Published room details",
                                tint = StatusEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "OFFICIAL DEMO ROOM DETAILS",
                                style = MaterialTheme.typography.labelMedium,
                                color = StatusEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Room ID: ${tournament.roomId}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Password: ${tournament.roomPassword}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(
                                onClick = {
                                    clipboard.setText(
                                        AnnotatedString("Room ID: ${tournament.roomId} | Pass: ${tournament.roomPassword}")
                                    )
                                    onCopyMessage("Demo room credentials copied to clipboard.")
                                },
                                modifier = Modifier.testTag("copy_room_${registration.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy room details",
                                    tint = ElectricBlue
                                )
                            }
                        }
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Room details locked",
                            tint = StatusAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when {
                                registration.status == RegistrationStatus.REJECTED ->
                                    "Registration rejected. Room details are unavailable."
                                registration.status != RegistrationStatus.APPROVED ->
                                    "Room ID & Password unlock after Admin approves your registration."
                                else ->
                                    "Approved! Room details will appear here once officially published by Admin."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
