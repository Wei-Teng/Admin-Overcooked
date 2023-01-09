package com.example.adminovercooked.data.repositories.recipe

import android.util.Log
import androidx.core.net.toUri
import com.example.adminovercooked.data.model.Recipe
import com.example.adminovercooked.data.model.Report
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storageReference: StorageReference,
) : RecipeRepository {

    private val recipesCollection = firestore.collection("recipes")

    override suspend fun getCurrentRecipe(recipeId: String): Recipe {
        return try {
            val snapshotList = recipesCollection.whereEqualTo("id", recipeId).get().await()
            val recipe = snapshotList.documents[0].toObject(Recipe::class.java) ?: throw Exception()
            recipe
        } catch (e: Exception) {
            Log.e("RecipeRepositoryImpl", "getCurrentRecipe: ${e.message}")
            Recipe()
        }
    }

    override suspend fun editRecipe(recipe: Recipe, recipeDetails: HashMap<String, Any>): Boolean {
        return try {
            val childRef =
                storageReference.child("RecipeImages").child(System.currentTimeMillis().toString())
            val uploadTask = childRef.putFile(recipeDetails["image"].toString().toUri()).await()
            val imageUrl = uploadTask.metadata?.reference?.downloadUrl?.await() ?: throw Exception()
            recipeDetails["image"] = imageUrl.toString()
            if (recipe.image.isNotEmpty()) {
                val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(recipe.image)
                imageRef.delete().await()
            }
            recipesCollection.document(recipe.id).update(
                recipeDetails as Map<String, Any>
            ).await()

            true
        } catch (e: Exception) {
            Log.e("RecipeRepositoryImp", "editRecipe ${e.message}")
            false
        }
    }

    override suspend fun deleteRecipe(recipe: Recipe, reportId: String): Boolean {
        return try {
            recipesCollection.document(recipe.id).delete().await()
            deleteReport(reportId)
            true
        } catch (e: Exception) {
            Log.e("RecipeRepositoryImp", "deleteRecipe ${e.message}")
            false
        }
    }

    override suspend fun getRecipeReport() = callbackFlow {
        val snapshotListener =
            firestore.collection("reports")
                .addSnapshotListener { snapshot, e ->
                    val reportList = if (snapshot != null) {
                        val reports = snapshot.toObjects(Report::class.java)
                        reports
                    } else {
                        throw Exception(e?.message)
                    }
                    trySend(reportList)
                }

        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun deleteReport(reportId: String) {
        firestore.collection("reports").document(reportId).delete().await()
    }
}