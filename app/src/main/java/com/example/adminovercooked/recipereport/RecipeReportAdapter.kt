package com.example.adminovercooked.recipereport

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.ImageButton
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.adminovercooked.R
import com.example.adminovercooked.data.model.Report
import dagger.hilt.android.AndroidEntryPoint

class RecipeReportAdapter(
    private val context: Context,
    private val originalList: List<Report>
) : BaseAdapter() {
    private var filteredList = ArrayList<Report>(originalList)

    override fun getCount() = filteredList.size

    override fun getItem(p0: Int): Any {
        return filteredList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item_recipe_report, null)
        val targetReport = filteredList[p0]
        if (targetReport.recipeImage.isNotEmpty())
            Glide.with(context)
                .load(targetReport.recipeImage)
                .into(view.findViewById(R.id.ivRecipe))
        view.findViewById<TextView>(R.id.tvRecipeTitle).text = targetReport.recipeTitle
        view.findViewById<TextView>(R.id.tvReportReason).text = targetReport.reason
        return view
    }

    fun filter(): Filter {
        return MyFilter()
    }

    inner class MyFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val filterString =
                p0?.toString() ?: ""
            val result = FilterResults()
            val newList = ArrayList<Report>()

            for (i in originalList.indices) {
                val recipeReport = originalList[i]
                if (recipeReport.recipeTitle.startsWith(filterString, true))
                    newList.add(recipeReport)
            }
            result.values = newList
            result.count = newList.size
            return result
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            if (p1!!.values as? ArrayList<Report> != null) {
                filteredList = p1.values as ArrayList<Report>
                notifyDataSetChanged()
            }
        }
    }
}