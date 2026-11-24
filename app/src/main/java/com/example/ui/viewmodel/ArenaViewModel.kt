package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AnnouncementEntity
import com.example.data.local.ArenaDatabase
import com.example.data.local.ManagedPlayerEntity
import com.example.data.local.MatchResultEntity
import com.example.data.local.PlayerProfileEntity
import com.example.data.local.RegistrationEntity
import com.example.data.local.RewardClaimEntity
import com.example.data.local.RewardItemEntity
import com.example.data.local.SupportTicketEntity
import com.example.data.local.TournamentEntity
import com.example.data.local.WalletTransactionEntity
import com.example.data.preferences.UserPreferences
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.ActionResult
import com.example.data.repository.ArenaRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UiFeedback(
    val message: String,
    val isError: Boolean = false
)

data class ArenaUiState(
    val isLoading: Boolean = true,
    val tournaments: List<TournamentEntity> = emptyList(),
    val myRegistrations: List<RegistrationEntity> = emptyList(),
    val allRegistrations: List<RegistrationEntity> = emptyList(),
    val allResults: List<MatchResultEntity> = emptyList(),
    val myMatchHistory: List<MatchResultEntity> = emptyList(),
    val walletTransactions: List<WalletTransactionEntity> = emptyList(),
    val rewardItems: List<RewardItemEntity> = emptyList(),
    val rewardClaims: List<RewardClaimEntity> = emptyList(),
    val announcements: List<AnnouncementEntity> = emptyList(),
    val supportTickets: List<SupportTicketEntity> = emptyList(),
    val profile: PlayerProfileEntity? = null,
    val managedPlayers: List<ManagedPlayerEntity> = emptyList(),
    val preferences: UserPreferences = UserPreferences()
) {
    val totalKills: Int get() = myMatchHistory.sumOf { it.kills }
    val totalWins: Int get() = myMatchHistory.count { it.rank == 1 }
    val totalMatchesPlayed: Int get() = myMatchHistory.size
    val averagePoints: Int
        get() = if (myMatchHistory.isEmpty()) 0 else myMatchHistory.sumOf { it.totalPoints } / myMatchHistory.size
}

class ArenaViewModel(private val repository: ArenaRepository) : ViewModel() {

