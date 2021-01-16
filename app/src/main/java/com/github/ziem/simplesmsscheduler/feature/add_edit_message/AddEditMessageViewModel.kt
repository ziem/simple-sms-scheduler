package com.github.ziem.simplesmsscheduler.feature.add_edit_message

import com.github.ziem.simplesmsscheduler.alarm.AlarmScheduler
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.OffsetDateTime
import timber.log.Timber

class AddEditMessageViewModel(
    initialState: State?,
    private val saveMessageUseCase: SaveMessageUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val alarmScheduler: AlarmScheduler
) : BaseViewModel<Action, State>() {
    override val initialState: State = initialState ?: State()

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.Loading -> state.copy(
                isLoading = true,
                error = null
            )
            is Change.Init -> state.copy(
                isIdle = true,
                message = change.message
            )
            is Change.ContactChange -> {
                val contact = change.contact
                state.copy(
                    message = state.message.copy(
                        phoneNumber = contact.contactNumber,
                        contactName = contact.contactName,
                        contactThumbnail = contact.contactThumbnail
                    )
                )
            }
            is Change.MessageError -> state.copy(
                isLoading = false,
                error = null
            )
            Change.MessageDeleted -> state.copy(
                isLoading = false,
                isMessageDeleted = true
            )
            Change.MessageSaved -> state.copy(
                error = null,
                isMessageSaved = true
            )
            is Change.MessageChange -> state.copy(
                isIdle = false,
                message = state.message.copy(message = change.message)
            )
            is Change.DateTimeChange -> state.copy(message = state.message.copy(dateTime = change.dateTime))
            is Change.SendAutomaticallyChange -> state.copy(message = state.message.copy(sendAutomatically = change.sendAutomatically))
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val deleteMessage = actions.ofType<Action.DeleteMessage>()
            .switchMap { action ->
                deleteMessageUseCase.execute(action.messageId)
                    .andThen(alarmScheduler.removeAlarm(action.messageId))
                    .subscribeOn(Schedulers.io())
                    .toSingleDefault<Change>(Change.MessageDeleted)
                    .onErrorReturn { Change.MessageError(it) }
                    .toObservable()
                    .startWith(Change.Loading)
            }

        val initMessage = actions.ofType<Action.InitMessage>()
            .switchMap { action -> Observable.just(Change.Init(action.message)) }

        val applyContact = actions.ofType<Action.ApplyContact>()
            .switchMap { action -> Observable.just(Change.ContactChange(action.contact)) }

        val applyMessage = actions.ofType<Action.ApplyMessage>()
            .switchMap { action -> Observable.just(Change.MessageChange(action.message)) }

        val applyDateTime = actions.ofType<Action.ApplyDateTime>()
            .switchMap { action -> Observable.just(Change.DateTimeChange(action.dateTime)) }

        val applySendAutomatically = actions.ofType<Action.SendAutomaticallyRun>()
            .switchMap { action -> Observable.just(Change.SendAutomaticallyChange(action.sendAutomatically)) }

        val saveMessage = actions.ofType<Action.SaveMessage>()
            .switchMap { _ ->
                val message = state.value?.message!!
                saveMessageUseCase.execute(message)
                    .andThen(alarmScheduler.scheduleAlarm(message.id, message.dateTime))
                    .andThen(saveMessageUseCase.execute(message.copy(state = com.github.ziem.simplesmsscheduler.model.State.Scheduled)))
                    .subscribeOn(Schedulers.io())
                    .toSingleDefault<Change>(Change.MessageSaved)
                    .onErrorReturn { Change.MessageError(it) }
                    .toObservable()
                    .startWith(Change.Loading)
            }

        val listOfObservables = listOf(
            deleteMessage,
            initMessage,
            applyContact,
            applyMessage,
            applyDateTime,
            applySendAutomatically,
            saveMessage
        )

        val allChanges = Observable.merge(listOfObservables)

        disposables += allChanges
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .map { it.copy(isValid = validateState(it)) }
            .subscribe(state::postValue, Timber::e)
    }

    private fun validateState(state: State): Boolean {
        val message = state.message
        return message.message.isNotBlank()
                && message.contactName.isNotBlank()
                && message.dateTime.isAfter(OffsetDateTime.now())
    }
}