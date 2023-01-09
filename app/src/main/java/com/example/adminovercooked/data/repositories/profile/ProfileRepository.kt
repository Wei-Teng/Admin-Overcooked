package com.example.adminovercooked.data.repositories.profile

import android.net.Uri
import com.example.adminovercooked.data.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getTargetUser(id: String): User
    suspend fun uploadImage(uri: Uri, firebaseAuthId: String)
    suspend fun uploadName(newName: String, firebaseAuthId: String)
    suspend fun uploadBio(newBio: String, firebaseAuthId: String)
    suspend fun getPopularCreatorList() : Flow<List<User>>
    suspend fun deleteTargetUser(user: User)
}