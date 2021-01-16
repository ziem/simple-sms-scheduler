package com.github.ziem.simplesmsscheduler.di

import android.app.Application
import androidx.room.Room
import com.github.ziem.simplesmsscheduler.database.AppDatabase
import com.github.ziem.simplesmsscheduler.database.MessagesDao
import toothpick.config.Module
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class DatabaseProvider : Provider<AppDatabase> {
    @Inject
    lateinit var application: Application

    override fun get(): AppDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            AppDatabase::class.java,
            "ssmss_database"
        ).build()
    }
}

@Singleton
class MessagesDaoProvider : Provider<MessagesDao> {
    @Inject
    lateinit var appDatabase: AppDatabase

    override fun get(): MessagesDao {
        return appDatabase.messagesDao()
    }
}

object DatabaseModule : Module() {
    init {
        bind(AppDatabase::class.java).toProvider(DatabaseProvider::class.java)
        bind(MessagesDao::class.java).toProvider(MessagesDaoProvider::class.java)
    }
}