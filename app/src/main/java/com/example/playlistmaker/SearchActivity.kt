package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
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
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private var userText: String = ""
    private var trackList: MutableList<Track> = mutableListOf()
    private lateinit var trackAdapter: TrackAdapter

    private lateinit var noResultsLayout: LinearLayout
    private lateinit var networkErrorLayout: LinearLayout

    private var isFirstSearch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }
        noResultsLayout = findViewById(R.id.no_results)
        networkErrorLayout = findViewById(R.id.network_error)

        if (savedInstanceState != null) {
            userText = savedInstanceState.getString(BundleKeys.SEARCH_TEXT, "")
            trackList = savedInstanceState.getParcelableArrayList<Track>("trackList")?.toMutableList() ?: mutableListOf()
            val noResultsVisible = savedInstanceState.getBoolean("noResultsVisible", false)
            val networkErrorVisible = savedInstanceState.getBoolean("networkErrorVisible", false)
            showNoResults(noResultsVisible)
            showNetworkError(networkErrorVisible)
        }

        trackAdapter = TrackAdapter(trackList)

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
            showNoResults(false)
            showNetworkError(false)
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                userText = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        queryInput.addTextChangedListener(searchTextWatcher)

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = queryInput.text.toString()
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

        if (!isFirstSearch && trackList.isEmpty()) {
            showNoResults(true)
        }

        val refreshButton = findViewById<Button>(R.id.refresh_btn)
        refreshButton.setOnClickListener {
            if (userText.isNotEmpty()) {
                searchTracks(userText)
            }
        }
    }

    private fun searchTracks(query: String) {
        isFirstSearch = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.Main) {
                    trackList.clear()
                    trackAdapter.updateTracks(trackList)}
                val response: Response<SearchResponse> = RetrofitClient.api.searchTracks(query)

                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) {
                        val result = response.body()!!
                        if (result.resultCount > 0) {
                            trackList = result.results.toMutableList()
                            trackAdapter.updateTracks(trackList)

                            showNoResults(false)
                            showNetworkError(false)
                        } else {
                            showNoResults(true)
                            showNetworkError(false)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showNoResults(true)
                        showNetworkError(false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showNoResults(false)
                    showNetworkError(true)
                }
            }
        }
    }

    private fun showNoResults(show: Boolean) {
        noResultsLayout.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showNetworkError(show: Boolean) {
        networkErrorLayout.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun formatTrackTime(timeInMillis: Long): String {
        val minutes = timeInMillis / 60000
        val seconds = (timeInMillis % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = currentFocus
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
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
        inputEditText.setText(userText)

        val cursorPosition = savedInstanceState.getInt("cursorPosition", 0)
        inputEditText.setSelection(cursorPosition)

        trackList = savedInstanceState.getParcelableArrayList<Track>("trackList")?.toMutableList() ?: mutableListOf()
        trackAdapter.updateTracks(trackList)

        val noResultsVisible = savedInstanceState.getBoolean("noResultsVisible", false)
        showNoResults(noResultsVisible)

        val networkErrorVisible = savedInstanceState.getBoolean("networkErrorVisible", false)
        showNetworkError(networkErrorVisible)
    }
}
