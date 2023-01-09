package com.example.adminovercooked.user

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminovercooked.data.model.User
import com.example.adminovercooked.data.repositories.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user
    private val _creatorsLiveData = MutableLiveData<List<User>>()
    val creators: LiveData<List<User>> = _creatorsLiveData

    fun getTargetUser(userId: String) = viewModelScope.launch {
        _user.value = repository.getTargetUser(userId)
    }

    fun uploadUserProfileImage(uri: Uri, firebaseAuthId: String) = viewModelScope.launch {
        repository.uploadImage(uri, firebaseAuthId)
    }

    fun uploadUsername(newName: String, firebaseAuthId: String) = viewModelScope.launch {
        repository.uploadName(newName, firebaseAuthId)
    }

    fun uploadBio(newBio: String, firebaseAuthId: String) = viewModelScope.launch {
        repository.uploadBio(newBio, firebaseAuthId)
    }

    fun getPopularCreatorList() = viewModelScope.launch {
        repository.getPopularCreatorList().collect {
            _creatorsLiveData.value = it
        }
    }

    fun deleteTargetUser(user: User) = viewModelScope.launch {
        repository.deleteTargetUser(user)
    }
}