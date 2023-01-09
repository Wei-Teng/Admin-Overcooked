package com.example.adminovercooked.data.repositories.profile

import android.net.Uri
import android.util.Log
import com.example.adminovercooked.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val storageReference: StorageReference,
    private val firebaseStorage: FirebaseStorage,
    firestore: FirebaseFirestore
) : ProfileRepository {

    private val usersCollection = firestore.collection("users")
    private val recipesCollection = firestore.collection("recipes")


    override suspend fun getTargetUser(id: String): User {
        return try {
            val snapshotList = usersCollection.whereEqualTo("id", id)
                .get().await()
            val user = snapshotList.documents[0].toObject(User::class.java) ?: throw Exception()
            user
        } catch (e: Exception) {
            Log.e("ProfileRepositoryImpl", "getTargetUser: ${e.message}")
            User()
        }
    }

    override suspend fun uploadImage(uri: Uri, firebaseAuthId: String) {
        try {
            val childRef =
                storageReference.child("ProfileImages").child(System.currentTimeMillis().toString())
            val uploadTask = childRef.putFile(uri).await()
            val imageUrl = uploadTask.metadata?.reference?.downloadUrl?.await() ?: throw Exception()

            val snapshot = usersCollection.document(firebaseAuthId).get().await()
            val user = snapshot.toObject(User::class.java) ?: throw Exception()
            val newUser = user.copy(image = imageUrl.toString())

            usersCollection.document(firebaseAuthId).set(newUser).await()
            if (user.image.isNotEmpty()) {
                val imageRef = firebaseStorage.getReferenceFromUrl(user.image)
                imageRef.delete().await()
            }
        } catch (e: Exception) {
            Log.e("ProfileRepositoryImp", "uploadImageAndReturnUser: ${e.message}")
        }
    }

    override suspend fun uploadName(newName: String, firebaseAuthId: String) {
        try {
            usersCollection.document(firebaseAuthId)
                .update(mapOf("username" to newName)).await()
        } catch (e: Exception) {
            Log.e("ProfileRepositoryImp", "uploadImageAndReturnUser: ${e.message}")
        }
    }

    override suspend fun uploadBio(newBio: String, firebaseAuthId: String) {
        try {
            usersCollection.document(firebaseAuthId)
                .update(mapOf("bio" to newBio)).await()
        } catch (e: Exception) {
            Log.e("ProfileRepositoryImp", "uploadImageAndReturnUser: ${e.message}")
        }
    }

    override suspend fun getPopularCreatorList() = callbackFlow {
        val snapshotListener =
            usersCollection.whereGreaterThan("followerNum", -1)
                .addSnapshotListener { snapshot, e ->
                    val creatorsList = if (snapshot != null) {
                        val creators = snapshot.toObjects(User::class.java)
                        creators
                    } else {
                        throw Exception(e?.message)
                    }
                    trySend(creatorsList)
                }

        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun deleteTargetUser(user: User) {
        val snapshotList = recipesCollection.whereEqualTo("authorId", user.id).get().await()
        if (snapshotList.size() > 0) {
            for (snapshot in snapshotList)
                recipesCollection.document(snapshot.get("id").toString()).delete().await()
        }
        usersCollection.document(user.firebaseAuthId).delete().await()
    }
}