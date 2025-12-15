package com.example.dicodingevent.ui.finished

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.R
import com.example.dicodingevent.data.model.EventItem
import com.example.dicodingevent.databinding.FragmentEventListBinding
import com.example.dicodingevent.di.ViewModelFactory
import com.example.dicodingevent.ui.detail.DetailEventActivity
import com.example.dicodingevent.ui.list.EventAdapter
import com.example.dicodingevent.ui.viewmodel.EventResult
import com.example.dicodingevent.ui.viewmodel.EventViewModel

class FinishedEventFragment : Fragment() {

    private var _binding: FragmentEventListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventAdapter = EventAdapter(
            onItemClick = { event -> navigateToDetail(event) },
            onFavoriteClick = { event -> viewModel.toggleFavorite(event) }
        )

        binding.rvEventList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEventList.adapter = eventAdapter

        observeEvents(eventAdapter)
    }

    private fun observeEvents(adapter: EventAdapter) {
        viewModel.finishedEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is EventResult.Loading -> showLoading(true)
                is EventResult.Success -> {
                    showLoading(false)
                    val data = result.data
                    if (data.isNullOrEmpty()) {
                        showStatusMessage(true, getString(R.string.status_no_events_found))
                        adapter.submitList(emptyList())
                    } else {
                        showStatusMessage(false, null)
                        adapter.submitList(data)
                    }
                }
                is EventResult.Error -> {
                    showLoading(false)
                    showStatusMessage(true, "Error: ${result.exception.message}")
                }
            }
        }
    }

    private fun navigateToDetail(event: EventItem) {
        val intent = Intent(activity, DetailEventActivity::class.java)
        intent.putExtra(DetailEventActivity.EXTRA_EVENT_ID, event.id)
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvEventList.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showStatusMessage(isVisible: Boolean, message: String?) {
        binding.tvErrorMessage.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.tvErrorMessage.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}