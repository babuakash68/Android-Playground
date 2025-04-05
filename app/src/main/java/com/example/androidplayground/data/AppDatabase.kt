package com.example.androidplayground.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [User::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Define the migration from version 1 to 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the photoUrl column to the users table
                database.execSQL("ALTER TABLE users ADD COLUMN photoUrl TEXT")
            }
        }

        // Define the migration from version 2 to 3
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add any new columns or changes needed for version 3
                // For now, we'll just recreate the table with the correct schema
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS users_new (
                        email TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        photoUrl TEXT,
                        isLoggedIn INTEGER NOT NULL DEFAULT 1,
                        lastLoginTime INTEGER NOT NULL DEFAULT 0
                    )
                """)
                
                // Copy data from old table to new table
                database.execSQL("""
                    INSERT INTO users_new (email, name, photoUrl, isLoggedIn, lastLoginTime)
                    SELECT email, name, photoUrl, isLoggedIn, lastLoginTime FROM users
                """)
                
                // Remove old table
                database.execSQL("DROP TABLE users")
                
                // Rename new table to original name
                database.execSQL("ALTER TABLE users_new RENAME TO users")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // Add both migrations
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 