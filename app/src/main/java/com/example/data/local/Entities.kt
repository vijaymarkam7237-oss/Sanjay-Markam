package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

object TournamentStatus {
    const val UPCOMING = "UPCOMING"
    const val LIVE = "LIVE"
    const val COMPLETED = "COMPLETED"
}

object RegistrationStatus {
    const val PENDING_APPROVAL = "PENDING_APPROVAL"
    const val APPROVED = "APPROVED"
    const val REJECTED = "REJECTED"
}

object RewardClaimStatus {
    const val PENDING_VERIFICATION = "PENDING_VERIFICATION"
    const val APPROVED = "APPROVED"
    const val PAID = "PAID"
    const val REJECTED = "REJECTED"
}

object WalletTxType {
    const val MATCH_REWARD = "MATCH_REWARD"
    const val PARTICIPATION_BONUS = "PARTICIPATION_BONUS"
    const val REWARD_CLAIM = "REWARD_CLAIM"
    const val REWARD_REFUND = "REWARD_REFUND"
    const val ADMIN_ADJUSTMENT = "ADMIN_ADJUSTMENT"
}

@Entity(tableName = "tournaments")
data class TournamentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val gameTitle: String,
    val mode: String, // Solo, Duo, Squad
    val teamSize: Int,
    val maxSlots: Int,
    val filledSlots: Int,
    val startTimeMillis: Long,
    val status: String = TournamentStatus.UPCOMING,
    val demoRewardPoolPoints: Int,
    val rulesSummary: String,
    val mapName: String,
    val roomId: String = "",
    val roomPassword: String = "",
    val roomPublished: Boolean = false,
    val bannerStyle: String = "CYBER", // CYBER, STORM, SOLAR, NEON
    val isFeatured: Boolean = false,
    val isDemo: Boolean = true
)

@Entity(
    tableName = "registrations",
    indices = [Index(value = ["tournamentId", "gameUid"], unique = true)]
)
data class RegistrationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tournamentId: Long,
    val tournamentTitle: String,
    val mode: String,
    val playerNickname: String,
    val gameUid: String,
    val teammateNames: String = "",
    val status: String = RegistrationStatus.PENDING_APPROVAL,
    val registeredAtMillis: Long = System.currentTimeMillis(),
    val isCurrentPlayer: Boolean = true,
    val isDemo: Boolean = true
)

@Entity(tableName = "match_results")
data class MatchResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tournamentId: Long,
    val tournamentTitle: String,
    val mode: String,
    val rank: Int,
    val playerNickname: String,
    val gameUid: String,
    val kills: Int,
    val placementPoints: Int,
    val totalPoints: Int,
    val demoPointsAwarded: Int,
    val completedAtMillis: Long,
    val isVerifiedDemo: Boolean = true,
    val isCurrentPlayer: Boolean = false
)

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val pointsDelta: Int,
    val type: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val isDemo: Boolean = true
)

@Entity(tableName = "reward_items")
data class RewardItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val pointsCost: Int,
    val iconKey: String,
    val isActive: Boolean = true,
    val isDemo: Boolean = true
)

@Entity(tableName = "reward_claims")
data class RewardClaimEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rewardId: Long,
    val rewardTitle: String,
    val pointsCost: Int,
    val playerNickname: String,
    val gameUid: String,
    val status: String = RewardClaimStatus.PENDING_VERIFICATION,
    val adminNote: String = "Awaiting demo verification review.",
    val requestedAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis(),
    val isDemo: Boolean = true
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String,
    val tag: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val isDemo: Boolean = true
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val subject: String,
    val message: String,
    val status: String = "SAVED_LOCALLY_DEMO",
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val nickname: String,
    val gameUid: String,
    val bio: String,
    val region: String,
    val preferredMode: String,
    val demoPointsBalance: Int,
    val avatarKey: String = "CYBER_KNIGHT"
)

@Entity(tableName = "managed_players")
data class ManagedPlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nickname: String,
    val gameUid: String,
    val region: String,
    val matchesPlayed: Int,
    val wins: Int,
    val demoPoints: Int,
    val isSuspended: Boolean = false,
    val isCurrentUser: Boolean = false
)
