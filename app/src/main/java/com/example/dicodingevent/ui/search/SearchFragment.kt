package com.example.dicodingevent.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.data.model.EventItem
import com.example.dicodingevent.databinding.FragmentSearchBinding
import com.example.dicodingevent.di.ViewModelFactory
import com.example.dicodingevent.ui.detail.DetailEventActivity
import com.example.dicodingevent.ui.list.EventAdapter
import com.example.dicodingevent.ui.viewmodel.EventResult
import com.example.dicodingevent.ui.viewmodel.EventViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var searchAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchAdapter = EventAdapter(
            onItemClick = { event -> navigateToDetail(event) },
            onFavoriteClick = { event -> viewModel.toggleFavorite(event) }
        )

        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchResults.adapter = searchAdapter

        setupSearchView()
        observeSearchResults()

        showStatusMessage(true, "Mulai cari event favoritmu!")
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    viewModel.searchEvents(query.trim())
                    binding.searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    searchAdapter.submitList(emptyList())
                    showStatusMessage(true, "Mulai cari event favoritmu!")
                }
                return true
            }
        })
    }

    private fun navigateToDetail(event: EventItem) {
        val intent = Intent(activity, DetailEventActivity::class.java)
        intent.putExtra(DetailEventActivity.EXTRA_EVENT_ID, event.id)
        startActivity(intent)
    }

    private fun observeSearchResults() {
        viewModel.searchResults.observe(viewLifecycleOwner) { result ->
            when (result) {
                is EventResult.Loading -> {
                    showLoading(true)
                    showStatusMessage(false, null)
                }
                is EventResult.Success -> {
                    showLoading(false)
                    val data = result.data
                    if (data.isNullOrEmpty()) {
                        showStatusMessage(true, "Event tidak ditemukan")
                    } else {
                        showStatusMessage(false, null)
                        searchAdapter.submitList(data)
                    }
                }
                is EventResult.Error -> {
                    showLoading(false)
                    showStatusMessage(true, "Terjadi kesalahan: ${result.exception.message}")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showStatusMessage(isVisible: Boolean, message: String?) {
        binding.tvNotFound.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.tvNotFound.text = message
        binding.rvSearchResults.visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}