package com.github.ziem.simplesmsscheduler.feature.add_edit_message

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.ziem.simplesmsscheduler.R
import com.github.ziem.simplesmsscheduler.alarm.AlarmScheduler
import com.github.ziem.simplesmsscheduler.binding.viewBinding
import com.github.ziem.simplesmsscheduler.contact.ContactsDao
import com.github.ziem.simplesmsscheduler.databinding.FragmentAddEditMessageBinding
import com.github.ziem.simplesmsscheduler.model.Message
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.jakewharton.rxbinding3.widget.textChanges
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import javax.inject.Inject


class AddEditMessageFragment : BottomSheetDialogFragment(), TimePickerDialog.OnTimeSetListener {
    companion object {
        const val PICK_CONTACT_REQUEST_CODE = 12345
        const val PERMISSION_REQUEST_CODE = 54321
    }

    @Inject
    lateinit var messageViewModelFactory: AddEditMessageViewModelFactory

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var contactsDao: ContactsDao

    private val binding by viewBinding(FragmentAddEditMessageBinding::bind)

    private lateinit var viewModel: AddEditMessageViewModel

    private val disposables = CompositeDisposable()

    private val timeChangeRelay = PublishRelay.create<OffsetDateTime>()

    private val args: AddEditMessageFragmentArgs by navArgs()

    private val dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, messageViewModelFactory)
            .get(AddEditMessageViewModel::class.java)

        if (savedInstanceState == null) {
            viewModel.dispatch(Action.InitMessage(args.message))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_edit_message, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior: BottomSheetBehavior<View> = BottomSheetBehavior.from(bottomSheet)
            behavior.skipCollapsed = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observableState.observe(this, Observer { state ->
            state?.let { renderState(state) }
        })

        disposables += binding.chip.clicks()
            .subscribe {
                chooseContact()
            }

        disposables += binding.schedule.clicks()
            .subscribe {
                viewModel.dispatch(Action.SaveMessage)
            }

        disposables += binding.delete.clicks()
            .subscribe {
                viewModel.dispatch(Action.DeleteMessage(args.message.id))
            }

        disposables += binding.message.textChanges()
            .skipInitialValue()
            .subscribe {
                viewModel.dispatch(Action.ApplyMessage(it.toString()))
            }

        disposables += binding.time.clicks()
            .subscribe {
                val currentDateTime =
                    viewModel.observableState.value?.message?.dateTime ?: OffsetDateTime.now()
                TimePickerDialog(
                    requireContext(),
                    this,
                    currentDateTime.hour,
                    currentDateTime.minute,
                    true
                ).show()
            }

        disposables += binding.sendAutomatically.checkedChanges()
            .skip(1)
            .subscribe {
                if (isSendSmsPermissionGranted()) {
                    viewModel.dispatch(Action.SendAutomaticallyRun(it))
                } else {
                    if (it == true) {
                        binding.sendAutomatically.isChecked = false
                        viewModel.dispatch(Action.SendAutomaticallyRun(false))
                        requestPermissions(
                            arrayOf(Manifest.permission.SEND_SMS),
                            PERMISSION_REQUEST_CODE
                        )
                    }
                }
            }

        disposables += timeChangeRelay.subscribe {
            viewModel.dispatch(Action.ApplyDateTime(it))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: Uri? = data?.data
        if (isResultValid(requestCode, resultCode, result)) {
            disposables += contactsDao.getContact(requireContext(), result!!)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.dispatch(Action.ApplyContact(it))
                }, {
                    Timber.e(it)
                })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            binding.sendAutomatically.isChecked = true
            viewModel.dispatch(Action.SendAutomaticallyRun(true))
        }
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val selectedDateTime = OffsetDateTime.now()
            .withHour(hourOfDay)
            .withMinute(minute)
            .withSecond(0)
            .withNano(0)

        timeChangeRelay.accept(selectedDateTime)
    }

    private fun isResultValid(requestCode: Int, resultCode: Int, result: Uri?): Boolean {
        return resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT_REQUEST_CODE && result != null
    }

    private fun chooseContact() {
        val contactPickerIntent = Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }
        contactPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivityForResult(contactPickerIntent, PICK_CONTACT_REQUEST_CODE)
    }

    private fun renderState(state: State) {
        with(state) {
            when {
                isMessageDeleted -> renderMessageDeleted()
                isMessageSaved -> renderMessageSaved()
                isLoading -> renderLoadingState()
                error != null -> renderLoadMessageError(error)
                else -> renderMessageState(message, isIdle)
            }

            renderValidity(state.isValid)
        }
    }

    private fun renderLoadingState() {
        binding.schedule.visibility = View.GONE
        binding.progress.visibility = View.VISIBLE
    }

    private fun renderMessageState(message: Message, isIdle: Boolean) {
        if (isIdle) {
            binding.message.setText(message.message)
            binding.message.setSelection(message.message.length)
        }
        if (message.contactName.isBlank()) {
            binding.chip.text = "Select contact"
            binding.chip.setChipIconResource(R.drawable.ic_person_add_24dp)
        } else {
            binding.chip.text = message.contactName
            binding.chip.setChipIconResource(R.drawable.ic_baseline_person_24)
        }
        binding.schedule.visibility = View.VISIBLE
        binding.progress.visibility = View.GONE
        binding.time.text = dateTimeFormat.format(message.dateTime)

        binding.sendAutomatically.isChecked = message.sendAutomatically
    }

    private fun renderValidity(isValid: Boolean) {
        binding.schedule.isEnabled = isValid
    }

    private fun renderLoadMessageError(error: Throwable) {
        Toast.makeText(requireContext(), "$error", Toast.LENGTH_LONG).show()
    }

    private fun renderMessageDeleted() {
        findNavController().popBackStack()
    }

    private fun renderMessageSaved() {
        findNavController().popBackStack()
    }

    private fun isSendSmsPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }
}