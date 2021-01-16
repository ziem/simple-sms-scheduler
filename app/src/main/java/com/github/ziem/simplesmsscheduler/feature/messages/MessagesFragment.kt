package com.github.ziem.simplesmsscheduler.feature.messages

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ziem.simplesmsscheduler.R
import com.github.ziem.simplesmsscheduler.binding.viewBinding
import com.github.ziem.simplesmsscheduler.databinding.FragmentMessagesBinding
import com.github.ziem.simplesmsscheduler.feature.add_edit_message.AddEditMessageFragmentArgs
import com.github.ziem.simplesmsscheduler.model.Message
import com.jakewharton.rxbinding3.appcompat.itemClicks
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class MessagesFragment : Fragment(R.layout.fragment_messages) {
    private val binding by viewBinding(FragmentMessagesBinding::bind)

    @Inject
    lateinit var recyclerViewAdapter: RecyclerViewAdapter

    @Inject
    lateinit var messagesViewModelFactory: MessagesViewModelFactory

    private val disposables = CompositeDisposable()

    private lateinit var viewModel: MessagesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, messagesViewModelFactory)
            .get(MessagesViewModel::class.java)

        if (savedInstanceState == null) {
            viewModel.dispatch(Action.LoadMessages)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.recycler.layoutManager = LinearLayoutManager(context)
        } else {
            binding.recycler.layoutManager = GridLayoutManager(context, 2)
        }

        binding.recycler.adapter = recyclerViewAdapter
        binding.recycler.itemAnimator = DefaultItemAnimator()
        binding.bar.inflateMenu(R.menu.menu_main)

        viewModel.observableState.observe(viewLifecycleOwner, Observer { state ->
            state?.let { renderState(state) }
        })

        disposables += binding.refresh.refreshes()
            .subscribe {
                viewModel.dispatch(Action.LoadMessages)
            }

        disposables += binding.fab.clicks()
            .subscribe {
                findNavController().navigate(
                    R.id.action_messagesFragment_to_addEditMessageFragment,
                    AddEditMessageFragmentArgs(Message()).toBundle()
                )
            }

        disposables += binding.bar.itemClicks()
            .subscribe { menuItem ->
                if (menuItem.itemId == R.id.clear) {
                    viewModel.dispatch(Action.ClearCompletedMessages)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    private fun renderState(state: State) {
        with(state) {
            when {
                isLoading -> renderLoadingState()
                error != null -> renderErrorState(error)
                messages.isNotEmpty() -> renderMessagesState(messages)
                else -> renderEmptyState()
            }
        }
    }

    private fun renderMessagesState(messages: List<Message>) {
        binding.recycler.visibility = View.VISIBLE
        binding.empty.visibility = View.GONE
        binding.refresh.isRefreshing = false
        recyclerViewAdapter.submitList(listOf(Title("Scheduled messages")) + messages)
    }

    private fun renderLoadingState() {
        binding.recycler.visibility = View.GONE
        binding.empty.visibility = View.GONE
        binding.refresh.isRefreshing = true
    }

    private fun renderErrorState(error: Throwable) {
        binding.recycler.visibility = View.GONE
        binding.refresh.isRefreshing = false
        binding.empty.visibility = View.GONE
        Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
    }

    private fun renderEmptyState() {
        binding.recycler.visibility = View.VISIBLE
        binding.refresh.isRefreshing = false
        binding.empty.visibility = View.VISIBLE
        recyclerViewAdapter.submitList(listOf(Title("SMS Scheduler")))
    }
}