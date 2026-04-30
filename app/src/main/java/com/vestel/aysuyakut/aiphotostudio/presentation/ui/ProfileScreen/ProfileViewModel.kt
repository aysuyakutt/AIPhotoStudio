package com.vestel.aysuyakut.aiphotostudio.presentation.ui.ProfileScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vestel.aysuyakut.aiphotostudio.R
import com.vestel.aysuyakut.aiphotostudio.data.mapper.toUi
import com.vestel.aysuyakut.aiphotostudio.data.repository.AuthRepository
import com.vestel.aysuyakut.aiphotostudio.data.repository.ImageRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: ImageRepo,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _events = Channel<Event>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadUserName()
        observePhotos()
    }

    private fun observePhotos() = viewModelScope.launch {
        repo.getAll().collect { images ->
            val photos = images.map { it.toUi() }
            _uiState.update { state ->
                state.copy(
                    profileImageRes = R.drawable.profile,
                    userInfo = "AIPhotoStudio • ${photos.size} posts",
                    photos = photos
                )
            }
        }
    }

    private fun loadUserName() = viewModelScope.launch {
        val user = FirebaseAuth.getInstance().currentUser
        val resolved = when {
            user == null || user.isAnonymous -> "Guest"
            !user.displayName.isNullOrBlank() -> user.displayName!!
            else -> fetchNameFromFirestore(user.uid)
                ?: user.email?.substringBefore('@')?.replaceFirstChar { it.titlecase() }
                ?: "User"
        }
        _uiState.update { it.copy(userName = resolved) }
    }

    private suspend fun fetchNameFromFirestore(uid: String): String? {
        return try {
            val snap = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .await()
            snap.getString("displayName")
        } catch (_: Exception) {
            null
        }
    }

    fun onOpenDrawer() = _uiState.update { it.copy(isDrawerOpen = true) }
    fun onCloseDrawer() = _uiState.update { it.copy(isDrawerOpen = false) }
    fun onPhotoClicked(photo: OldPhoto) = _uiState.update { it.copy(selectedPhoto = photo) }
    fun onClosePhoto() = _uiState.update { it.copy(selectedPhoto = null) }

    fun onDeletePhoto(photo: OldPhoto, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val ok = try { repo.delete(photo.id, photo.uri) } catch (_: Throwable) { false }
            _uiState.update { it.copy(selectedPhoto = null) }
            onResult(ok)
        }
    }

    fun onEditProfile() { /* TODO */ }
    fun onSettings() { /* TODO */ }

    fun onLogout() {
        viewModelScope.launch {
            runCatching { authRepo.logout() }
            _events.send(Event.LoggedOut)
        }
    }

    sealed interface Event { data object LoggedOut : Event }
}
