package com.example.playlistmaker

import SearchHistory
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import android.os.Handler
import android.os.Looper

class SearchActivity : AppCompatActivity() {

    private var userText: String = ""
    private var trackList: MutableList<Track> = mutableListOf()
    private lateinit var trackAdapter: TrackAdapter

    private lateinit var noResultsLayout: LinearLayout
    private lateinit var networkErrorLayout: LinearLayout

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyTitle: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var searchHistory: SearchHistory
    private lateinit var progressBar: ProgressBar
    private var isFirstSearch = true

    private val searchDebounceDelay = 2000L
    private lateinit var searchHandler: Handler
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchHistory = SearchHistory(getSharedPreferences("app_preferences", Context.MODE_PRIVATE))
        noResultsLayout = findViewById(R.id.no_results)
        networkErrorLayout = findViewById(R.id.network_error)

        sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        historyRecyclerView = findViewById(R.id.recycler_view_history)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        trackAdapter = TrackAdapter(trackList) { track ->
            addToSearchHistory(track)
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("track", track)
            }
            startActivity(intent)
        }

        historyLayout = findViewById(R.id.history_list)
        historyTitle = findViewById(R.id.history)
        clearHistoryButton = findViewById(R.id.clear_history_btn)

        val queryInput = findViewById<EditText>(R.id.search_field)
        val backButton = findViewById<ImageButton>(R.id.search_btn_back)
        backButton.setOnClickListener {
            finish()
        }

        val clearButton = findViewById<ImageView>(R.id.clear_icon)
        clearButton.setOnClickListener {
            queryInput.setText("")
            hideKeyboard()
            trackList.clear()
            trackAdapter.updateTracks(trackList)
            isFirstSearch = true
            updateUI()
            showSearchHistory(true)
        }

        queryInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && queryInput.text.isNullOrEmpty()) {
                showSearchHistory(true)
            } else {
                showSearchHistory(false)
            }
        }

        progressBar = findViewById(R.id.progressBar)

        searchHandler = Handler(Looper.getMainLooper())

        queryInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                searchRunnable?.let { searchHandler.removeCallbacks(it) }

                if (!s.isNullOrEmpty()) {
                    showSearchHistory(false)

                    searchRunnable = Runnable {
                        val query = s.toString().trim()
                        if (query.isNotEmpty()) {
                            searchTracks(query)
                        }
                    }
                    searchHandler.postDelayed(searchRunnable!!, searchDebounceDelay)
                } else {
                    trackList.clear()
                    trackAdapter.updateTracks(trackList)
                    isFirstSearch = true
                    updateUI()
                    showSearchHistory(true)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }

                val query = queryInput.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchTracks(query)
                    hideKeyboard()
                }
                true
            } else {
                false
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        val refreshButton = findViewById<Button>(R.id.refresh_btn)
        refreshButton.setOnClickListener {
            if (userText.isNotEmpty()) {
                searchTracks(userText)
            }
        }

        clearHistoryButton.setOnClickListener {
            clearSearchHistory()
        }
    }

    private fun searchTracks(query: String) {
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
        isFirstSearch = false
        userText = query
        resetSearchState()
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.Main) {
                    trackList.clear()
                    trackAdapter.updateTracks(trackList)
                }
                val response: Response<SearchResponse> = RetrofitClient.api.searchTracks(query)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()!!
                        if (result.resultCount > 0) {
                            trackList = result.results.toMutableList()
                            trackAdapter.updateTracks(trackList)
                            showNoResults(false)
                            showNetworkError(false)
                        } else {
                            trackList.clear()
                            trackAdapter.updateTracks(trackList)
                            showNoResults(true)
                            showNetworkError(false)
                        }
                    } else {
                        trackList.clear()
                        trackAdapter.updateTracks(trackList)
                        showNoResults(true)
                        showNetworkError(false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    showNoResults(false)
                    showNetworkError(true)
                }
            }
        }
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

    private fun showSearchHistory(show: Boolean) {
        val history = getSearchHistory()
        historyLayout.visibility = if (show && history.isNotEmpty()) View.VISIBLE else View.GONE
        historyTitle.visibility = if (show && history.isNotEmpty()) View.VISIBLE else View.GONE
        clearHistoryButton.visibility = if (show && history.isNotEmpty()) View.VISIBLE else View.GONE

        if (show && history.isNotEmpty()) {
            val historyAdapter = TrackAdapter(history.toMutableList()) { track ->
                addToSearchHistory(track)
                val intent = Intent(this, PlayerActivity::class.java).apply {
                    putExtra("track", track)
                }
                startActivity(intent)
            }
            historyRecyclerView.adapter = historyAdapter
        }
    }

    private fun showNoResults(show: Boolean) {
        noResultsLayout.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showNetworkError(show: Boolean) {
        networkErrorLayout.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = currentFocus
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
    }

    private fun getSearchHistory(): List<Track> {
        return searchHistory.getSearchHistory()
    }

    private fun addToSearchHistory(track: Track) {
        searchHistory.addToSearchHistory(track)
        if (historyLayout.visibility == View.VISIBLE) {
            showSearchHistory(true)
        }
    }

    private fun clearSearchHistory() {
        searchHistory.clearSearchHistory()
        showSearchHistory(false)
    }

    object BundleKeys {
        const val SEARCH_TEXT = "userText"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(BundleKeys.SEARCH_TEXT, userText)

        val cursorPosition = findViewById<EditText>(R.id.search_field).selectionStart
        outState.putInt("cursorPosition", cursorPosition)

        outState.putParcelableArrayList("trackList", ArrayList(trackList))

        val noResultsVisible = noResultsLayout.visibility == View.VISIBLE
        val networkErrorVisible = networkErrorLayout.visibility == View.VISIBLE
        outState.putBoolean("noResultsVisible", noResultsVisible)
        outState.putBoolean("networkErrorVisible", networkErrorVisible)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        userText = savedInstanceState.getString(BundleKeys.SEARCH_TEXT, "")
        val inputEditText = findViewById<EditText>(R.id.search_field)

        val cursorPosition = savedInstanceState.getInt("cursorPosition", 0)
        inputEditText.setSelection(cursorPosition)

        trackList = savedInstanceState.getParcelableArrayList<Track>("trackList")?.toMutableList() ?: mutableListOf()
        trackAdapter.updateTracks(trackList)

        val noResultsVisible = savedInstanceState.getBoolean("noResultsVisible", false)
        showNoResults(noResultsVisible)

        val networkErrorVisible = savedInstanceState.getBoolean("networkErrorVisible", false)
        showNetworkError(networkErrorVisible)

        val history = getSearchHistory()
        if (userText.isEmpty() && inputEditText.hasFocus() && history.isNotEmpty()) {
            showSearchHistory(true)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
    }
}