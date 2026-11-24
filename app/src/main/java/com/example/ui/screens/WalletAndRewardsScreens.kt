package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.RewardClaimEntity
import com.example.data.local.RewardItemEntity
import com.example.data.local.WalletTransactionEntity
import com.example.ui.components.ArenaConfirmDialog
import com.example.ui.components.ArenaSectionHeader
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.EmptyArenaState
import com.example.ui.components.RewardClaimStatusPill
import com.example.ui.components.StatusPill
import com.example.ui.components.formatDateTime
import com.example.ui.navigation.ArenaRoutes
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.StatusCrimson
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.TrophyGold
import com.example.ui.viewmodel.ArenaUiState

/**
 * Screen 6: WALLET — Strictly Demo Points balance, ledger history, claim status, and reward previews.
 * Never represents demo points as real money; no deposits, UPI PINs, bank details, or self-award buttons.
 */
@Composable
fun WalletScreen(
    uiState: ArenaUiState,
    onNavigate: (String) -> Unit,
    onClaimReward: (RewardItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val balance = uiState.profile?.demoPointsBalance ?: 0
    var pendingRewardConfirm by remember { mutableStateOf<RewardItemEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("wallet_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DemoModeBanner("DEMO POINTS ONLY — These points have zero cash value and are not real money. No deposits, entry fees, or payment gateways.")
        }

        // Balance Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, ElectricPurple.copy(alpha = 0.6f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1D103B), Color(0xFF10243E), Color(0xFF0E1322))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AccountBalanceWallet,
                                    contentDescription = "Demo Wallet",
                                    tint = TrophyGold,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "DEMO POINTS BALANCE",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = ElectricBlue
                                )
                            }
                            StatusPill(
                                label = "NOT REAL MONEY",
                                containerColor = TrophyGold.copy(alpha = 0.18f),
                                contentColor = TrophyGold
                            )
                        }

                        Text(
                            text = "$balance DEMO PTS",
                            style = MaterialTheme.typography.displayLarge,
                            color = Color.White,
                            modifier = Modifier.testTag("wallet_balance_value")
                        )

                        Text(
                            text = "Earn demo points only from Admin-verified demo match results. Redeem for demo digital collectibles below.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFC7D3EC)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { onNavigate(ArenaRoutes.REWARDS) },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("wallet_open_rewards_btn")
                            ) {
                                Icon(Icons.Filled.CardGiftcard, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Browse Demo Rewards")
                            }
                        }
                    }
                }
            }
        }

        // Available Demo Rewards Quick Preview
        item {
            ArenaSectionHeader(
                title = "Available Demo Rewards",
                subtitle = "Redeem using your demo points",
                actionLabel = "All Rewards",
                onActionClick = { onNavigate(ArenaRoutes.REWARDS) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                uiState.rewardItems.take(2).forEach { reward ->
                    RewardCatalogCard(
                        reward = reward,
                        canAfford = balance >= reward.pointsCost,
                        onClaimClick = { pendingRewardConfirm = reward }
                    )
                }
            }
        }

        // Recent Claims
        item {
            ArenaSectionHeader(
                title = "Recent Reward Claims",
                subtitle = "Verified by Admin in demo mode",
                actionLabel = "View Claims",
                onActionClick = { onNavigate(ArenaRoutes.REWARDS) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            val myClaims = uiState.rewardClaims.filter {
                it.gameUid.equals(uiState.profile?.gameUid, ignoreCase = true)
            }.take(3)
            if (myClaims.isEmpty()) {
                EmptyArenaState(
                    icon = Icons.Filled.CardGiftcard,
                    title = "No Claims Yet",
                    message = "Claim any eligible demo reward above to see its verification status."
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    myClaims.forEach { claim ->
                        RewardClaimRowCard(claim)
                    }
                }
            }
        }

        // Transaction History
        item {
            ArenaSectionHeader(
                title = "Demo Points Transaction Ledger",
                subtitle = "Read-only local history"
            )
        }

        if (uiState.walletTransactions.isEmpty()) {
            item {
                EmptyArenaState(
                    icon = Icons.Filled.AccountBalanceWallet,
                    title = "No Transactions",
                    message = "Your demo points activity will be recorded here."
                )
            }
        } else {
            items(uiState.walletTransactions, key = { it.id }) { tx ->
                WalletTransactionRow(tx)
            }
        }
    }

    pendingRewardConfirm?.let { reward ->
        ArenaConfirmDialog(
            title = "Confirm Demo Reward Claim",
            body = "Redeem \"${reward.title}\" for ${reward.pointsCost} DEMO PTS? Your claim will start as Pending Verification for Admin review.",
            confirmLabel = "Submit Claim",
            onConfirm = {
                onClaimReward(reward)
                pendingRewardConfirm = null
            },
            onDismiss = { pendingRewardConfirm = null }
        )
    }
}

/**
 * Screen 13: REWARDS — Eligible demo rewards catalog, claim submission, and claim status history.
 */
@Composable
fun RewardsScreen(
    uiState: ArenaUiState,
    onClaimReward: (RewardItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var pendingConfirm by remember { mutableStateOf<RewardItemEntity?>(null) }
    val balance = uiState.profile?.demoPointsBalance ?: 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("rewards_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Eligible Rewards (${uiState.rewardItems.size})") },
                modifier = Modifier.testTag("rewards_tab_catalog")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Claim History (${uiState.rewardClaims.size})") },
                modifier = Modifier.testTag("rewards_tab_claims")
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                    border = BorderStroke(1.dp, ElectricPurple.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Available Balance (Demo Only)", style = MaterialTheme.typography.bodySmall)
                            Text("$balance DEMO PTS", style = MaterialTheme.typography.titleLarge, color = TrophyGold)
                        }
                        StatusPill("NO REAL PAYOUTS", ElectricBlue.copy(alpha = 0.16f), ElectricBlue)
                    }
                }
            }

            if (selectedTab == 0) {
                items(uiState.rewardItems, key = { it.id }) { reward ->
                    RewardCatalogCard(
                        reward = reward,
                        canAfford = balance >= reward.pointsCost,
                        onClaimClick = { pendingConfirm = reward }
                    )
                }
            } else {
                if (uiState.rewardClaims.isEmpty()) {
                    item {
                        EmptyArenaState(
                            icon = Icons.Filled.CardGiftcard,
                            title = "No Reward Claims",
                            message = "Submit a demo reward claim from the Eligible Rewards tab to track its status."
                        )
                    }
                } else {
                    items(uiState.rewardClaims, key = { it.id }) { claim ->
                        RewardClaimRowCard(claim)
                    }
                }
            }
        }
    }

    pendingConfirm?.let { reward ->
        ArenaConfirmDialog(
            title = "Request Demo Reward",
            body = "Deduct ${reward.pointsCost} DEMO PTS to request \"${reward.title}\"? Ordinary players cannot self-approve; status stays Pending Verification until reviewed in Admin Dashboard.",
            confirmLabel = "Confirm Claim",
            onConfirm = {
                onClaimReward(reward)
                pendingConfirm = null
                selectedTab = 1
            },
            onDismiss = { pendingConfirm = null }
        )
    }
}

