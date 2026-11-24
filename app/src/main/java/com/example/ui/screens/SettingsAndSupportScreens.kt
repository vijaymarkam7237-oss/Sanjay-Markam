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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import com.example.ui.components.ArenaSectionHeader
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.StatusPill
import com.example.ui.components.formatDateTime
import com.example.ui.navigation.ArenaRoutes
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.StatusEmerald
import com.example.ui.viewmodel.ArenaUiState

/**
 * Screen 12: SETTINGS — Notification preferences (local demo prefs), language, privacy info, theme toggle, help links.
 */
@Composable
fun SettingsScreen(
    uiState: ArenaUiState,
    onToggleDarkTheme: (Boolean) -> Unit,
    onToggleMatchReminders: (Boolean) -> Unit,
    onToggleRoomAlerts: (Boolean) -> Unit,
    onToggleAnnouncements: (Boolean) -> Unit,
    onSelectLanguage: (String) -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val prefs = uiState.preferences

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DemoModeBanner("Preferences are saved locally on your device. Push notifications are preferences-only until an online backend is connected.")
        }

        // Theme Preference
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.DarkMode, contentDescription = "Theme", tint = ElectricPurple)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Appearance", style = MaterialTheme.typography.titleMedium)
                    }
                    SettingToggleRow(
                        title = "Esports Dark Charcoal Theme",
                        subtitle = "Recommended high-contrast dark theme with purple & electric-blue accents",
                        checked = prefs.darkThemeEnabled,
                        testTag = "settings_dark_theme_switch",
                        onCheckedChange = onToggleDarkTheme
                    )
                }
            }
        }

        // Notification Preferences
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = ElectricBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Notification Preferences (Demo)", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        text = "Saved locally for demo preview. Real push alerts are not active in offline demo mode.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SettingToggleRow(
                        title = "Upcoming Match Reminders",
                        subtitle = "Preference for 15-min countdown alerts",
                        checked = prefs.matchRemindersEnabled,
                        testTag = "settings_match_reminders",
                        onCheckedChange = onToggleMatchReminders
                    )
                    SettingToggleRow(
                        title = "Room ID & Password Alerts",
                        subtitle = "Preference for official room publication notices",
                        checked = prefs.roomAlertsEnabled,
                        testTag = "settings_room_alerts",
                        onCheckedChange = onToggleRoomAlerts
                    )
                    SettingToggleRow(
                        title = "Arena Announcements",
                        subtitle = "Preference for new demo brackets & rule updates",
                        checked = prefs.announcementAlertsEnabled,
                        testTag = "settings_announcements",
                        onCheckedChange = onToggleAnnouncements
                    )
                }
            }
        }

        // Language Selection
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Language, contentDescription = "Language", tint = ElectricBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Language Preference", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        text = "Selected: ${prefs.language} (Full translations can be added via Android strings resources later).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("English", "Hindi", "Spanish", "Indonesian").forEach { lang ->
                            FilterChip(
                                selected = prefs.language == lang,
                                onClick = { onSelectLanguage(lang) },
                                label = { Text(lang) }
                            )
                        }
                    }
                }
            }
        }

        // Privacy Info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.PrivacyTip, contentDescription = "Privacy", tint = StatusEmerald)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Privacy Information", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        text = "• All profile details, demo registrations, and demo points stay strictly on your Android device.\n" +
                            "• No background tracking, no ad networks, no real-money payments, and no UPI/bank/OTP data collection.\n" +
                            "• Before publishing on Google Play, host a public Privacy Policy URL and link it here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Help Links
        item {
            ArenaSectionHeader(title = "Help & Legal Links", subtitle = "Quick access")
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { onNavigate(ArenaRoutes.RULES) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Rules")
                }
                OutlinedButton(
                    onClick = { onNavigate(ArenaRoutes.SUPPORT) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Support & FAQ")
                }
                OutlinedButton(
                    onClick = { onNavigate(ArenaRoutes.ACCOUNT) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Account")
                }
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag)
        )
    }
}

private val supportFaqs = listOf(
    "Are Demo Points real money?" to "No. Demo Points have zero cash value and cannot be bought, deposited, or withdrawn as real money. This is a free local demo.",
    "Why are Room ID and Password hidden?" to "Room credentials stay locked until an organizer approves your registration AND officially publishes the room details in Admin Dashboard.",
    "Why can't I join the same tournament twice?" to "To keep brackets fair, the app prevents duplicate registrations for the same tournament and Game UID.",
    "How do I test Admin features?" to "Open the drawer menu and tap Admin Dashboard. Use the local demo passkey shown on that screen to test tournament creation, approvals, results, and reward reviews.",
    "How do I contact live support?" to "This zero-budget demo does not have a live support desk, email, or phone number configured yet. Submitting the form below saves your test report locally on your phone."
)

/**
 * Screen 14: SUPPORT — FAQ, help guide, contact/problem report form (saved locally; no fabricated email/phone).
 */
@Composable
fun SupportScreen(
    uiState: ArenaUiState,
    onSubmitTicket: (String, String, String, () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedFaq by remember { mutableStateOf<Int?>(0) }
    var category by remember { mutableStateOf("Report a Problem") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("support_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            DemoModeBanner("Local Demo Support — No official support email or phone number is configured in this demo build. Reports are saved locally on your device.")
        }

        item {
            ArenaSectionHeader(title = "Frequently Asked Questions", subtitle = "Tap a question for details")
        }

        itemsIndexed(supportFaqs) { idx, (question, answer) ->
            val expanded = expandedFaq == idx
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedFaq = if (expanded) null else idx },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Filled.HelpOutline, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(question, style = MaterialTheme.typography.titleSmall)
                        }
                        Icon(
                            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = "Toggle answer"
                        )
                    }
                    if (expanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(answer, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            ArenaSectionHeader(title = "Contact / Report a Problem", subtitle = "Saved locally in Demo Mode")
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Report a Problem", "Match Dispute", "General Help").forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = subject,
                        onValueChange = {
                            subject = it
                            errorMsg = null
                        },
                        label = { Text("Subject *") },
                        placeholder = { Text("Short summary of your question or bug") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("support_subject_input")
                    )

                    OutlinedTextField(
                        value = message,
                        onValueChange = {
                            message = it
                            errorMsg = null
                        },
                        label = { Text("Details *") },
                        placeholder = { Text("Describe what happened (include Tournament name or UID if relevant)") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("support_message_input")
                    )

                    errorMsg?.let { err ->
                        Text(err, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }

                    Button(
                        onClick = {
                            if (subject.trim().length < 4) {
                                errorMsg = "Subject must be at least 4 characters."
                            } else if (message.trim().length < 10) {
                                errorMsg = "Please provide at least 10 characters of detail."
                            } else {
                                onSubmitTicket(category, subject, message) {
                                    subject = ""
                                    message = ""
                                    errorMsg = null
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("support_submit_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Demo Support Ticket")
                    }
                }
            }
        }

        if (uiState.supportTickets.isNotEmpty()) {
            item {
                ArenaSectionHeader(title = "Your Saved Local Tickets", subtitle = "Stored on-device for demo testing")
            }
            items(uiState.supportTickets, key = { it.id }) { ticket ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusPill(ticket.category.uppercase(), ElectricBlue.copy(alpha = 0.16f), ElectricBlue)
                            Text(formatDateTime(ticket.createdAtMillis), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(ticket.subject, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(ticket.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
