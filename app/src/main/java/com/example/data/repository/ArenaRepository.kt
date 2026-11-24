package com.example.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.data.local.AnnouncementEntity
import com.example.data.local.ArenaDao
import com.example.data.local.DemoSeedData
import com.example.data.local.ManagedPlayerEntity
import com.example.data.local.MatchResultEntity
import com.example.data.local.PlayerProfileEntity
import com.example.data.local.RegistrationEntity
import com.example.data.local.RegistrationStatus
import com.example.data.local.RewardClaimEntity
import com.example.data.local.RewardClaimStatus
import com.example.data.local.RewardItemEntity
import com.example.data.local.SupportTicketEntity
import com.example.data.local.TournamentEntity
import com.example.data.local.TournamentStatus
import com.example.data.local.WalletTransactionEntity
import com.example.data.local.WalletTxType
import com.example.data.preferences.UserPreferences
import com.example.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

sealed interface ActionResult {
    data class Success(val message: String) : ActionResult
    data class Error(val message: String) : ActionResult
}

class ArenaRepository(
    private val dao: ArenaDao,
    private val prefsRepo: UserPreferencesRepository
) {
    val tournaments: Flow<List<TournamentEntity>> = dao.observeAllTournaments()
    val allRegistrations: Flow<List<RegistrationEntity>> = dao.observeAllRegistrations()
    val myRegistrations: Flow<List<RegistrationEntity>> = dao.observeMyRegistrations()
    val allMatchResults: Flow<List<MatchResultEntity>> = dao.observeAllMatchResults()
    val myMatchHistory: Flow<List<MatchResultEntity>> = dao.observeMyMatchHistory()
    val walletTransactions: Flow<List<WalletTransactionEntity>> = dao.observeWalletTransactions()
    val rewardItems: Flow<List<RewardItemEntity>> = dao.observeRewardItems()
    val rewardClaims: Flow<List<RewardClaimEntity>> = dao.observeRewardClaims()
    val announcements: Flow<List<AnnouncementEntity>> = dao.observeAnnouncements()
    val supportTickets: Flow<List<SupportTicketEntity>> = dao.observeSupportTickets()
    val playerProfile: Flow<PlayerProfileEntity?> = dao.observePlayerProfile()
    val managedPlayers: Flow<List<ManagedPlayerEntity>> = dao.observeManagedPlayers()
    val preferences: Flow<UserPreferences> = prefsRepo.preferencesFlow

    suspend fun ensureSeeded() {
        if (dao.getTournamentCount() == 0 || dao.getPlayerProfile() == null) {
            DemoSeedData.seedAll(dao)
        }
    }

    suspend fun resetAllDemoData() {
        dao.clearTournaments()
        dao.clearRegistrations()
        dao.clearMatchResults()
        dao.clearWalletTransactions()
        dao.clearRewardItems()
        dao.clearRewardClaims()
        dao.clearAnnouncements()
        dao.clearSupportTickets()
        dao.clearPlayerProfile()
        dao.clearManagedPlayers()
        DemoSeedData.seedAll(dao)
        prefsRepo.setDemoSignedIn(true)
        prefsRepo.setAdminUnlocked(false)
    }

    // Player Actions (strictly non-privileged — cannot edit results, award points, or approve rewards)

    suspend fun registerForTournament(
        tournamentId: Long,
        nickname: String,
        gameUid: String,
        selectedMode: String,
        teammates: String
    ): ActionResult {
        val trimmedNick = nickname.trim()
        val trimmedUid = gameUid.trim().uppercase()
        if (trimmedNick.length < 3) {
            return ActionResult.Error("Nickname must be at least 3 characters.")
        }
        if (trimmedUid.length < 5) {
            return ActionResult.Error("Enter a valid Game UID (at least 5 characters).")
        }
        val tournament = dao.getTournamentById(tournamentId)
            ?: return ActionResult.Error("Selected tournament was not found.")
        if (tournament.status == TournamentStatus.COMPLETED) {
            return ActionResult.Error("This demo tournament is already completed.")
        }
        if (tournament.filledSlots >= tournament.maxSlots) {
            return ActionResult.Error("All slots are full for ${tournament.title}.")
        }
        val existingCount = dao.countRegistrationForTournament(tournamentId, trimmedUid)
        if (existingCount > 0) {
            return ActionResult.Error("Duplicate registration blocked: UID $trimmedUid is already registered for this tournament.")
        }

        return try {
            dao.insertRegistration(
                RegistrationEntity(
                    tournamentId = tournament.id,
                    tournamentTitle = tournament.title,
                    mode = selectedMode,
                    playerNickname = trimmedNick,
                    gameUid = trimmedUid,
                    teammateNames = teammates.trim(),
                    status = RegistrationStatus.PENDING_APPROVAL,
                    registeredAtMillis = System.currentTimeMillis(),
                    isCurrentPlayer = true,
                    isDemo = true
                )
            )
            dao.updateTournament(tournament.copy(filledSlots = (tournament.filledSlots + 1).coerceAtMost(tournament.maxSlots)))
            ActionResult.Success("Demo registration submitted for ${tournament.title}! Status: Pending Approval.")
        } catch (_: SQLiteConstraintException) {
            ActionResult.Error("Duplicate registration blocked: you are already registered for this tournament.")
        }
    }

    suspend fun updatePlayerProfile(
        nickname: String,
        gameUid: String,
        bio: String,
        region: String,
        preferredMode: String,
        avatarKey: String
    ): ActionResult {
        val trimmedNick = nickname.trim()
        val trimmedUid = gameUid.trim().uppercase()
        if (trimmedNick.length < 3) return ActionResult.Error("Nickname must be at least 3 characters.")
        if (trimmedUid.length < 5) return ActionResult.Error("Game UID must be at least 5 characters.")

        val current = dao.getPlayerProfile() ?: return ActionResult.Error("Profile not loaded.")
        // Note: ordinary profile edit preserves demoPointsBalance untouched!
        dao.upsertPlayerProfile(
            current.copy(
                nickname = trimmedNick,
                gameUid = trimmedUid,
                bio = bio.trim(),
                region = region,
                preferredMode = preferredMode,
                avatarKey = avatarKey
            )
        )
        dao.syncCurrentManagedPlayer(trimmedNick, trimmedUid, region)
        return ActionResult.Success("Demo profile updated locally.")
    }

    suspend fun submitRewardClaim(reward: RewardItemEntity): ActionResult {
        val profile = dao.getPlayerProfile() ?: return ActionResult.Error("Player profile unavailable.")
        if (profile.demoPointsBalance < reward.pointsCost) {
            return ActionResult.Error("Not enough Demo Points. You need ${reward.pointsCost - profile.demoPointsBalance} more DEMO PTS.")
        }
        val newBalance = profile.demoPointsBalance - reward.pointsCost
        dao.upsertPlayerProfile(profile.copy(demoPointsBalance = newBalance))
        dao.syncCurrentManagedPlayerPoints(newBalance)

        val now = System.currentTimeMillis()
        dao.insertRewardClaim(
            RewardClaimEntity(
                rewardId = reward.id,
                rewardTitle = reward.title,
                pointsCost = reward.pointsCost,
                playerNickname = profile.nickname,
                gameUid = profile.gameUid,
                status = RewardClaimStatus.PENDING_VERIFICATION,
                adminNote = "Submitted by player — pending admin verification.",
                requestedAtMillis = now,
                updatedAtMillis = now,
                isDemo = true
            )
        )
        dao.insertWalletTransaction(
            WalletTransactionEntity(
                title = "Reward Claim — ${reward.title}",
                description = "Pending verification (Local Demo Reward)",
                pointsDelta = -reward.pointsCost,
                type = WalletTxType.REWARD_CLAIM,
                timestampMillis = now
            )
        )
        return ActionResult.Success("Claim submitted for ${reward.title}! Status: Pending Verification.")
    }

    suspend fun submitSupportTicket(category: String, subject: String, message: String): ActionResult {
        if (subject.trim().length < 4) return ActionResult.Error("Please enter a short subject (4+ characters).")
        if (message.trim().length < 10) return ActionResult.Error("Please describe your issue in at least 10 characters.")
        dao.insertSupportTicket(
            SupportTicketEntity(
                category = category,
                subject = subject.trim(),
                message = message.trim()
            )
        )
        return ActionResult.Success("Saved locally in Demo Mode. Live support requires backend integration.")
    }

    // Preferences
    suspend fun setDarkTheme(enabled: Boolean) = prefsRepo.setDarkTheme(enabled)
    suspend fun setMatchReminders(enabled: Boolean) = prefsRepo.setMatchReminders(enabled)
    suspend fun setRoomAlerts(enabled: Boolean) = prefsRepo.setRoomAlerts(enabled)
    suspend fun setAnnouncementAlerts(enabled: Boolean) = prefsRepo.setAnnouncementAlerts(enabled)
    suspend fun setLanguage(lang: String) = prefsRepo.setLanguage(lang)
    suspend fun setDemoSignedIn(signedIn: Boolean) = prefsRepo.setDemoSignedIn(signedIn)

    suspend fun verifyDemoAdminPasskey(passkey: String): ActionResult {
        return if (passkey.trim().equals(DEMO_ADMIN_PASSKEY, ignoreCase = true)) {
            prefsRepo.setAdminUnlocked(true)
            ActionResult.Success("Demo Admin Dashboard unlocked on this device.")
        } else {
            ActionResult.Error("Invalid local demo passkey. Use $DEMO_ADMIN_PASSKEY to test Admin tools.")
        }
    }

    suspend fun lockAdminSession() {
        prefsRepo.setAdminUnlocked(false)
    }

    // Admin-Only Operations (Verified against Admin session state)

    private suspend fun requireAdmin(): Boolean {
        return prefsRepo.preferencesFlow.first().isAdminUnlocked
    }

    suspend fun adminSaveTournament(
        existingId: Long?,
        title: String,
        gameTitle: String,
        mode: String,
        maxSlots: Int,
        rewardPoolPoints: Int,
        mapName: String,
        rulesSummary: String,
        hoursFromNow: Int,
        status: String
    ): ActionResult {
        if (!requireAdmin()) return ActionResult.Error("Admin authorization required.")
        if (title.trim().length < 4) return ActionResult.Error("Tournament title must be at least 4 characters.")
        val teamSize = when (mode) {
            "Solo" -> 1
            "Duo" -> 2
            else -> 4
        }
        val sanitizedTitle = if (title.contains("DEMO", ignoreCase = true)) title.trim() else "${title.trim()} [DEMO]"
        val startMillis = System.currentTimeMillis() + hoursFromNow.coerceAtLeast(1) * 3_600_000L

        if (existingId != null && existingId > 0L) {
            val existing = dao.getTournamentById(existingId)
                ?: return ActionResult.Error("Tournament not found for editing.")
            dao.updateTournament(
                existing.copy(
                    title = sanitizedTitle,
                    gameTitle = gameTitle.trim().ifBlank { existing.gameTitle },
                    mode = mode,
                    teamSize = teamSize,
                    maxSlots = maxSlots.coerceIn(4, 100),
                    demoRewardPoolPoints = rewardPoolPoints.coerceAtLeast(100),
                    mapName = mapName.trim().ifBlank { existing.mapName },
                    rulesSummary = rulesSummary.trim().ifBlank { existing.rulesSummary },
                    startTimeMillis = if (status == TournamentStatus.UPCOMING) startMillis else existing.startTimeMillis,
                    status = status
                )
            )
            return ActionResult.Success("Updated demo tournament: $sanitizedTitle")
        } else {
            dao.insertTournament(
                TournamentEntity(
                    title = sanitizedTitle,
                    gameTitle = gameTitle.trim().ifBlank { "Cyber Rift Royale" },
                    mode = mode,
                    teamSize = teamSize,
                    maxSlots = maxSlots.coerceIn(4, 100),
                    filledSlots = 0,
                    startTimeMillis = startMillis,
                    status = status,
                    demoRewardPoolPoints = rewardPoolPoints.coerceAtLeast(100),
                    rulesSummary = rulesSummary.trim().ifBlank { "Standard fair play demo tournament rules apply." },
                    mapName = mapName.trim().ifBlank { "Neon Sector 7" },
                    bannerStyle = listOf("CYBER", "STORM", "NEON", "SOLAR").random(),
                    isFeatured = true,
                    isDemo = true
                )
            )
            return ActionResult.Success("Created demo tournament: $sanitizedTitle")
        }
    }

    suspend fun adminPublishRoomSchedule(
        tournamentId: Long,
        roomId: String,
        roomPassword: String,
        publishRoom: Boolean,
        status: String
    ): ActionResult {
        if (!requireAdmin()) return ActionResult.Error("Admin authorization required.")
        val tournament = dao.getTournamentById(tournamentId)
            ?: return ActionResult.Error("Tournament not found.")
        if (publishRoom && (roomId.isBlank() || roomPassword.isBlank())) {
            return ActionResult.Error("Enter both Room ID and Password before publishing.")
        }
        dao.updateTournament(
            tournament.copy(
                roomId = roomId.trim(),
                roomPassword = roomPassword.trim(),
                roomPublished = publishRoom,
                status = status
            )
        )
        return ActionResult.Success(
            if (publishRoom) "Room credentials published for ${tournament.title}."
            else "Schedule updated for ${tournament.title}."
        )
    }

    suspend fun adminUpdateRegistrationStatus(registrationId: Long, newStatus: String): ActionResult {
        if (!requireAdmin()) return ActionResult.Error("Admin authorization required.")
        dao.updateRegistrationStatus(registrationId, newStatus)
        return ActionResult.Success("Registration marked as ${newStatus.replace('_', ' ')}.")
    }

    suspend fun adminPublishMatchResult(
        tournamentId: Long,
        playerNickname: String,
        gameUid: String,
        rank: Int,
        kills: Int,
        placementPoints: Int,
        demoPointsAwarded: Int
    ): ActionResult {
        if (!requireAdmin()) return ActionResult.Error("Admin authorization required.")
        val tournament = dao.getTournamentById(tournamentId)
            ?: return ActionResult.Error("Select a valid tournament.")
        val cleanNick = playerNickname.trim()
        val cleanUid = gameUid.trim().uppercase()
        if (cleanNick.isBlank() || cleanUid.isBlank()) {
            return ActionResult.Error("Player nickname and Game UID are required.")
        }
        val profile = dao.getPlayerProfile()
        val isCurrent = profile != null && profile.gameUid.equals(cleanUid, ignoreCase = true)
        val totalPts = placementPoints + (kills * 2)
        val now = System.currentTimeMillis()

        dao.insertMatchResult(
            MatchResultEntity(
                tournamentId = tournament.id,
                tournamentTitle = tournament.title,
                mode = tournament.mode,
                rank = rank.coerceAtLeast(1),
                playerNickname = cleanNick,
                gameUid = cleanUid,
                kills = kills.coerceAtLeast(0),
                placementPoints = placementPoints.coerceAtLeast(0),
                totalPoints = totalPts,
                demoPointsAwarded = demoPointsAwarded.coerceAtLeast(0),
                completedAtMillis = now,
                isVerifiedDemo = true,
                isCurrentPlayer = isCurrent
            )
        )
        dao.updateTournament(tournament.copy(status = TournamentStatus.COMPLETED))

        if (isCurrent && profile != null && demoPointsAwarded > 0) {
            val updatedPoints = profile.demoPointsBalance + demoPointsAwarded
            dao.upsertPlayerProfile(profile.copy(demoPointsBalance = updatedPoints))
            dao.syncCurrentManagedPlayerPoints(updatedPoints)
            dao.insertWalletTransaction(
                WalletTransactionEntity(
                    title = "Rank #$rank — ${tournament.title}",
                    description = "Admin-published verified demo result reward",
                    pointsDelta = demoPointsAwarded,
                    type = WalletTxType.MATCH_REWARD,
                    timestampMillis = now
                )
            )
        }
        return ActionResult.Success("Published verified demo result for $cleanNick (#$rank).")
    }

    suspend fun adminReviewRewardClaim(claimId: Long, newStatus: String, note: String): ActionResult {
        if (!requireAdmin()) return ActionResult.Error("Admin authorization required.")
        val claim = dao.getRewardClaimById(claimId) ?: return ActionResult.Error("Claim not found.")
        val previousStatus = claim.status
        val now = System.currentTimeMillis()
        dao.updateRewardClaim(
            claim.copy(
                status = newStatus,
                adminNote = note.trim().ifBlank { "Updated by Admin to $newStatus." },
                updatedAtMillis = now
            )
        )

        // If rejecting a claim belonging to current player that wasn't already rejected, refund Demo Points
        val profile = dao.getPlayerProfile()
        if (newStatus == RewardClaimStatus.REJECTED &&
            previousStatus != RewardClaimStatus.REJECTED &&
            profile != null &&
            profile.gameUid.equals(claim.gameUid, ignoreCase = true)
        ) {
            val refundedBalance = profile.demoPointsBalance + claim.pointsCost
            dao.upsertPlayerProfile(profile.copy(demoPointsBalance = refundedBalance))
            dao.syncCurrentManagedPlayerPoints(refundedBalance)
            dao.insertWalletTransaction(
                WalletTransactionEntity(
                    title = "Refund — ${claim.rewardTitle}",
                    description = "Demo claim rejected by Admin; demo points restored",
                    pointsDelta = claim.pointsCost,
                    type = WalletTxType.REWARD_REFUND,
                    timestampMillis = now
                )
            )
        }
        return ActionResult.Success("Claim updated to ${newStatus.replace('_', ' ')}.")
    }

    suspend fun adminTogglePlayerSuspension(player: ManagedPlayerEntity): ActionResult {
        if (!requireAdmin()) return ActionResult.Error("Admin authorization required.")
        val nextState = !player.isSuspended
        dao.updateManagedPlayerSuspension(player.id, nextState)
        return ActionResult.Success(
            if (nextState) "${player.nickname} suspended in demo roster."
            else "${player.nickname} restored to active demo roster."
        )
    }

    companion object {
        const val DEMO_ADMIN_PASSKEY = "ADMIN-DEMO"
    }
}
