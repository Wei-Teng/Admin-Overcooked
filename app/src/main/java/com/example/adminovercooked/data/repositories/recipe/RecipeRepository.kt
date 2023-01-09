package com.example.adminovercooked.data.repositories.recipe

import com.example.adminovercooked.data.model.Recipe
import com.example.adminovercooked.data.model.Report
import kotlinx.coroutines.flow.Flow


interface RecipeRepository {
    suspend fun getCurrentRecipe(recipeId: String): Recipe
    suspend fun editRecipe(recipe: Recipe, recipeDetails: HashMap<String, Any>): Boolean
    suspend fun deleteRecipe(recipe: Recipe, reportId: String): Boolean
    suspend fun getRecipeReport(): Flow<List<Report>>
    suspend fun deleteReport(reportId: String)
}