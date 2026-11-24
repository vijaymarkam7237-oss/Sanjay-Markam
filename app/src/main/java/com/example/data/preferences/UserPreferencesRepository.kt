package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.arenaDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "battle_arena_demo_prefs"
)

data class UserPreferences(
    val darkThemeEnabled: Boolean = true,
    val matchRemindersEnabled: Boolean = true,
    val roomAlertsEnabled: Boolean = true,
    val announcementAlertsEnabled: Boolean = false,
    val language: String = "English",
    val isDemoSignedIn: Boolean = true,
    val isAdminUnlocked: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {
    private object Keys {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val MATCH_REMINDERS = booleanPreferencesKey("match_reminders")
        val ROOM_ALERTS = booleanPreferencesKey("room_alerts")
        val ANNOUNCEMENT_ALERTS = booleanPreferencesKey("announcement_alerts")
        val LANGUAGE = stringPreferencesKey("language")
        val DEMO_SIGNED_IN = booleanPreferencesKey("demo_signed_in")
        val ADMIN_UNLOCKED = booleanPreferencesKey("admin_unlocked")
    }

    val preferencesFlow: Flow<UserPreferences> = context.arenaDataStore.data.map { prefs ->
        UserPreferences(
            darkThemeEnabled = prefs[Keys.DARK_THEME] ?: true,
            matchRemindersEnabled = prefs[Keys.MATCH_REMINDERS] ?: true,
            roomAlertsEnabled = prefs[Keys.ROOM_ALERTS] ?: true,
            announcementAlertsEnabled = prefs[Keys.ANNOUNCEMENT_ALERTS] ?: false,
            language = prefs[Keys.LANGUAGE] ?: "English",
            isDemoSignedIn = prefs[Keys.DEMO_SIGNED_IN] ?: true,
            isAdminUnlocked = prefs[Keys.ADMIN_UNLOCKED] ?: false
        )
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.arenaDataStore.edit { it[Keys.DARK_THEME] = enabled }
    }

    suspend fun setMatchReminders(enabled: Boolean) {
        context.arenaDataStore.edit { it[Keys.MATCH_REMINDERS] = enabled }
    }

    suspend fun setRoomAlerts(enabled: Boolean) {
        context.arenaDataStore.edit { it[Keys.ROOM_ALERTS] = enabled }
    }

    suspend fun setAnnouncementAlerts(enabled: Boolean) {
        context.arenaDataStore.edit { it[Keys.ANNOUNCEMENT_ALERTS] = enabled }
    }

    suspend fun setLanguage(language: String) {
        context.arenaDataStore.edit { it[Keys.LANGUAGE] = language }
    }

    suspend fun setDemoSignedIn(signedIn: Boolean) {
        context.arenaDataStore.edit { it[Keys.DEMO_SIGNED_IN] = signedIn }
    }

    suspend fun setAdminUnlocked(unlocked: Boolean) {
        context.arenaDataStore.edit { it[Keys.ADMIN_UNLOCKED] = unlocked }
    }
}
