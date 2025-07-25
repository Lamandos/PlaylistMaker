package com.example.playlistmaker.search.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.ui.TrackParcelable
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.TrackAdapter
import com.example.playlistmaker.search.ui.toDomain
import com.example.playlistmaker.search.ui.view_model.SearchScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
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
    private lateinit var backButton: ImageButton
    private lateinit var refreshButton: Button

    private val handler = Handler(Looper.getMainLooper())
    private val debounceDelay = 3000L
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRecyclerViews()
        setupClickListeners()
        setupSearchField()
        observeViewModel()
    }

    private fun initViews() {
        queryInput = findViewById(R.id.search_field)
        clearButton = findViewById(R.id.clear_icon)
        noResultsLayout = findViewById(R.id.no_results)
        networkErrorLayout = findViewById(R.id.network_error)
        progressBar = findViewById(R.id.progressBar)
        historyRecyclerView = findViewById(R.id.recycler_view_history)
        historyLayout = findViewById(R.id.history_list)
        historyTitle = findViewById(R.id.history)
        clearHistoryButton = findViewById(R.id.clear_history_btn)
        backButton = findViewById(R.id.search_btn_back)
        refreshButton = findViewById(R.id.refresh_btn)
    }

    private fun setupRecyclerViews() {
        findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = trackAdapter
        }

        historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = historyAdapter
        }
    }
    private fun setupClickListeners() {
        backButton.setOnClickListener { onBackPressed() }
        clearHistoryButton.setOnClickListener {
            hideSearchHistory()
            searchViewModel.clearSearchHistory()
            showSearchHistoryAsync()
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
    }

    private fun setupSearchField() {
        queryInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && queryInput.text.isNullOrEmpty()) {
                showSearchHistoryAsync()
            }
        }

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                handler.removeCallbacksAndMessages(null)
                performSearch(queryInput.text.toString().trim())
                true
            } else {
                false
            }
        }

        queryInput.addTextChangedListener(object : TextWatcher {
            private var lastText = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentText = s?.toString() ?: ""
                clearButton.visibility = if (currentText.isEmpty()) View.GONE else View.VISIBLE

                handler.removeCallbacksAndMessages(null)

                if (currentText.isEmpty()) {
                    showSearchHistoryAsync()
                    return
                }

                lastText = currentText
                handler.postDelayed({
                    if (lastText == currentText) {
                        performSearch(currentText.trim())
                    }
                }, debounceDelay)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
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
        if (queryInput.text.isNotEmpty()) return

        uiScope.launch {
            searchViewModel.loadSearchHistory()
            val state = searchViewModel.screenState.value
            if (state?.historyList?.isNotEmpty() == true) {
                historyLayout.visibility = View.VISIBLE
                historyTitle.visibility = View.VISIBLE
                clearHistoryButton.visibility = View.VISIBLE
            } else {
                historyLayout.visibility = View.GONE
                historyTitle.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
            }
        }
    }

    private fun hideSearchHistory() {
        historyLayout.visibility = View.GONE
        historyTitle.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }

    private fun observeViewModel() {
        searchViewModel.screenState.observe(this) { state ->
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
        startActivity(Intent(this, PlayerActivity::class.java).apply {
            putExtra("track", track)
        })
    }

    private fun hideKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.let { imm ->
            imm.hideSoftInputFromWindow(queryInput.windowToken, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        searchViewModel.screenState.removeObservers(this)
    }
}

