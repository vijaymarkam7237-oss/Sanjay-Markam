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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.ArenaConfirmDialog
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.StatusPill
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusCrimson
import com.example.ui.theme.StatusEmerald
import com.example.ui.viewmodel.ArenaUiState

private data class RuleSection(
    val title: String,
    val tag: String,
    val bullets: List<String>
)

private val arenaRuleSections = listOf(
    RuleSection(
        title = "1. Tournament Rules & Scoring",
        tag = "CORE",
        bullets = listOf(
            "All brackets in this build are FREE LOCAL DEMO tournaments with zero entry fees and zero cash wagering.",
            "Standard demo scoring awards placement points (1st: 15 pts, 2nd: 12 pts, 3rd: 10 pts, 4th: 8 pts) plus 2 pts per elimination.",
            "Only one registration per Game UID is allowed for each tournament; duplicate entries are automatically blocked."
        )
    ),
    RuleSection(
        title = "2. Fair Play & Anti-Cheat Policy",
        tag = "FAIR PLAY",
        bullets = listOf(
            "No third-party cheats, aim assists, wallhacks, modified APKs, scripts, or unauthorized emulators.",
            "Teaming with opposing squads, intentional feeding, or exploiting map glitches results in immediate removal.",
            "Players must behave respectfully in lobbies; harassment or hate speech is strictly prohibited."
        )
    ),
    RuleSection(
        title = "3. Eligibility Requirements",
        tag = "ELIGIBILITY",
        bullets = listOf(
            "Participants must provide an accurate in-game nickname and valid Game UID during registration.",
            "Players must meet local minimum age guidelines for esports participation in their region.",
            "Squad captains are responsible for entering accurate teammate nicknames before the bracket closes."
        )
    ),
    RuleSection(
        title = "4. Disqualification Conditions",
        tag = "PENALTIES",
        bullets = listOf(
            "Failing to join the official match room before countdown expiry.",
            "Playing under an unregistered Game UID or sharing private room passwords with non-registered players.",
            "Submitting edited screenshots or attempting to manipulate match standings."
        )
    ),
    RuleSection(
        title = "5. Match Procedures",
        tag = "PROCEDURE",
        bullets = listOf(
            "Check My Matches 15 minutes before start time to check approval status and official Room ID/Password.",
            "Room credentials become visible only after Admin approves your entry and publishes the room.",
            "Results are verified and published exclusively by tournament organizers in the Admin Dashboard."
        )
    ),
    RuleSection(
        title = "6. Dispute & Review Process",
        tag = "DISPUTES",
        bullets = listOf(
            "If you notice a scoring mistake or suspected rule violation, submit a report via the Support screen.",
            "Include tournament name, match time, and the Game UIDs involved.",
            "In this local demo build, dispute tickets are saved on-device for testing. Live dispute resolution requires backend setup."
        )
    )
)

/**
 * Screen 10: RULES — Tournament rules, fair play, eligibility, disqualifications, match procedures, disputes.
 */
@Composable
fun RulesScreen(modifier: Modifier = Modifier) {
    var expandedIndex by remember { mutableStateOf<Int?>(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("rules_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            DemoModeBanner("Official Battle Arena Tournaments Demo Rulebook. Review all fair play and match procedures before entering.")
        }

        itemsIndexed(arenaRuleSections) { index, section ->
            val isExpanded = expandedIndex == index
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedIndex = if (isExpanded) null else index }
                    .testTag("rule_section_$index"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isExpanded) ElectricPurple else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Filled.Gavel, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                StatusPill(section.tag, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(section.title, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                        Icon(
                            imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse section" else "Expand section",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            section.bullets.forEach { bullet ->
                                Text(
                                    text = "• $bullet",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Screen 11: ACCOUNT — Local demo account state, sign-in/logout controls, local data reset, and future deletion policy info.
 */
@Composable
fun AccountScreen(
    uiState: ArenaUiState,
    onSetSignedIn: (Boolean) -> Unit,
    onResetLocalDemoData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSignedIn = uiState.preferences.isDemoSignedIn
    val profile = uiState.profile
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("account_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            DemoModeBanner("No online cloud authentication is connected yet. This screen demonstrates local demo account state on your device.")
        }

        // Account Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.ManageAccounts, contentDescription = "Account", tint = ElectricBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Account Status", style = MaterialTheme.typography.titleLarge)
                        }
                        StatusPill(
                            label = if (isSignedIn) "LOCAL DEMO ACTIVE" else "SIGNED OUT",
                            containerColor = if (isSignedIn) StatusEmerald.copy(alpha = 0.18f) else StatusAmber.copy(alpha = 0.18f),
                            contentColor = if (isSignedIn) StatusEmerald else StatusAmber
                        )
                    }

                    Text("Mode: Offline Local Demo Profile (Zero-Budget Build)", style = MaterialTheme.typography.bodyMedium)
                    if (isSignedIn && profile != null) {
                        Text("Handle: ${profile.nickname}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Game UID: ${profile.gameUid}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Role: Standard Player${if (uiState.preferences.isAdminUnlocked) " + Local Demo Admin Unlocked" else ""}", style = MaterialTheme.typography.bodySmall, color = ElectricPurple)
                    } else {
                        Text(
                            text = "You signed out of the local demo profile. Sign back in anytime to continue testing.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    if (isSignedIn) {
                        OutlinedButton(
                            onClick = { showLogoutDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("account_logout_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign Out (Local Demo)")
                        }
                    } else {
                        Button(
                            onClick = { onSetSignedIn(true) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("account_signin_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign In as Local Demo Player")
                        }
                    }
                }
            }
        }

        // Security & Privacy Pledge
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Security, contentDescription = "Security", tint = StatusEmerald)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Data & Security Boundaries", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        text = "• No UPI PINs, bank passwords, payment cards, or OTPs are ever requested or stored.\n" +
                            "• No secret API keys are embedded in this app.\n" +
                            "• When you upgrade to live online mode (e.g., Firebase Auth + Firestore), users will sign in securely with verified accounts and server-enforced roles.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Account Deletion & Local Reset Information
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, StatusCrimson.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.DeleteForever, contentDescription = "Account deletion info", tint = StatusCrimson)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Account Deletion & Local Data Reset", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        text = "In this offline demo, all data lives strictly inside your phone's local app storage. You can erase all local data by uninstalling the app, clearing Android App Storage, or resetting the demo dataset below. When online authentication is added in the future, an in-app Permanent Cloud Account Deletion flow (required by Google Play) will erase your cloud profile and records.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = StatusCrimson.copy(alpha = 0.12f)
                    ) {
                        Button(
                            onClick = { showResetDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StatusCrimson,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("account_reset_demo_btn")
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reset All Local Demo Data")
                        }
                    }
                }
            }
        }
    }

    if (showLogoutDialog) {
        ArenaConfirmDialog(
            title = "Sign Out of Demo Session?",
            body = "This signs you out of the local demo profile view. Your local Room database stays saved on-device until reset.",
            confirmLabel = "Sign Out",
            onConfirm = {
                showLogoutDialog = false
                onSetSignedIn(false)
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    if (showResetDialog) {
        ArenaConfirmDialog(
            title = "Reset Local Demo Database?",
            body = "All custom demo registrations, edited profiles, admin-created tournaments, and local support tickets will be wiped and replaced with fresh sample DEMO data.",
            confirmLabel = "Reset Demo Data",
            isDestructive = true,
            onConfirm = {
                showResetDialog = false
                onResetLocalDemoData()
            },
            onDismiss = { showResetDialog = false }
        )
    }
}
