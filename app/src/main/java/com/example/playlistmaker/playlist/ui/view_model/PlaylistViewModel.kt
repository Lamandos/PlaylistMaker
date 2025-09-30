import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.data.PlaylistEntity
import com.example.playlistmaker.playlist.domain.PlaylistInteractor
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class PlaylistViewModel(
    application: Application,
    private val playlistInteractor: PlaylistInteractor
) : AndroidViewModel(application) {

    private val _isPlaylistCreated = MutableLiveData<Boolean>()
    val isPlaylistCreated: MutableLiveData<Boolean> = _isPlaylistCreated

    private val _createdPlaylistName = MutableLiveData<String>()
    val createdPlaylistName: MutableLiveData<String> = _createdPlaylistName

    fun saveCoverImage(uri: Uri): String {
        val context = getApplication<Application>()
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputFile = File(context.filesDir, "cover_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(outputFile)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return outputFile.absolutePath
    }

    fun createPlaylist(name: String, description: String, coverImagePath: String) {
        val playlist = PlaylistEntity(
            name = name,
            description = description,
            coverImagePath = coverImagePath,
            trackCount = 0
        )

        viewModelScope.launch {
            try {
                playlistInteractor.createPlaylist(playlist)
                _createdPlaylistName.postValue(name)
                _isPlaylistCreated.postValue(true)
            } catch (e: Exception) {
                _isPlaylistCreated.postValue(false)
            }
        }
    }
}