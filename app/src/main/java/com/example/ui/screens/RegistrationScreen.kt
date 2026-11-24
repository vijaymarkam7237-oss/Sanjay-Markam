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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.TournamentStatus
import com.example.ui.components.ArenaConfirmDialog
import com.example.ui.components.ArenaSectionHeader
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.EmptyArenaState
import com.example.ui.components.SlotProgressBar
import com.example.ui.components.StatusPill
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.StatusCrimson
import com.example.ui.theme.StatusEmerald
import com.example.ui.viewmodel.ArenaUiState

/**
 * Screen 7: JOINING / REGISTRATION — Solo, Duo, Squad options, nickname, game UID,
 * tournament selection, form validation, confirmation dialog, and duplicate prevention.
 */
@Composable
fun RegistrationScreen(
    uiState: ArenaUiState,
    preselectedTournamentId: Long,
    onSubmitRegistration: (Long, String, String, String, String, () -> Unit) -> Unit,
    onGoToMyMatches: () -> Unit,
    modifier: Modifier = Modifier
) {
    val openTournaments = remember(uiState.tournaments) {
        uiState.tournaments.filter { it.status != TournamentStatus.COMPLETED }
    }

    var selectedTournamentId by remember(preselectedTournamentId, openTournaments) {
        mutableLongStateOf(
            when {
                preselectedTournamentId > 0L && openTournaments.any { it.id == preselectedTournamentId } ->
                    preselectedTournamentId
                else -> openTournaments.firstOrNull()?.id ?: -1L
            }
        )
    }

    val selectedTournament = openTournaments.find { it.id == selectedTournamentId }

    var selectedMode by remember { mutableStateOf("Solo") }
    var nickname by remember { mutableStateOf(uiState.profile?.nickname.orEmpty()) }
    var gameUid by remember { mutableStateOf(uiState.profile?.gameUid.orEmpty()) }
    var teammates by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessBanner by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.profile) {
        if (nickname.isBlank()) nickname = uiState.profile?.nickname.orEmpty()
        if (gameUid.isBlank()) gameUid = uiState.profile?.gameUid.orEmpty()
    }

    LaunchedEffect(selectedTournament?.id) {
        selectedTournament?.let { selectedMode = it.mode }
    }

    val isDuplicate = remember(uiState.myRegistrations, selectedTournamentId, gameUid) {
        uiState.myRegistrations.any {
            it.tournamentId == selectedTournamentId &&
                it.gameUid.equals(gameUid.trim(), ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("registration_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DemoModeBanner("FREE DEMO REGISTRATION — No entry fee required. Duplicate registration for the same tournament is automatically blocked.")
        }

        if (showSuccessBanner) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = StatusEmerald.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, StatusEmerald.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = "Confirmed", tint = StatusEmerald)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Demo Registration Confirmed!", style = MaterialTheme.typography.titleMedium, color = StatusEmerald)
                        }
                        Text(
                            text = "Your entry is saved locally with Pending Approval status. Check My Matches or approve it from Admin Dashboard to preview Room Details.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        OutlinedButton(
                            onClick = onGoToMyMatches,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("reg_go_my_matches")
                        ) {
                            Text("Open My Matches")
                        }
                    }
                }
            }
        }

        if (openTournaments.isEmpty()) {
            item {
                EmptyArenaState(
                    icon = Icons.Filled.MilitaryTech,
                    title = "No Open Demo Tournaments",
                    message = "Create an upcoming demo tournament in Admin Dashboard to test registration."
                )
            }
        } else {
            // Step 1: Select Tournament
            item {
                ArenaSectionHeader(title = "1. Select Demo Tournament", subtitle = "Choose an open bracket")
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    openTournaments.forEach { t ->
                        val isSelected = t.id == selectedTournamentId
                        val joined = uiState.myRegistrations.any { it.tournamentId == t.id }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTournamentId = t.id
                                    validationError = null
                                    showSuccessBanner = false
                                }
                                .testTag("reg_select_tournament_${t.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) ElectricPurple else MaterialTheme.colorScheme.outlineVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            selectedTournamentId = t.id
                                            validationError = null
                                            showSuccessBanner = false
                                        }
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(t.title, style = MaterialTheme.typography.titleMedium)
                                        Text(
                                            text = "${t.gameTitle} • ${t.mode} • ${t.demoRewardPoolPoints} Demo Pts",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (joined) {
                                        StatusPill("JOINED", StatusEmerald.copy(alpha = 0.2f), StatusEmerald)
                                    }
                                }
                                SlotProgressBar(filled = t.filledSlots, max = t.maxSlots)
                            }
                        }
                    }
                }
            }

            // Step 2: Mode Selection (Solo / Duo / Squad)
            item {
                ArenaSectionHeader(title = "2. Entry Format", subtitle = "Solo, Duo, or Squad")
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("Solo", "Duo", "Squad").forEach { mode ->
                        FilterChip(
                            selected = selectedMode == mode,
                            onClick = { selectedMode = mode },
                            label = { Text(mode) },
                            leadingIcon = {
                                Icon(Icons.Filled.Groups, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            modifier = Modifier.testTag("reg_mode_${mode.lowercase()}")
                        )
                    }
                }
            }

            // Step 3: Player Details Form
            item {
                ArenaSectionHeader(title = "3. Player Details", subtitle = "Matches your in-game identity")
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = nickname,
                            onValueChange = {
                                nickname = it
                                validationError = null
                            },
                            label = { Text("In-Game Nickname *") },
                            placeholder = { Text("e.g., NovaValkyrie") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_nickname_input")
                        )

                        OutlinedTextField(
                            value = gameUid,
                            onValueChange = {
                                gameUid = it
                                validationError = null
                            },
                            label = { Text("Game UID *") },
                            placeholder = { Text("e.g., BA-849201") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_uid_input")
                        )

                        if (selectedMode != "Solo") {
                            OutlinedTextField(
                                value = teammates,
                                onValueChange = {
                                    teammates = it
                                    validationError = null
                                },
                                label = {
                                    Text(
                                        if (selectedMode == "Duo") "Partner Nickname (Required for Duo) *"
                                        else "Squad Teammates (Comma-separated) *"
                                    )
                                },
                                placeholder = {
                                    Text(
                                        if (selectedMode == "Duo") "e.g., ZephyrK"
                                        else "e.g., ZephyrK, PulseByte, IronMantis"
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_teammates_input")
                            )
                        }

                        if (isDuplicate) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = StatusCrimson.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, StatusCrimson.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.ErrorOutline, contentDescription = "Duplicate warning", tint = StatusCrimson)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "You are already registered for this tournament with UID ${gameUid.trim().uppercase()}. Duplicate entries are prevented.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = StatusCrimson
                                    )
                                }
                            }
                        }

                        validationError?.let { err ->
                            Text(
                                text = err,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Entry Fee", style = MaterialTheme.typography.bodyMedium)
                            Text("FREE (Demo Mode)", style = MaterialTheme.typography.titleSmall, color = StatusEmerald)
                        }

                        Button(
                            onClick = {
                                when {
                                    selectedTournament == null ->
                                        validationError = "Please select a tournament."
                                    selectedTournament.filledSlots >= selectedTournament.maxSlots ->
                                        validationError = "This bracket has reached maximum slots."
                                    isDuplicate ->
                                        validationError = "Duplicate registration is not allowed for the same tournament."
                                    nickname.trim().length < 3 ->
                                        validationError = "Nickname must be at least 3 characters."
                                    gameUid.trim().length < 5 ->
                                        validationError = "Game UID must be at least 5 characters."
                                    selectedMode != "Solo" && teammates.trim().length < 3 ->
                                        validationError = "Please enter your teammate nickname(s) for $selectedMode mode."
                                    else -> {
                                        validationError = null
                                        showConfirmDialog = true
                                    }
                                }
                            },
                            enabled = !isDuplicate && (selectedTournament?.filledSlots ?: 0) < (selectedTournament?.maxSlots ?: 1),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("reg_submit_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple)
                        ) {
                            Text("Confirm Free Demo Registration")
                        }
                    }
                }
            }
        }
    }

    if (showConfirmDialog && selectedTournament != null) {
        ArenaConfirmDialog(
            title = "Confirm Tournament Entry",
            body = "Register ${nickname.trim()} (${gameUid.trim().uppercase()}) for \"${selectedTournament.title}\" in $selectedMode mode? Entry is 100% free (Local Demo).",
            confirmLabel = "Submit Registration",
            onConfirm = {
                showConfirmDialog = false
                onSubmitRegistration(
                    selectedTournament.id,
                    nickname,
                    gameUid,
                    selectedMode,
                    teammates
                ) {
                    showSuccessBanner = true
                    teammates = ""
                }
            },
            onDismiss = { showConfirmDialog = false }
        )
    }
}
