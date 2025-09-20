package com.example.playlistmaker.search.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.ui.TrackParcelable
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.TrackAdapter
import com.example.playlistmaker.search.ui.toDomain
import com.example.playlistmaker.search.ui.view_model.SearchScreenState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val searchViewModel: SearchViewModel by viewModel()
    private val trackAdapter = TrackAdapter(mutableListOf()) { track ->
        handleTrackClick(track)
    }
    private val historyAdapter = TrackAdapter(mutableListOf()) { track ->
        handleTrackClick(track)
    }

    private lateinit var queryInput: EditText
    private lateinit var clearButton: ImageView
    private lateinit var noResultsLayout: LinearLayout
    private lateinit var networkErrorLayout: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyTitle: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var refreshButton: Button

    private val debounceDelay = 3000L
    private var isUserTyping = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerViews()
        setupClickListeners()
        setupSearchField()
        observeViewModel()

        val savedQuery = searchViewModel.getCurrentQuery()
        if (savedQuery.isNotEmpty()) {
            queryInput.setText(savedQuery)
            updateClearButtonVisibility(savedQuery)
        }
    }

    private fun initViews(view: View) {
        queryInput = view.findViewById(R.id.search_field)
        clearButton = view.findViewById(R.id.clear_icon)
        noResultsLayout = view.findViewById(R.id.no_results)
        networkErrorLayout = view.findViewById(R.id.network_error)
        progressBar = view.findViewById(R.id.progressBar)
        historyRecyclerView = view.findViewById(R.id.recycler_view_history)
        historyLayout = view.findViewById(R.id.history_list)
        historyTitle = view.findViewById(R.id.history)
        clearHistoryButton = view.findViewById(R.id.clear_history_btn)
        refreshButton = view.findViewById(R.id.refresh_btn)
    }

    private fun setupRecyclerViews() {
        requireView().findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }

        historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun setupClickListeners() {
        clearHistoryButton.setOnClickListener {
            searchViewModel.clearSearchHistory()
            hideSearchHistory()
        }

        clearButton.setOnClickListener {
            queryInput.text.clear()
            searchViewModel.clearSearchResults()
            trackAdapter.updateTracks(emptyList())
            hideKeyboard()
            showSearchHistoryAsync()
        }

        refreshButton.setOnClickListener {
            val currentQuery = queryInput.text.toString().trim()
            if (currentQuery.isNotEmpty()) {
                performSearch(currentQuery)
            }
        }

        queryInput.setOnClickListener {
            if (queryInput.text.isNullOrEmpty()) {
                showSearchHistoryAsync()
            }
        }
    }

    private fun setupSearchField() {
        queryInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && queryInput.text.isNullOrEmpty()) {
                showSearchHistoryAsync()
            }
        }

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val query = queryInput.text.toString().trim()
                if (query.isNotEmpty()) {
                    performSearch(query)
                    hideKeyboard()
                }
                true
            } else {
                false
            }
        }

        val searchFlow = callbackFlow<String> {
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val currentText = s?.toString() ?: ""
                    trySend(currentText)
                    updateClearButtonVisibility(currentText)

                    if (currentText.isEmpty()) {
                        isUserTyping = false
                        showSearchHistoryAsync()
                    } else {
                        isUserTyping = true
                        hideSearchHistory()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            }
            queryInput.addTextChangedListener(textWatcher)
            awaitClose { queryInput.removeTextChangedListener(textWatcher) }
        }

        searchFlow
            .debounce(debounceDelay)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isNotEmpty()) {
                    performSearch(query)
                } else {
                    showSearchHistoryAsync()
                }
                isUserTyping = false
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun updateClearButtonVisibility(text: String) {
        clearButton.visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            showSearchHistoryAsync()
            return
        }

        hideSearchHistory()
        searchViewModel.searchTracks(query)
    }

    private fun showSearchHistoryAsync() {
        if (isUserTyping || !queryInput.text.isNullOrEmpty()) return

        lifecycleScope.launch {
            searchViewModel.loadSearchHistory()
            val state = searchViewModel.screenState.value
            if (state?.historyList?.isNotEmpty() == true) {
                showSearchHistory()
            } else {
                hideSearchHistory()
            }
        }
    }

    private fun showSearchHistory() {
        historyLayout.visibility = View.VISIBLE
        historyTitle.visibility = View.VISIBLE
        clearHistoryButton.visibility = View.VISIBLE
    }

    private fun hideSearchHistory() {
        historyLayout.visibility = View.GONE
        historyTitle.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }

    private fun observeViewModel() {
        searchViewModel.screenState.observe(viewLifecycleOwner) { state ->
            handleScreenState(state)
        }
    }

    private fun handleScreenState(state: SearchScreenState) {
        progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        noResultsLayout.visibility = if (state.showNoResults) View.VISIBLE else View.GONE
        networkErrorLayout.visibility = if (state.showNetworkError) View.VISIBLE else View.GONE

        if (state.trackList.isNullOrEmpty()) {
            trackAdapter.updateTracks(emptyList())
        } else {
            trackAdapter.updateTracks(state.trackList ?: emptyList())
        }

        state.historyList?.let { historyList ->
            historyAdapter.updateTracks(historyList)
            if (historyList.isNotEmpty() && queryInput.text.isNullOrEmpty() && !isUserTyping) {
                showSearchHistory()
            }
        }
    }

    private fun handleTrackClick(track: TrackParcelable) {
        addToHistoryAndNavigate(track)
    }

    private fun addToHistoryAndNavigate(track: TrackParcelable) {
        searchViewModel.addToSearchHistory(track.toDomain())
        navigateToPlayer(track)
    }

    private fun navigateToPlayer(track: TrackParcelable) {
        val direction = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(track)
        findNavController().navigate(direction)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm.hideSoftInputFromWindow(queryInput.windowToken, 0)
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}