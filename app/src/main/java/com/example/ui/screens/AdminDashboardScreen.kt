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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.ManagedPlayerEntity
import com.example.data.local.RegistrationStatus
import com.example.data.local.RewardClaimStatus
import com.example.data.local.TournamentEntity
import com.example.data.local.TournamentStatus
import com.example.data.repository.ArenaRepository
import com.example.ui.components.ArenaConfirmDialog
import com.example.ui.components.ArenaSectionHeader
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.RegistrationStatusPill
import com.example.ui.components.RewardClaimStatusPill
import com.example.ui.components.StatusPill
import com.example.ui.components.TournamentStatusPill
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusCrimson
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.TrophyGold
import com.example.ui.viewmodel.ArenaUiState

/**
 * Screen 15: ADMIN DASHBOARD — Protected organizer panel for creating/editing demo tournaments,
 * approving registrations, scheduling & publishing room credentials, publishing verified results,
 * reviewing reward claims, and managing demo players.
 */
@Composable
fun AdminDashboardScreen(
    uiState: ArenaUiState,
    onUnlockAdmin: (String) -> Unit,
    onLockAdmin: () -> Unit,
    onSaveTournament: (Long?, String, String, String, Int, Int, String, String, Int, String, () -> Unit) -> Unit,
    onPublishRoomSchedule: (Long, String, String, Boolean, String) -> Unit,
    onUpdateRegistration: (Long, String) -> Unit,
    onPublishResult: (Long, String, String, Int, Int, Int, Int, () -> Unit) -> Unit,
    onReviewRewardClaim: (Long, String, String) -> Unit,
    onTogglePlayerSuspension: (ManagedPlayerEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!uiState.preferences.isAdminUnlocked) {
        AdminLockGate(onUnlockAdmin = onUnlockAdmin, modifier = modifier)
        return
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tournaments", "Approvals", "Results", "Rewards", "Players")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_dashboard_unlocked")
    ) {
        // Active Admin Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = ElectricPurple.copy(alpha = 0.18f),
            border = BorderStroke(1.dp, ElectricPurple.copy(alpha = 0.45f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = ElectricPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("DEMO ADMIN MODE ACTIVE", style = MaterialTheme.typography.labelLarge, color = ElectricPurple)
                        Text("Local device demo organizer controls", style = MaterialTheme.typography.bodySmall)
                    }
                }
                OutlinedButton(
                    onClick = onLockAdmin,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("admin_lock_btn")
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Lock")
                }
            }
        }

        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            edgePadding = 12.dp
        ) {
            tabs.forEachIndexed { idx, label ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = { Text(label) },
                    modifier = Modifier.testTag("admin_tab_${label.lowercase()}")
                )
            }
        }

        when (selectedTab) {
            0 -> AdminTournamentsTab(
                tournaments = uiState.tournaments,
                onSaveTournament = onSaveTournament,
                onPublishRoomSchedule = onPublishRoomSchedule
            )
            1 -> AdminApprovalsTab(
                uiState = uiState,
                onUpdateRegistration = onUpdateRegistration
            )
            2 -> AdminResultsTab(
                uiState = uiState,
                onPublishResult = onPublishResult
            )
            3 -> AdminRewardsTab(
                uiState = uiState,
                onReviewRewardClaim = onReviewRewardClaim
            )
            4 -> AdminPlayersTab(
                players = uiState.managedPlayers,
                onTogglePlayerSuspension = onTogglePlayerSuspension
            )
        }
    }
}

