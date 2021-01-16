package com.github.ziem.simplesmsscheduler.database

import androidx.room.*
import com.github.ziem.simplesmsscheduler.model.State
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface MessagesDao {
    @Query("SELECT * FROM scheduled_message")
    fun getAll(): Observable<List<DatabaseMessage>>

    @Query("SELECT * FROM scheduled_message ORDER BY datetime(date_time)")
    fun getAllOrdered(): Observable<List<DatabaseMessage>>

    @Query("SELECT * FROM scheduled_message WHERE datetime(date_time) >= datetime('now')")
    fun getFuture(): Single<List<DatabaseMessage>>

    @Query("SELECT * FROM scheduled_message WHERE id = :messageId")
    fun get(messageId: String): Single<DatabaseMessage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(databaseMessage: DatabaseMessage): Completable

    @Query("DELETE FROM scheduled_message WHERE state >= :state")
    fun deleteAll(state: State): Completable

    @Delete
    fun delete(databaseMessage: DatabaseMessage): Completable

    @Query("DELETE FROM scheduled_message WHERE id = :messageId")
    fun delete(messageId: String): Completable

    @Update
    fun update(databaseMessage: DatabaseMessage): Completable
}
