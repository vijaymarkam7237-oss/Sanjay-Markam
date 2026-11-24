package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TournamentEntity::class,
        RegistrationEntity::class,
        MatchResultEntity::class,
        WalletTransactionEntity::class,
        RewardItemEntity::class,
        RewardClaimEntity::class,
        AnnouncementEntity::class,
        SupportTicketEntity::class,
        PlayerProfileEntity::class,
        ManagedPlayerEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ArenaDatabase : RoomDatabase() {
    abstract fun arenaDao(): ArenaDao

    companion object {
        @Volatile
        private var INSTANCE: ArenaDatabase? = null

        fun getInstance(context: Context): ArenaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ArenaDatabase::class.java,
                    "battle_arena_demo.db"
                ).fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
