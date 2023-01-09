package com.example.adminovercooked.recipereport

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminovercooked.data.model.Recipe
import com.example.adminovercooked.data.model.Report
import com.example.adminovercooked.data.repositories.recipe.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository,
) : ViewModel() {

    private val _recipe = MutableLiveData<Recipe>()
    val recipe: LiveData<Recipe> = _recipe
    private val _recipeReportList = MutableLiveData<List<Report>>()
    val recipeReportList = _recipeReportList

    fun getCurrentRecipe(recipeId: String) = viewModelScope.launch {
        val recipeData = repository.getCurrentRecipe(recipeId)
        _recipe.value = recipeData
    }

    fun deleteRecipe(recipe: Recipe, reportId: String) = viewModelScope.launch {
        repository.deleteRecipe(recipe, reportId)
    }

    fun editRecipe(recipe: Recipe, recipeDetails: HashMap<String, Any>) = viewModelScope.launch {
        repository.editRecipe(recipe, recipeDetails)
    }

    fun getReportList() = viewModelScope.launch {
        repository.getRecipeReport().collect {
            _recipeReportList.value = it
        }
    }

    fun deleteReport(reportId: String) = viewModelScope.launch {
        repository.deleteReport(reportId)
    }
}