    private val _isSeeded = MutableStateFlow(false)
    private val _feedback = MutableSharedFlow<UiFeedback>(extraBufferCapacity = 4)
    val feedback: SharedFlow<UiFeedback> = _feedback.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            _isSeeded.value = true
        }
    }

    private val coreArenaFlow = combine(
        repository.tournaments,
        repository.myRegistrations,
        repository.allRegistrations,
        repository.allMatchResults,
        repository.myMatchHistory
    ) { tournaments, myRegs, allRegs, allResults, myHistory ->
        CoreArenaBundle(tournaments, myRegs, allRegs, allResults, myHistory)
    }

    private val economyAndMetaFlow = combine(
        repository.walletTransactions,
        repository.rewardItems,
        repository.rewardClaims,
        repository.announcements,
        repository.supportTickets
    ) { txs, items, claims, announcements, tickets ->
        EconomyMetaBundle(txs, items, claims, announcements, tickets)
    }

    val uiState: StateFlow<ArenaUiState> = combine(
        _isSeeded,
        coreArenaFlow,
        economyAndMetaFlow,
        repository.playerProfile,
        combine(repository.managedPlayers, repository.preferences) { players, prefs -> players to prefs }
    ) { seeded, core, economy, profile, playersAndPrefs ->
        ArenaUiState(
            isLoading = !seeded || profile == null,
            tournaments = core.tournaments,
            myRegistrations = core.myRegistrations,
            allRegistrations = core.allRegistrations,
            allResults = core.allResults,
            myMatchHistory = core.myMatchHistory,
            walletTransactions = economy.transactions,
            rewardItems = economy.rewardItems,
            rewardClaims = economy.rewardClaims,
            announcements = economy.announcements,
            supportTickets = economy.supportTickets,
            profile = profile,
            managedPlayers = playersAndPrefs.first,
            preferences = playersAndPrefs.second
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ArenaUiState(isLoading = true)
    )

    private fun emitResult(result: ActionResult) {
        when (result) {
            is ActionResult.Success -> _feedback.tryEmit(UiFeedback(result.message, isError = false))
            is ActionResult.Error -> _feedback.tryEmit(UiFeedback(result.message, isError = true))
        }
    }

    fun registerForTournament(
        tournamentId: Long,
        nickname: String,
        gameUid: String,
        mode: String,
        teammates: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = repository.registerForTournament(tournamentId, nickname, gameUid, mode, teammates)
            emitResult(result)
            if (result is ActionResult.Success) onSuccess()
        }
    }

    fun updateProfile(
        nickname: String,
        gameUid: String,
        bio: String,
        region: String,
        preferredMode: String,
        avatarKey: String
    ) {
        viewModelScope.launch {
            emitResult(repository.updatePlayerProfile(nickname, gameUid, bio, region, preferredMode, avatarKey))
        }
    }

    fun claimReward(reward: RewardItemEntity) {
        viewModelScope.launch {
            emitResult(repository.submitRewardClaim(reward))
        }
    }

    fun submitSupportTicket(category: String, subject: String, message: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val res = repository.submitSupportTicket(category, subject, message)
            emitResult(res)
            if (res is ActionResult.Success) onSuccess()
        }
    }

    // Settings & Demo Session
    fun setDarkTheme(enabled: Boolean) = viewModelScope.launch { repository.setDarkTheme(enabled) }
    fun setMatchReminders(enabled: Boolean) = viewModelScope.launch { repository.setMatchReminders(enabled) }
    fun setRoomAlerts(enabled: Boolean) = viewModelScope.launch { repository.setRoomAlerts(enabled) }
    fun setAnnouncementAlerts(enabled: Boolean) = viewModelScope.launch { repository.setAnnouncementAlerts(enabled) }
    fun setLanguage(lang: String) = viewModelScope.launch {
        repository.setLanguage(lang)
        _feedback.tryEmit(UiFeedback("Display language preference saved: $lang (Demo labels stay in English)."))
    }

    fun setDemoSignedIn(signedIn: Boolean) = viewModelScope.launch {
        repository.setDemoSignedIn(signedIn)
        _feedback.tryEmit(
            UiFeedback(
                if (signedIn) "Signed in to Local Demo Profile." else "Signed out of Local Demo session."
            )
        )
    }

    fun resetDemoData() = viewModelScope.launch {
        repository.resetAllDemoData()
        _feedback.tryEmit(UiFeedback("Local demo database reset to fresh sample tournaments and profile."))
    }

    // Admin Access & Actions
    fun unlockAdmin(passkey: String, onUnlocked: () -> Unit = {}) = viewModelScope.launch {
        val result = repository.verifyDemoAdminPasskey(passkey)
        emitResult(result)
        if (result is ActionResult.Success) onUnlocked()
    }

    fun lockAdmin() = viewModelScope.launch {
        repository.lockAdminSession()
        _feedback.tryEmit(UiFeedback("Admin Dashboard locked. Returned to standard player permissions."))
    }

    fun adminSaveTournament(
        existingId: Long?,
        title: String,
        gameTitle: String,
        mode: String,
        maxSlots: Int,
        rewardPoolPoints: Int,
        mapName: String,
        rulesSummary: String,
        hoursFromNow: Int,
        status: String,
        onSuccess: () -> Unit = {}
    ) = viewModelScope.launch {
        val res = repository.adminSaveTournament(
            existingId, title, gameTitle, mode, maxSlots, rewardPoolPoints, mapName, rulesSummary, hoursFromNow, status
        )
        emitResult(res)
        if (res is ActionResult.Success) onSuccess()
    }

    fun adminPublishRoomSchedule(
        tournamentId: Long,
        roomId: String,
        roomPassword: String,
        publishRoom: Boolean,
        status: String
    ) = viewModelScope.launch {
        emitResult(repository.adminPublishRoomSchedule(tournamentId, roomId, roomPassword, publishRoom, status))
    }

    fun adminUpdateRegistration(registrationId: Long, status: String) = viewModelScope.launch {
        emitResult(repository.adminUpdateRegistrationStatus(registrationId, status))
    }

    fun adminPublishResult(
        tournamentId: Long,
        nickname: String,
        gameUid: String,
        rank: Int,
        kills: Int,
        placementPoints: Int,
        demoPointsAwarded: Int,
        onSuccess: () -> Unit = {}
    ) = viewModelScope.launch {
        val res = repository.adminPublishMatchResult(
            tournamentId, nickname, gameUid, rank, kills, placementPoints, demoPointsAwarded
        )
        emitResult(res)
        if (res is ActionResult.Success) onSuccess()
    }

    fun adminReviewRewardClaim(claimId: Long, status: String, note: String) = viewModelScope.launch {
        emitResult(repository.adminReviewRewardClaim(claimId, status, note))
    }

    fun adminTogglePlayerSuspension(player: ManagedPlayerEntity) = viewModelScope.launch {
        emitResult(repository.adminTogglePlayerSuspension(player))
    }

    private data class CoreArenaBundle(
        val tournaments: List<TournamentEntity>,
        val myRegistrations: List<RegistrationEntity>,
        val allRegistrations: List<RegistrationEntity>,
        val allResults: List<MatchResultEntity>,
        val myMatchHistory: List<MatchResultEntity>
    )

    private data class EconomyMetaBundle(
        val transactions: List<WalletTransactionEntity>,
        val rewardItems: List<RewardItemEntity>,
        val rewardClaims: List<RewardClaimEntity>,
        val announcements: List<AnnouncementEntity>,
        val supportTickets: List<SupportTicketEntity>
    )

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = ArenaDatabase.getInstance(context.applicationContext)
            val prefs = UserPreferencesRepository(context.applicationContext)
            val repo = ArenaRepository(db.arenaDao(), prefs)
            return ArenaViewModel(repo) as T
        }
    }
}