@Composable
private fun AdminLockGate(
    onUnlockAdmin: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var passkeyInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_lock_gate"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DemoModeBanner("Role Separation Enforced — Ordinary players cannot edit results, award demo points, or approve reward claims.")
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, ElectricPurple.copy(alpha = 0.55f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Lock, contentDescription = "Admin locked", tint = ElectricPurple, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Admin Dashboard Locked", style = MaterialTheme.typography.headlineSmall)
                    }
                    Text(
                        text = "Enter the local demo organizer passkey to preview Admin tournament creation, registration approvals, room publishing, result publishing, player roster management, and reward claim reviews.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ElectricBlue.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "Demo Testing Passkey: ${ArenaRepository.DEMO_ADMIN_PASSKEY}\n(Security note: Before real launch, replace this local demo gate with verified server-side Admin authentication.)",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElectricBlue,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = passkeyInput,
                        onValueChange = { passkeyInput = it },
                        label = { Text("Enter Local Demo Admin Passkey") },
                        placeholder = { Text(ArenaRepository.DEMO_ADMIN_PASSKEY) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_passkey_input")
                    )

                    Button(
                        onClick = { onUnlockAdmin(passkeyInput) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_unlock_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Unlock Demo Admin Dashboard")
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminTournamentsTab(
    tournaments: List<TournamentEntity>,
    onSaveTournament: (Long?, String, String, String, Int, Int, String, String, Int, String, () -> Unit) -> Unit,
    onPublishRoomSchedule: (Long, String, String, Boolean, String) -> Unit
) {
    var editingId by remember { mutableStateOf<Long?>(null) }
    var title by remember { mutableStateOf("") }
    var gameTitle by remember { mutableStateOf("Cyber Rift Royale") }
    var mode by remember { mutableStateOf("Squad") }
    var maxSlotsText by remember { mutableStateOf("16") }
    var rewardPoolText by remember { mutableStateOf("3000") }
    var mapName by remember { mutableStateOf("Neon Sector 7") }
    var rulesSummary by remember { mutableStateOf("Fair play demo rules apply. Check in 15 mins early.") }
    var hoursFromNowText by remember { mutableStateOf("4") }
    var status by remember { mutableStateOf(TournamentStatus.UPCOMING) }

    // Room Publisher state
    var roomTargetId by remember(tournaments) { mutableLongStateOf(tournaments.firstOrNull()?.id ?: -1L) }
    val targetTournament = tournaments.find { it.id == roomTargetId }
    var roomIdInput by remember(targetTournament?.id) { mutableStateOf(targetTournament?.roomId.orEmpty()) }
    var roomPassInput by remember(targetTournament?.id) { mutableStateOf(targetTournament?.roomPassword.orEmpty()) }
    var roomPublishToggle by remember(targetTournament?.id) { mutableStateOf(targetTournament?.roomPublished ?: false) }
    var roomStatus by remember(targetTournament?.id) { mutableStateOf(targetTournament?.status ?: TournamentStatus.UPCOMING) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Create / Edit Tournament Form
        item {
            ArenaSectionHeader(
                title = if (editingId == null) "Create Demo Tournament" else "Editing Tournament #$editingId",
                subtitle = "All created brackets are tagged DEMO"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Tournament Name *") },
                        placeholder = { Text("e.g., Weekend Cyber Cup [DEMO]") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_tourney_title_input")
                    )
                    OutlinedTextField(
                        value = gameTitle,
                        onValueChange = { gameTitle = it },
                        label = { Text("Original Game Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Solo", "Duo", "Squad").forEach { m ->
                            FilterChip(selected = mode == m, onClick = { mode = m }, label = { Text(m) })
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = maxSlotsText,
                            onValueChange = { maxSlotsText = it.filter(Char::isDigit) },
                            label = { Text("Max Slots") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = rewardPoolText,
                            onValueChange = { rewardPoolText = it.filter(Char::isDigit) },
                            label = { Text("Demo Pts Pool") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = mapName,
                            onValueChange = { mapName = it },
                            label = { Text("Map Name") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = hoursFromNowText,
                            onValueChange = { hoursFromNowText = it.filter(Char::isDigit) },
                            label = { Text("Starts in (hrs)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = rulesSummary,
                        onValueChange = { rulesSummary = it },
                        label = { Text("Rules Summary") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (editingId != null) {
                            OutlinedButton(
                                onClick = {
                                    editingId = null
                                    title = ""
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel Edit")
                            }
                        }
                        Button(
                            onClick = {
                                onSaveTournament(
                                    editingId,
                                    title,
                                    gameTitle,
                                    mode,
                                    maxSlotsText.toIntOrNull() ?: 16,
                                    rewardPoolText.toIntOrNull() ?: 2500,
                                    mapName,
                                    rulesSummary,
                                    hoursFromNowText.toIntOrNull() ?: 4,
                                    status
                                ) {
                                    editingId = null
                                    title = ""
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_save_tourney_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple)
                        ) {
                            Text(if (editingId == null) "Create Tournament" else "Save Changes")
                        }
                    }
                }
            }
        }

        // Match Scheduling & Room Credentials Publisher
        item {
            ArenaSectionHeader(title = "Match Scheduling & Room Publisher", subtitle = "Publish Room ID/Pass to approved players")
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    tournaments.take(5).forEach { t ->
                        FilterChip(
                            selected = roomTargetId == t.id,
                            onClick = { roomTargetId = t.id },
                            label = { Text(t.title) }
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(TournamentStatus.UPCOMING, TournamentStatus.LIVE, TournamentStatus.COMPLETED).forEach { st ->
                            FilterChip(selected = roomStatus == st, onClick = { roomStatus = st }, label = { Text(st) })
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = roomIdInput,
                            onValueChange = { roomIdInput = it },
                            label = { Text("Room ID") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = roomPassInput,
                            onValueChange = { roomPassInput = it },
                            label = { Text("Room Password") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Publish Room to Approved Players", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = roomPublishToggle, onCheckedChange = { roomPublishToggle = it })
                    }
                    Button(
                        onClick = {
                            if (roomTargetId > 0L) {
                                onPublishRoomSchedule(roomTargetId, roomIdInput, roomPassInput, roomPublishToggle, roomStatus)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_publish_room_btn")
                    ) {
                        Icon(Icons.Filled.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Schedule & Room Settings")
                    }
                }
            }
        }

        // Existing Tournaments List to Edit
        item {
            ArenaSectionHeader(title = "Existing Demo Tournaments", subtitle = "Tap Edit to load into form above")
        }
        items(tournaments, key = { it.id }) { t ->
            Card(
                shape = RoundedCornerShape(14.dp),
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
                        TournamentStatusPill(t.status)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(t.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(
                            "${t.mode} • ${t.filledSlots}/${t.maxSlots} slots • Room: ${if (t.roomPublished) t.roomId else "Hidden"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            editingId = t.id
                            title = t.title
                            gameTitle = t.gameTitle
                            mode = t.mode
                            maxSlotsText = t.maxSlots.toString()
                            rewardPoolText = t.demoRewardPoolPoints.toString()
                            mapName = t.mapName
                            rulesSummary = t.rulesSummary
                            status = t.status
                        }
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit")
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminApprovalsTab(
    uiState: ArenaUiState,
    onUpdateRegistration: (Long, String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ArenaSectionHeader(title = "Registration Approvals", subtitle = "Approve players to unlock published room details")
        }
        items(uiState.allRegistrations, key = { it.id }) { reg ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RegistrationStatusPill(reg.status)
                        Text(reg.mode, style = MaterialTheme.typography.labelMedium, color = ElectricBlue)
                    }
                    Text(reg.tournamentTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${reg.playerNickname} (UID: ${reg.gameUid})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onUpdateRegistration(reg.id, RegistrationStatus.APPROVED) },
                            enabled = reg.status != RegistrationStatus.APPROVED,
                            colors = ButtonDefaults.buttonColors(containerColor = StatusEmerald),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_approve_reg_${reg.id}")
                        ) {
                            Text("Approve")
                        }
                        OutlinedButton(
                            onClick = { onUpdateRegistration(reg.id, RegistrationStatus.REJECTED) },
                            enabled = reg.status != RegistrationStatus.REJECTED,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_reject_reg_${reg.id}")
                        ) {
                            Text("Reject", color = StatusCrimson)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminResultsTab(
    uiState: ArenaUiState,
    onPublishResult: (Long, String, String, Int, Int, Int, Int, () -> Unit) -> Unit
) {
    var selectedTournamentId by remember(uiState.tournaments) {
        mutableLongStateOf(uiState.tournaments.firstOrNull()?.id ?: -1L)
    }
    var playerNick by remember { mutableStateOf(uiState.profile?.nickname ?: "NovaValkyrie") }
    var playerUid by remember { mutableStateOf(uiState.profile?.gameUid ?: "BA-849201") }
    var rankText by remember { mutableStateOf("1") }
    var killsText by remember { mutableStateOf("8") }
    var placementPtsText by remember { mutableStateOf("15") }
    var demoAwardText by remember { mutableStateOf("500") }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ArenaSectionHeader(title = "Publish Verified Demo Result", subtitle = "Awards demo points if UID matches player profile")
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.tournaments.take(5).forEach { t ->
                        FilterChip(
                            selected = selectedTournamentId == t.id,
                            onClick = { selectedTournamentId = t.id },
                            label = { Text(t.title) }
                        )
                    }
                    OutlinedTextField(
                        value = playerNick,
                        onValueChange = { playerNick = it },
                        label = { Text("Winner / Player Nickname *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = playerUid,
                        onValueChange = { playerUid = it },
                        label = { Text("Game UID *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = rankText,
                            onValueChange = { rankText = it.filter(Char::isDigit) },
                            label = { Text("Rank") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = killsText,
                            onValueChange = { killsText = it.filter(Char::isDigit) },
                            label = { Text("Kills") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = placementPtsText,
                            onValueChange = { placementPtsText = it.filter(Char::isDigit) },
                            label = { Text("Place Pts") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = demoAwardText,
                            onValueChange = { demoAwardText = it.filter(Char::isDigit) },
                            label = { Text("Demo Award Pts") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Button(
                        onClick = {
                            if (selectedTournamentId > 0L) {
                                onPublishResult(
                                    selectedTournamentId,
                                    playerNick,
                                    playerUid,
                                    rankText.toIntOrNull() ?: 1,
                                    killsText.toIntOrNull() ?: 0,
                                    placementPtsText.toIntOrNull() ?: 10,
                                    demoAwardText.toIntOrNull() ?: 250
                                ) {}
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_publish_result_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple)
                    ) {
                        Text("Publish Verified Demo Result")
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminRewardsTab(
    uiState: ArenaUiState,
    onReviewRewardClaim: (Long, String, String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ArenaSectionHeader(title = "Reward Claim Review Queue", subtitle = "Approve, mark Paid (Demo), or Reject (refunds demo pts)")
        }
        items(uiState.rewardClaims, key = { it.id }) { claim ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RewardClaimStatusPill(claim.status)
                        Text("${claim.pointsCost} DEMO PTS", style = MaterialTheme.typography.labelLarge, color = TrophyGold)
                    }
                    Text(claim.rewardTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text("${claim.playerNickname} (${claim.gameUid})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = { onReviewRewardClaim(claim.id, RewardClaimStatus.APPROVED, "Verified by Admin; queued for demo fulfillment.") },
                            enabled = claim.status != RewardClaimStatus.APPROVED,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Approve", style = MaterialTheme.typography.labelSmall)
                        }
                        Button(
                            onClick = { onReviewRewardClaim(claim.id, RewardClaimStatus.PAID, "Demo collectible marked fulfilled by Admin.") },
                            enabled = claim.status != RewardClaimStatus.PAID,
                            colors = ButtonDefaults.buttonColors(containerColor = StatusEmerald),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Mark Paid", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = { onReviewRewardClaim(claim.id, RewardClaimStatus.REJECTED, "Rejected in Admin review; demo points refunded.") },
                            enabled = claim.status != RewardClaimStatus.REJECTED,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reject", color = StatusCrimson, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminPlayersTab(
    players: List<ManagedPlayerEntity>,
    onTogglePlayerSuspension: (ManagedPlayerEntity) -> Unit
) {
    var confirmPlayer by remember { mutableStateOf<ManagedPlayerEntity?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ArenaSectionHeader(title = "Demo Player Management", subtitle = "Inspect roster status & toggle suspension")
        }
        items(players, key = { it.id }) { player ->
            Card(
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(player.nickname, style = MaterialTheme.typography.titleMedium)
                            StatusPill(
                                label = if (player.isSuspended) "SUSPENDED" else "ACTIVE",
                                containerColor = if (player.isSuspended) StatusCrimson.copy(alpha = 0.2f) else StatusEmerald.copy(alpha = 0.18f),
                                contentColor = if (player.isSuspended) StatusCrimson else StatusEmerald
                            )
                        }
                        Text(
                            text = "UID: ${player.gameUid} • ${player.region}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Matches: ${player.matchesPlayed} • Wins: ${player.wins} • ${player.demoPoints} Demo Pts",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricBlue
                        )
                    }
                    OutlinedButton(
                        onClick = { confirmPlayer = player },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = if (player.isSuspended) Icons.Filled.CheckCircle else Icons.Filled.Block,
                            contentDescription = null,
                            tint = if (player.isSuspended) StatusEmerald else StatusAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (player.isSuspended) "Restore" else "Suspend")
                    }
                }
            }
        }
    }

    confirmPlayer?.let { p ->
        ArenaConfirmDialog(
            title = if (p.isSuspended) "Restore ${p.nickname}?" else "Suspend ${p.nickname}?",
            body = if (p.isSuspended) {
                "Restore this player to active status in the local demo roster?"
            } else {
                "Mark this player as suspended in the local demo roster?"
            },
            confirmLabel = if (p.isSuspended) "Restore" else "Suspend",
            isDestructive = !p.isSuspended,
            onConfirm = {
                onTogglePlayerSuspension(p)
                confirmPlayer = null
            },
            onDismiss = { confirmPlayer = null }
        )
    }
}
