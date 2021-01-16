package com.github.ziem.simplesmsscheduler.feature.add_edit_message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.ziem.simplesmsscheduler.alarm.AlarmScheduler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddEditMessageViewModelFactory @Inject constructor(
    private val saveMessageUseCase: SaveMessageUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val alarmScheduler: AlarmScheduler
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddEditMessageViewModel(
            null,
            saveMessageUseCase,
            deleteMessageUseCase,
            alarmScheduler
        ) as T
    }
}