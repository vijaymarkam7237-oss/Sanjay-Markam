package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ArenaDao {
    // Tournaments
    @Query("SELECT * FROM tournaments ORDER BY startTimeMillis ASC")
    fun observeAllTournaments(): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE id = :id LIMIT 1")
    suspend fun getTournamentById(id: Long): TournamentEntity?

    @Query("SELECT COUNT(*) FROM tournaments")
    suspend fun getTournamentCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: TournamentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournaments(tournaments: List<TournamentEntity>): List<Long>

    @Update
    suspend fun updateTournament(tournament: TournamentEntity)

    // Registrations
    @Query("SELECT * FROM registrations ORDER BY registeredAtMillis DESC")
    fun observeAllRegistrations(): Flow<List<RegistrationEntity>>

    @Query("SELECT * FROM registrations WHERE isCurrentPlayer = 1 ORDER BY registeredAtMillis DESC")
    fun observeMyRegistrations(): Flow<List<RegistrationEntity>>

    @Query("SELECT COUNT(*) FROM registrations WHERE tournamentId = :tournamentId AND gameUid = :gameUid")
    suspend fun countRegistrationForTournament(tournamentId: Long, gameUid: String): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertRegistration(registration: RegistrationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistrations(registrations: List<RegistrationEntity>)

    @Update
    suspend fun updateRegistration(registration: RegistrationEntity)

    @Query("UPDATE registrations SET status = :status WHERE id = :registrationId")
    suspend fun updateRegistrationStatus(registrationId: Long, status: String)

    // Match Results
    @Query("SELECT * FROM match_results ORDER BY completedAtMillis DESC, rank ASC")
    fun observeAllMatchResults(): Flow<List<MatchResultEntity>>

    @Query("SELECT * FROM match_results WHERE isCurrentPlayer = 1 ORDER BY completedAtMillis DESC")
    fun observeMyMatchHistory(): Flow<List<MatchResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchResult(result: MatchResultEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchResults(results: List<MatchResultEntity>)

    // Wallet Transactions
    @Query("SELECT * FROM wallet_transactions ORDER BY timestampMillis DESC")
    fun observeWalletTransactions(): Flow<List<WalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalletTransaction(tx: WalletTransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalletTransactions(txs: List<WalletTransactionEntity>)

    // Reward Items
    @Query("SELECT * FROM reward_items WHERE isActive = 1 ORDER BY pointsCost ASC")
    fun observeRewardItems(): Flow<List<RewardItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRewardItems(items: List<RewardItemEntity>)

    // Reward Claims
    @Query("SELECT * FROM reward_claims ORDER BY requestedAtMillis DESC")
    fun observeRewardClaims(): Flow<List<RewardClaimEntity>>

    @Query("SELECT * FROM reward_claims WHERE id = :claimId LIMIT 1")
    suspend fun getRewardClaimById(claimId: Long): RewardClaimEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRewardClaim(claim: RewardClaimEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRewardClaims(claims: List<RewardClaimEntity>)

    @Update
    suspend fun updateRewardClaim(claim: RewardClaimEntity)

    // Announcements
    @Query("SELECT * FROM announcements ORDER BY timestampMillis DESC")
    fun observeAnnouncements(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncements(announcements: List<AnnouncementEntity>)

    // Support Tickets
    @Query("SELECT * FROM support_tickets ORDER BY createdAtMillis DESC")
    fun observeSupportTickets(): Flow<List<SupportTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupportTicket(ticket: SupportTicketEntity): Long

    // Player Profile
    @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
    fun observePlayerProfile(): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
    suspend fun getPlayerProfile(): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlayerProfile(profile: PlayerProfileEntity)

    // Managed Players (Admin view)
    @Query("SELECT * FROM managed_players ORDER BY isCurrentUser DESC, demoPoints DESC")
    fun observeManagedPlayers(): Flow<List<ManagedPlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManagedPlayers(players: List<ManagedPlayerEntity>)

    @Query("UPDATE managed_players SET isSuspended = :suspended WHERE id = :playerId")
    suspend fun updateManagedPlayerSuspension(playerId: Long, suspended: Boolean)

    @Query("UPDATE managed_players SET nickname = :nickname, gameUid = :gameUid, region = :region WHERE isCurrentUser = 1")
    suspend fun syncCurrentManagedPlayer(nickname: String, gameUid: String, region: String)

    @Query("UPDATE managed_players SET demoPoints = :points WHERE isCurrentUser = 1")
    suspend fun syncCurrentManagedPlayerPoints(points: Int)

    // Reset helper
    @Query("DELETE FROM tournaments")
    suspend fun clearTournaments()

    @Query("DELETE FROM registrations")
    suspend fun clearRegistrations()

    @Query("DELETE FROM match_results")
    suspend fun clearMatchResults()

    @Query("DELETE FROM wallet_transactions")
    suspend fun clearWalletTransactions()

    @Query("DELETE FROM reward_items")
    suspend fun clearRewardItems()

    @Query("DELETE FROM reward_claims")
    suspend fun clearRewardClaims()

    @Query("DELETE FROM announcements")
    suspend fun clearAnnouncements()

    @Query("DELETE FROM support_tickets")
    suspend fun clearSupportTickets()

    @Query("DELETE FROM player_profile")
    suspend fun clearPlayerProfile()

    @Query("DELETE FROM managed_players")
    suspend fun clearManagedPlayers()
}
