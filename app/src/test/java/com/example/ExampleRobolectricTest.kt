package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ArenaDatabase
import com.example.data.local.RegistrationStatus
import com.example.data.local.RewardClaimStatus
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.ActionResult
import com.example.data.repository.ArenaRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var db: ArenaDatabase
    private lateinit var repository: ArenaRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, ArenaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val prefs = UserPreferencesRepository(context)
        repository = ArenaRepository(db.arenaDao(), prefs)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `app name matches Battle Arena Tournaments`() {
        assertEquals("Battle Arena Tournaments", context.getString(R.string.app_name))
    }

    @Test
    fun `demo seeding and duplicate registration prevention work`() = runTest {
        repository.ensureSeeded()
        val tournaments = repository.tournaments.first()
        assertTrue(tournaments.size >= 6)

        // Third tournament (Apex Grid Duo Clash) is open and not yet joined by NovaValkyrie
        val openTourney = tournaments.first { it.title.contains("Apex Grid") }
        val firstAttempt = repository.registerForTournament(
            tournamentId = openTourney.id,
            nickname = "NovaValkyrie",
            gameUid = "BA-849201",
            selectedMode = "Duo",
            teammates = "ZephyrK"
        )
        assertTrue(firstAttempt is ActionResult.Success)

        val duplicateAttempt = repository.registerForTournament(
            tournamentId = openTourney.id,
            nickname = "NovaValkyrie",
            gameUid = "BA-849201",
            selectedMode = "Duo",
            teammates = "ZephyrK"
        )
        assertTrue(duplicateAttempt is ActionResult.Error)
    }

    @Test
    fun `ordinary user cannot run admin actions and admin unlock enables approval`() = runTest {
        repository.ensureSeeded()
        val regs = repository.allRegistrations.first()
        val pendingReg = regs.first { it.status == RegistrationStatus.PENDING_APPROVAL }

        // Blocked while locked
        repository.lockAdminSession()
        val blocked = repository.adminUpdateRegistrationStatus(pendingReg.id, RegistrationStatus.APPROVED)
        assertTrue(blocked is ActionResult.Error)

        // Unlock with demo passkey and approve
        val unlocked = repository.verifyDemoAdminPasskey(ArenaRepository.DEMO_ADMIN_PASSKEY)
        assertTrue(unlocked is ActionResult.Success)

        val approved = repository.adminUpdateRegistrationStatus(pendingReg.id, RegistrationStatus.APPROVED)
        assertTrue(approved is ActionResult.Success)
    }

    @Test
    fun `reward claim deducts demo points and admin rejection refunds them`() = runTest {
        repository.ensureSeeded()
        val initialBalance = repository.playerProfile.first()!!.demoPointsBalance
        val reward = repository.rewardItems.first().first()

        val claimRes = repository.submitRewardClaim(reward)
        assertTrue(claimRes is ActionResult.Success)

        val afterClaimBalance = repository.playerProfile.first()!!.demoPointsBalance
        assertEquals(initialBalance - reward.pointsCost, afterClaimBalance)

        // Admin rejects -> refunds demo points
        repository.verifyDemoAdminPasskey(ArenaRepository.DEMO_ADMIN_PASSKEY)
        val latestClaim = repository.rewardClaims.first().first()
        val rejectRes = repository.adminReviewRewardClaim(latestClaim.id, RewardClaimStatus.REJECTED, "Demo refund test")
        assertTrue(rejectRes is ActionResult.Success)

        val refundedBalance = repository.playerProfile.first()!!.demoPointsBalance
        assertEquals(initialBalance, refundedBalance)
    }
}
