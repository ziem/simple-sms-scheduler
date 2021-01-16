package com.github.ziem.simplesmsscheduler.feature.messages

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MessagesViewModel(
    initialState: State?,
    private val loadMessagesUseCase: LoadMessagesUseCase,
    private val deleteCompletedMessagesUseCase: DeleteCompletedMessagesUseCase
) : BaseViewModel<Action, State>() {
    override val initialState: State = initialState ?: State()

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.Loading -> state.copy(
                isLoading = true,
                messages = emptyList(),
                error = null
            )
            is Change.Messages -> state.copy(
                isLoading = false,
                messages = change.messages,
                error = null
            )
            is Change.Error -> state.copy(
                isLoading = false,
                error = change.throwable
            )
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val loadNotesChange = actions.ofType<Action.LoadMessages>()
            .switchMap {
                loadMessagesUseCase.execute()
                    .subscribeOn(Schedulers.io())
                    .map<Change> { Change.Messages(it) }
                    .defaultIfEmpty(Change.Messages(emptyList()))
                    .onErrorReturn { Change.Error(it) }
                    .startWith(Change.Loading)
            }

        val clearCompleted = actions.ofType<Action.ClearCompletedMessages>()
            .switchMap {
                deleteCompletedMessagesUseCase.execute()
                    .subscribeOn(Schedulers.io())
                    .toObservable<Change>()
            }

        disposables += Observable.merge(loadNotesChange, clearCompleted)
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(state::setValue, Timber::e)
    }
}