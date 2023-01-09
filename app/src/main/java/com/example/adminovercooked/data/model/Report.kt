package com.example.adminovercooked.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Report(
    var id: String = "",
    var authorId: String = "",
    var reason: String = "",
    var recipeId: String = "",
    var recipeImage: String = "",
    var recipeTitle: String = ""
) : Parcelable