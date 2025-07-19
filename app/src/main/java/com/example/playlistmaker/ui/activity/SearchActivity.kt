package com.example.playlistmaker.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
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
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.interactor.SearchHistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.toParcelable
import com.example.playlistmaker.presentation.TrackParcelable
import com.example.playlistmaker.presentation.toDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SearchActivity : AppCompatActivity() {

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private val trackList = mutableListOf<TrackParcelable>()
    private var userText = ""
    private lateinit var noResultsLayout: LinearLayout
    private lateinit var networkErrorLayout: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyTitle: TextView
    private lateinit var clearHistoryButton: Button
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var isFirstSearch = true
    private lateinit var searchTracksInteractor: SearchTracksInteractor
    private lateinit var historyInteractor: SearchHistoryInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchTracksInteractor = Creator.searchTracksInteractor

        setContentView(R.layout.activity_search)
        initDependencies()

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupSearchHistory()
        setupSearchField()
        restoreState(savedInstanceState)
    }
    private fun initDependencies() {
        historyInteractor = Creator.provideSearchHistoryInteractor(applicationContext)
        trackAdapter = Creator.provideTrackAdapter { track ->
            addToSearchHistory(track)
            navigateToPlayer(track)
        }
        historyAdapter = Creator.provideTrackAdapter { track ->
            addToSearchHistory(track)
            navigateToPlayer(track)
        }
    }
    private fun initViews() {
        noResultsLayout = findViewById(R.id.no_results)
        networkErrorLayout = findViewById(R.id.network_error)
        progressBar = findViewById(R.id.progressBar)
        historyRecyclerView = findViewById(R.id.recycler_view_history)
        historyLayout = findViewById(R.id.history_list)
        historyTitle = findViewById(R.id.history)
        clearHistoryButton = findViewById(R.id.clear_history_btn)

        findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = trackAdapter
        }

        findViewById<ImageButton>(R.id.search_btn_back).setOnClickListener { finish() }
        findViewById<Button>(R.id.refresh_btn).setOnClickListener { retrySearch() }
        clearHistoryButton.setOnClickListener { clearSearchHistory() }
    }

    private fun setupSearchHistory() {
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
    }

    private fun setupSearchField() {
        val queryInput = findViewById<EditText>(R.id.search_field)
        val clearButton = findViewById<ImageView>(R.id.clear_icon)

        clearButton.setOnClickListener {
            queryInput.setText("")
            hideKeyboard()
            clearSearchResults()
            showSearchHistory(true)
        }

        queryInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && queryInput.text.isNullOrEmpty()) {
                showSearchHistory(true)
            }
        }

        queryInput.addTextChangedListener(createTextWatcher())
        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(queryInput.text.toString().trim())
                true
            } else {
                false
            }
        }
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                findViewById<ImageView>(R.id.clear_icon).visibility =
                    if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                searchRunnable?.let { searchHandler.removeCallbacks(it) }

                if (!s.isNullOrEmpty()) {
                    showSearchHistory(false)
                    searchRunnable = Runnable { performSearch(s.toString().trim()) }
                    searchHandler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
                } else {
                    clearSearchResults()
                    showSearchHistory(true)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun performSearch(query: String) {
        if (query.isNotEmpty()) {
            searchTracks(query)
            hideKeyboard()
        }
    }

    private fun retrySearch() {
        if (userText.isNotEmpty()) {
            searchTracks(userText)
        }
    }

    private fun searchTracks(query: String) {
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
        isFirstSearch = false
        userText = query
        resetSearchState()
        trackList.clear()
        trackAdapter.updateTracks(trackList)
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = Creator.searchTracksInteractor.searchTracks(query)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    handleSearchResult(result)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    handleSearchError()
                }
            }
        }
    }

    private fun handleSearchResult(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        trackList.clear()
        trackList.addAll(tracks.map { it.toParcelable() })
        trackAdapter.updateTracks(trackList)

        when {
            tracks.isNotEmpty() -> showSearchResults()
            else -> showNoResults()
        }
    }
    private fun handleSearchError() {
        progressBar.visibility = View.GONE
        trackList.clear()
        trackAdapter.updateTracks(trackList)
        showNetworkError(true)
        showNoResults(false)
    }

    private fun showSearchResults() {
        showNoResults(false)
        showNetworkError(false)
    }

    private fun showNoResults(show: Boolean = true) {
        noResultsLayout.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showNetworkError(show: Boolean = true) {
        networkErrorLayout.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun clearSearchResults() {
        trackList.clear()
        trackAdapter.updateTracks(trackList)
        isFirstSearch = true
        updateUI()
    }

    private fun resetSearchState() {
        progressBar.visibility = View.GONE
        showNoResults(false)
        showNetworkError(false)
    }

    private fun updateUI() {
        showNoResults(trackList.isEmpty() && !isFirstSearch)
        showNetworkError(false)
    }

    private fun hideKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.let { imm ->
            currentFocus?.windowToken?.let { token ->
                imm.hideSoftInputFromWindow(token, 0)
            }
        }
    }

    private fun navigateToPlayer(track: TrackParcelable) {
        startActivity(Intent(this, PlayerActivity::class.java).apply {
            putExtra("track", track as Parcelable)
        })
    }

    private fun addToSearchHistory(track: TrackParcelable) {
        lifecycleScope.launch {
            try {
                val domainTrack = track.toDomain()
                historyInteractor.addTrack(domainTrack)

                withContext(Dispatchers.Main) {
                    if (historyLayout.isVisible) {
                        loadSearchHistory()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun loadSearchHistory() {
        val history = historyInteractor.getHistory()
        val parcelableHistory = history.map { it.toParcelable() }
        historyAdapter.updateTracks(parcelableHistory.toMutableList())
    }

    private fun showSearchHistory(show: Boolean) {
        lifecycleScope.launch {
            try {
                val history = historyInteractor.getHistory()
                val shouldShowHistory = show && history.isNotEmpty()

                withContext(Dispatchers.Main) {
                    historyLayout.visibility = if (shouldShowHistory) View.VISIBLE else View.GONE
                    historyTitle.visibility = if (shouldShowHistory) View.VISIBLE else View.GONE
                    clearHistoryButton.visibility = if (shouldShowHistory) View.VISIBLE else View.GONE

                    if (shouldShowHistory) {
                        val parcelableHistory = history.map { it.toParcelable() }
                        historyAdapter.updateTracks(parcelableHistory.toMutableList())
                    }
                }
            } catch (e: Exception) {
            }
        }
    }
    private fun clearSearchHistory() {
        lifecycleScope.launch {
            try {
                historyInteractor.clear()
                withContext(Dispatchers.Main) {
                    historyAdapter.updateTracks(mutableListOf())
                    historyLayout.visibility = View.GONE
                    historyTitle.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                }
            } catch (e: Exception) {
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putString("userText", userText)
            putInt("cursorPosition", findViewById<EditText>(R.id.search_field).selectionStart)
            putParcelableArrayList("trackList", ArrayList(trackList))
            putBoolean("noResultsVisible", noResultsLayout.visibility == View.VISIBLE)
            putBoolean("networkErrorVisible", networkErrorLayout.visibility == View.VISIBLE)
        }
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            userText = it.getString("userText", "")
            trackList.clear()
            it.getParcelableArrayList<TrackParcelable>("trackList")?.let { list ->
                trackList.addAll(list)
            }
            trackAdapter.updateTracks(trackList)

            findViewById<EditText>(R.id.search_field).apply {
                setText(userText)
                setSelection(it.getInt("cursorPosition", 0))
            }

            showNoResults(it.getBoolean("noResultsVisible", false))
            showNetworkError(it.getBoolean("networkErrorVisible", false))

            if (userText.isEmpty() && findViewById<EditText>(R.id.search_field).hasFocus()) {
                lifecycleScope.launch {
                    val history = historyInteractor.getHistory()
                    if (history.isNotEmpty()) {
                        showSearchHistory(true)
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        val queryInput = findViewById<EditText>(R.id.search_field)
        if (queryInput.text.isNullOrEmpty() && queryInput.hasFocus()) {
            showSearchHistory(true)
        } else {
            showSearchHistory(false)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        searchRunnable?.let { searchHandler.removeCallbacks(it) }

    }
    private companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
