package com.example.data.local

import java.util.concurrent.TimeUnit

object DemoSeedData {
    suspend fun seedAll(dao: ArenaDao) {
        val now = System.currentTimeMillis()
        val hour = TimeUnit.HOURS.toMillis(1)
        val day = TimeUnit.DAYS.toMillis(1)

        val defaultProfile = PlayerProfileEntity(
            id = 1,
            nickname = "NovaValkyrie",
            gameUid = "BA-849201",
            bio = "Competitive tactical player exploring Battle Arena Tournaments (Local Demo).",
            region = "South Asia",
            preferredMode = "Squad",
            demoPointsBalance = 1450,
            avatarKey = "CYBER_KNIGHT"
        )
        dao.upsertPlayerProfile(defaultProfile)

        val t1Id = dao.insertTournament(
            TournamentEntity(
                title = "Cyber Rift Masters [DEMO]",
                gameTitle = "Cyber Rift Royale",
                mode = "Squad",
                teamSize = 4,
                maxSlots = 16,
                filledSlots = 12,
                startTimeMillis = now + 2 * hour + 35 * 60_000L,
                status = TournamentStatus.UPCOMING,
                demoRewardPoolPoints = 5000,
                rulesSummary = "4-player squad format. Standard demo scoring: 15 pts for 1st place + 2 pts per elimination. No emulators or modified clients.",
                mapName = "Neon Sector 7",
                roomId = "DEMO-CR-4821",
                roomPassword = "ARENA77",
                roomPublished = true,
                bannerStyle = "CYBER",
                isFeatured = true,
                isDemo = true
            )
        )

        val t2Id = dao.insertTournament(
            TournamentEntity(
                title = "Nova Strike Solo Showdown [DEMO]",
                gameTitle = "Nova Strike: Zero",
                mode = "Solo",
                teamSize = 1,
                maxSlots = 32,
                filledSlots = 24,
                startTimeMillis = now + 5 * hour + 15 * 60_000L,
                status = TournamentStatus.UPCOMING,
                demoRewardPoolPoints = 2500,
                rulesSummary = "1v31 solo elimination lobby. Check in 15 minutes early. Room credentials unlock once approved by organizer.",
                mapName = "Obsidian Spire",
                roomId = "",
                roomPassword = "",
                roomPublished = false,
                bannerStyle = "STORM",
                isFeatured = true,
                isDemo = true
            )
        )

        val t3Id = dao.insertTournament(
            TournamentEntity(
                title = "Apex Grid Duo Clash [DEMO]",
                gameTitle = "Apex Grid Tactics",
                mode = "Duo",
                teamSize = 2,
                maxSlots = 24,
                filledSlots = 17,
                startTimeMillis = now + day + 3 * hour,
                status = TournamentStatus.UPCOMING,
                demoRewardPoolPoints = 3200,
                rulesSummary = "2-player duo series across 2 demo rounds. Fair play verification required before standings lock.",
                mapName = "Pulse Canyon",
                roomId = "",
                roomPassword = "",
                roomPublished = false,
                bannerStyle = "NEON",
                isFeatured = false,
                isDemo = true
            )
        )

        val t4Id = dao.insertTournament(
            TournamentEntity(
                title = "Shadow Circuit Open Cup [DEMO]",
                gameTitle = "Shadow Circuit",
                mode = "Squad",
                teamSize = 4,
                maxSlots = 16,
                filledSlots = 16,
                startTimeMillis = now - 20 * 60_000L,
                status = TournamentStatus.LIVE,
                demoRewardPoolPoints = 4000,
                rulesSummary = "Live local demo bracket. Spectators and participants can check room rules and fair play guidelines.",
                mapName = "Vault District",
                roomId = "DEMO-SC-9090",
                roomPassword = "SHADOW9",
                roomPublished = true,
                bannerStyle = "SOLAR",
                isFeatured = true,
                isDemo = true
            )
        )

        val t5Id = dao.insertTournament(
            TournamentEntity(
                title = "Obsidian Night Qualifier [DEMO]",
                gameTitle = "Cyber Rift Royale",
                mode = "Solo",
                teamSize = 1,
                maxSlots = 32,
                filledSlots = 32,
                startTimeMillis = now - 2 * day,
                status = TournamentStatus.COMPLETED,
                demoRewardPoolPoints = 3000,
                rulesSummary = "Completed demo tournament with verified sample leaderboard standings.",
                mapName = "Cryo Harbor",
                roomId = "DEMO-ON-1102",
                roomPassword = "DONE01",
                roomPublished = true,
                bannerStyle = "CYBER",
                isFeatured = false,
                isDemo = true
            )
        )

        val t6Id = dao.insertTournament(
            TournamentEntity(
                title = "Vanguard Duo Invitational [DEMO]",
                gameTitle = "Nova Strike: Zero",
                mode = "Duo",
                teamSize = 2,
                maxSlots = 20,
                filledSlots = 20,
                startTimeMillis = now - 5 * day,
                status = TournamentStatus.COMPLETED,
                demoRewardPoolPoints = 3500,
                rulesSummary = "Completed demo duo showcase. Points awarded to top 3 teams.",
                mapName = "Aether Ridge",
                roomId = "DEMO-VD-5520",
                roomPassword = "DONE02",
                roomPublished = true,
                bannerStyle = "STORM",
                isFeatured = false,
                isDemo = true
            )
        )

        // Seed Registrations (Player already registered in t1 APPROVED, t2 PENDING, plus past completed t5 & t6)
        dao.insertRegistrations(
            listOf(
                RegistrationEntity(
                    tournamentId = t1Id,
                    tournamentTitle = "Cyber Rift Masters [DEMO]",
                    mode = "Squad",
                    playerNickname = defaultProfile.nickname,
                    gameUid = defaultProfile.gameUid,
                    teammateNames = "ZephyrK, PulseByte, IronMantis",
                    status = RegistrationStatus.APPROVED,
                    registeredAtMillis = now - 6 * hour,
                    isCurrentPlayer = true
                ),
                RegistrationEntity(
                    tournamentId = t2Id,
                    tournamentTitle = "Nova Strike Solo Showdown [DEMO]",
                    mode = "Solo",
                    playerNickname = defaultProfile.nickname,
                    gameUid = defaultProfile.gameUid,
                    teammateNames = "",
                    status = RegistrationStatus.PENDING_APPROVAL,
                    registeredAtMillis = now - 2 * hour,
                    isCurrentPlayer = true
                ),
                RegistrationEntity(
                    tournamentId = t5Id,
                    tournamentTitle = "Obsidian Night Qualifier [DEMO]",
                    mode = "Solo",
                    playerNickname = defaultProfile.nickname,
                    gameUid = defaultProfile.gameUid,
                    teammateNames = "",
                    status = RegistrationStatus.APPROVED,
                    registeredAtMillis = now - 3 * day,
                    isCurrentPlayer = true
                ),
                RegistrationEntity(
                    tournamentId = t6Id,
                    tournamentTitle = "Vanguard Duo Invitational [DEMO]",
                    mode = "Duo",
                    playerNickname = defaultProfile.nickname,
                    gameUid = defaultProfile.gameUid,
                    teammateNames = "ZephyrK",
                    status = RegistrationStatus.APPROVED,
                    registeredAtMillis = now - 6 * day,
                    isCurrentPlayer = true
                ),
                // Another sample player pending approval for Admin Dashboard demo
                RegistrationEntity(
                    tournamentId = t3Id,
                    tournamentTitle = "Apex Grid Duo Clash [DEMO]",
                    mode = "Duo",
                    playerNickname = "KryptonAce",
                    gameUid = "BA-332190",
                    teammateNames = "Solaris99",
                    status = RegistrationStatus.PENDING_APPROVAL,
                    registeredAtMillis = now - 90 * 60_000L,
                    isCurrentPlayer = false
                )
            )
        )

        // Seed Verified Demo Match Results
        dao.insertMatchResults(
            listOf(
                // Completed Tournament 5
                MatchResultEntity(
                    tournamentId = t5Id,
                    tournamentTitle = "Obsidian Night Qualifier [DEMO]",
                    mode = "Solo",
                    rank = 1,
                    playerNickname = defaultProfile.nickname,
                    gameUid = defaultProfile.gameUid,
                    kills = 9,
                    placementPoints = 15,
                    totalPoints = 33,
                    demoPointsAwarded = 800,
                    completedAtMillis = now - 2 * day,
                    isVerifiedDemo = true,
                    isCurrentPlayer = true
                ),
                MatchResultEntity(
                    tournamentId = t5Id,
                    tournamentTitle = "Obsidian Night Qualifier [DEMO]",
                    mode = "Solo",
                    rank = 2,
                    playerNickname = "KryptonAce",
                    gameUid = "BA-332190",
                    kills = 7,
                    placementPoints = 12,
                    totalPoints = 26,
                    demoPointsAwarded = 500,
                    completedAtMillis = now - 2 * day,
                    isVerifiedDemo = true,
                    isCurrentPlayer = false
                ),
                MatchResultEntity(
                    tournamentId = t5Id,
                    tournamentTitle = "Obsidian Night Qualifier [DEMO]",
                    mode = "Solo",
                    rank = 3,
                    playerNickname = "ShadowPulse",
                    gameUid = "BA-519044",
                    kills = 5,
                    placementPoints = 10,
                    totalPoints = 20,
                    demoPointsAwarded = 300,
                    completedAtMillis = now - 2 * day,
                    isVerifiedDemo = true,
                    isCurrentPlayer = false
                ),
                // Completed Tournament 6
                MatchResultEntity(
                    tournamentId = t6Id,
                    tournamentTitle = "Vanguard Duo Invitational [DEMO]",
                    mode = "Duo",
                    rank = 1,
                    playerNickname = "FrostByte & EchoV",
                    gameUid = "BA-119820",
                    kills = 11,
                    placementPoints = 15,
                    totalPoints = 37,
                    demoPointsAwarded = 900,
                    completedAtMillis = now - 5 * day,
                    isVerifiedDemo = true,
                    isCurrentPlayer = false
                ),
                MatchResultEntity(
                    tournamentId = t6Id,
                    tournamentTitle = "Vanguard Duo Invitational [DEMO]",
                    mode = "Duo",
                    rank = 2,
                    playerNickname = defaultProfile.nickname,
                    gameUid = defaultProfile.gameUid,
                    kills = 6,
                    placementPoints = 12,
                    totalPoints = 24,
                    demoPointsAwarded = 550,
                    completedAtMillis = now - 5 * day,
                    isVerifiedDemo = true,
                    isCurrentPlayer = true
                ),
                MatchResultEntity(
                    tournamentId = t6Id,
                    tournamentTitle = "Vanguard Duo Invitational [DEMO]",
                    mode = "Duo",
                    rank = 3,
                    playerNickname = "VortexTwin",
                    gameUid = "BA-774120",
                    kills = 4,
                    placementPoints = 10,
                    totalPoints = 18,
                    demoPointsAwarded = 350,
                    completedAtMillis = now - 5 * day,
                    isVerifiedDemo = true,
                    isCurrentPlayer = false
                ),
                // Extra past match record for Game History depth
                MatchResultEntity(
                    tournamentId = t4Id,
                    tournamentTitle = "Pre-Season Warmup Scrim [DEMO]",
                    mode = "Squad",
                    rank = 4,
                    playerNickname = defaultProfile.nickname,
                    gameUid = defaultProfile.gameUid,
                    kills = 5,
                    placementPoints = 8,
                    totalPoints = 18,
                    demoPointsAwarded = 250,
                    completedAtMillis = now - 8 * day,
                    isVerifiedDemo = true,
                    isCurrentPlayer = true
                )
            )
        )

        // Seed Wallet Transactions (Strictly Demo Points - Zero Real Money)
        dao.insertWalletTransactions(
            listOf(
                WalletTransactionEntity(
                    title = "1st Place — Obsidian Night [DEMO]",
                    description = "Verified demo placement bonus (Not real money)",
                    pointsDelta = 800,
                    type = WalletTxType.MATCH_REWARD,
                    timestampMillis = now - 2 * day
                ),
                WalletTransactionEntity(
                    title = "Reward Claim — Arena Legend Badge",
                    description = "Demo digital collectible redemption",
                    pointsDelta = -350,
                    type = WalletTxType.REWARD_CLAIM,
                    timestampMillis = now - 3 * day
                ),
                WalletTransactionEntity(
                    title = "2nd Place — Vanguard Duo [DEMO]",
                    description = "Verified demo duo placement reward",
                    pointsDelta = 550,
                    type = WalletTxType.MATCH_REWARD,
                    timestampMillis = now - 5 * day
                ),
                WalletTransactionEntity(
                    title = "Welcome Starter Demo Points",
                    description = "Free demo balance for testing rewards & tournaments",
                    pointsDelta = 450,
                    type = WalletTxType.PARTICIPATION_BONUS,
                    timestampMillis = now - 9 * day
                )
            )
        )

        // Seed Eligible Demo Rewards
        dao.insertRewardItems(
            listOf(
                RewardItemEntity(
                    title = "Cyber Champion Avatar Frame [DEMO]",
                    description = "Neon purple & electric-blue profile border unlocked in demo mode.",
                    category = "Avatar Frame",
                    pointsCost = 400,
                    iconKey = "FRAME"
                ),
                RewardItemEntity(
                    title = "Arena Legend Digital Badge [DEMO]",
                    description = "Displayed beside your player handle in demo tournament brackets.",
                    category = "Badge",
                    pointsCost = 350,
                    iconKey = "BADGE"
                ),
                RewardItemEntity(
                    title = "Season 1 Demo Trophy Pass [DEMO]",
                    description = "Unlocks access to showcase demo invitational brackets.",
                    category = "Trophy Pass",
                    pointsCost = 900,
                    iconKey = "TROPHY"
                ),
                RewardItemEntity(
                    title = "Elite Squad Banner Pack [DEMO]",
                    description = "Custom holographic squad card backdrop for demo lobbies.",
                    category = "Banner",
                    pointsCost = 600,
                    iconKey = "BANNER"
                ),
                RewardItemEntity(
                    title = "Founder Demo Certificate [DEMO]",
                    description = "Commemorative digital tournament participant milestone.",
                    category = "Milestone",
                    pointsCost = 1200,
                    iconKey = "CERTIFICATE"
                )
            )
        )

        // Seed Reward Claims showing all four statuses clearly
        dao.insertRewardClaims(
            listOf(
                RewardClaimEntity(
                    rewardId = 2,
                    rewardTitle = "Arena Legend Digital Badge [DEMO]",
                    pointsCost = 350,
                    playerNickname = defaultProfile.nickname,
                    gameUid = defaultProfile.gameUid,
                    status = RewardClaimStatus.PAID,
                    adminNote = "Demo fulfillment marked complete by Admin.",
                    requestedAtMillis = now - 3 * day,
                    updatedAtMillis = now - 2 * day
                ),
                RewardClaimEntity(
                    rewardId = 1,
                    rewardTitle = "Cyber Champion Avatar Frame [DEMO]",
                    pointsCost = 400,
                    playerNickname = defaultProfile.nickname,
                    gameUid = defaultProfile.gameUid,
                    status = RewardClaimStatus.PENDING_VERIFICATION,
                    adminNote = "Pending Admin demo review.",
                    requestedAtMillis = now - 4 * hour,
                    updatedAtMillis = now - 4 * hour
                ),
                RewardClaimEntity(
                    rewardId = 4,
                    rewardTitle = "Elite Squad Banner Pack [DEMO]",
                    pointsCost = 600,
                    playerNickname = "KryptonAce",
                    gameUid = "BA-332190",
                    status = RewardClaimStatus.APPROVED,
                    adminNote = "Approved for demo fulfillment queue.",
                    requestedAtMillis = now - 1 * day,
                    updatedAtMillis = now - 12 * hour
                ),
                RewardClaimEntity(
                    rewardId = 3,
                    rewardTitle = "Season 1 Demo Trophy Pass [DEMO]",
                    pointsCost = 900,
                    playerNickname = "ShadowPulse",
                    gameUid = "BA-519044",
                    status = RewardClaimStatus.REJECTED,
                    adminNote = "Duplicate claim test rejected in demo verification.",
                    requestedAtMillis = now - 4 * day,
                    updatedAtMillis = now - 3 * day
                )
            )
        )

        // Seed Announcements
        dao.insertAnnouncements(
            listOf(
                AnnouncementEntity(
                    title = "Welcome to Battle Arena Tournaments (Local Demo)",
                    body = "All tournaments, points, results, and rewards in this build run locally on your Android device. Zero entry fees and no real-money features.",
                    tag = "DEMO",
                    timestampMillis = now - 1 * hour
                ),
                AnnouncementEntity(
                    title = "Cyber Rift Masters Room Credentials Live",
                    body = "Approved squads can now inspect the demo Room ID and Password inside the My Matches screen.",
                    tag = "SCHEDULE",
                    timestampMillis = now - 3 * hour
                ),
                AnnouncementEntity(
                    title = "Season 1 Fair Play & Anti-Cheat Reminder",
                    body = "All participants must keep original game UIDs accurate. Review the Rules & Fair Play screen before entering any bracket.",
                    tag = "FAIR PLAY",
                    timestampMillis = now - 1 * day
                )
            )
        )

        // Seed Managed Players for Admin Dashboard
        dao.insertManagedPlayers(
            listOf(
                ManagedPlayerEntity(
                    nickname = defaultProfile.nickname,
                    gameUid = defaultProfile.gameUid,
                    region = defaultProfile.region,
                    matchesPlayed = 3,
                    wins = 1,
                    demoPoints = defaultProfile.demoPointsBalance,
                    isSuspended = false,
                    isCurrentUser = true
                ),
                ManagedPlayerEntity(
                    nickname = "KryptonAce",
                    gameUid = "BA-332190",
                    region = "South Asia",
                    matchesPlayed = 5,
                    wins = 2,
                    demoPoints = 1920,
                    isSuspended = false,
                    isCurrentUser = false
                ),
                ManagedPlayerEntity(
                    nickname = "ShadowPulse",
                    gameUid = "BA-519044",
                    region = "SEA",
                    matchesPlayed = 4,
                    wins = 0,
                    demoPoints = 780,
                    isSuspended = false,
                    isCurrentUser = false
                ),
                ManagedPlayerEntity(
                    nickname = "FrostByte",
                    gameUid = "BA-119820",
                    region = "Europe",
                    matchesPlayed = 6,
                    wins = 3,
                    demoPoints = 2400,
                    isSuspended = false,
                    isCurrentUser = false
                )
            )
        )
    }
}