@Composable
private fun RewardCatalogCard(
    reward: RewardItemEntity,
    canAfford: Boolean,
    onClaimClick: () -> Unit
) {
    val icon = when (reward.iconKey) {
        "TROPHY" -> Icons.Filled.EmojiEvents
        "BADGE" -> Icons.Filled.Shield
        else -> Icons.Filled.WorkspacePremium
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElectricPurple.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = reward.category, tint = ElectricPurple, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(reward.title, style = MaterialTheme.typography.titleMedium)
                    Text(reward.category, style = MaterialTheme.typography.labelSmall, color = ElectricBlue)
                }
                Text(
                    text = "${reward.pointsCost} PTS",
                    style = MaterialTheme.typography.titleMedium,
                    color = TrophyGold,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = reward.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onClaimClick,
                enabled = canAfford,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("claim_reward_${reward.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple)
            ) {
                Text(if (canAfford) "Claim Demo Reward" else "Need More Demo Points")
            }
        }
    }
}

@Composable
private fun RewardClaimRowCard(claim: RewardClaimEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RewardClaimStatusPill(claim.status)
                Text(
                    text = "${claim.pointsCost} DEMO PTS",
                    style = MaterialTheme.typography.labelLarge,
                    color = TrophyGold
                )
            }
            Text(claim.rewardTitle, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Player: ${claim.playerNickname} (${claim.gameUid}) • ${formatDateTime(claim.requestedAtMillis)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Review Note: ${claim.adminNote}",
                style = MaterialTheme.typography.bodySmall,
                color = ElectricBlue
            )
        }
    }
}

@Composable
private fun WalletTransactionRow(tx: WalletTransactionEntity) {
    val isPositive = tx.pointsDelta >= 0
    val accent = if (isPositive) StatusEmerald else StatusCrimson

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPositive) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward,
                    contentDescription = if (isPositive) "Credit" else "Debit",
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tx.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    text = tx.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatDateTime(tx.timestampMillis),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${if (isPositive) "+" else ""}${tx.pointsDelta}",
                style = MaterialTheme.typography.titleMedium,
                color = accent,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
