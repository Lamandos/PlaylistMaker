package com.example.playlistmaker.db.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PlaylistEntity::class, PlaylistTrackEntity::class,PlaylistTrackRelation::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabasePlaylist : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabasePlaylist? = null

        fun getDatabase(context: Context): AppDatabasePlaylist {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabasePlaylist::class.java,
                    "playlist_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

