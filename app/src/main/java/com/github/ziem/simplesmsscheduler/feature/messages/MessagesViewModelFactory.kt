package com.github.ziem.simplesmsscheduler.feature.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesViewModelFactory @Inject constructor(
    private val loadMessagesUseCase: LoadMessagesUseCase,
    private val deleteCompletedMessagesUseCase: DeleteCompletedMessagesUseCase
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MessagesViewModel(
            null,
            loadMessagesUseCase,
            deleteCompletedMessagesUseCase
        ) as T
    }
}