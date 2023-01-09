package com.example.adminovercooked.recipereport

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.adminovercooked.R
import com.example.adminovercooked.data.model.Report
import com.example.adminovercooked.databinding.FragmentReportBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReportFragment: Fragment(R.layout.fragment_report) {
    private lateinit var binding: FragmentReportBinding
    private val viewModel: RecipeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentReportBinding.bind(view)
        viewModel.getReportList()
        viewModel.recipeReportList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                binding.tvNoAnyReport.visibility = View.INVISIBLE
            val recipeReportAdapter = RecipeReportAdapter(requireContext(), it)
            binding.lvRecipeReport.adapter = recipeReportAdapter
            setupSvReportQueryTextListener(recipeReportAdapter)
            setupListViewListener(recipeReportAdapter)
        }
    }

    private fun setupSvReportQueryTextListener(recipeReportAdapter: RecipeReportAdapter) {
        binding.svReport.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                binding.svReport.clearFocus()
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                recipeReportAdapter.filter().filter(p0)
                return false
            }
        })
    }

    private fun setupListViewListener(recipeReportAdapter: RecipeReportAdapter) {
        binding.lvRecipeReport.setOnItemClickListener { _, _, i, _ ->
            lifecycleScope.launch {
                val targetReport = recipeReportAdapter.getItem(i) as Report
                viewModel.getCurrentRecipe(targetReport.recipeId).join()
                val recipe = viewModel.recipe.value!!
                requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )
                    .replace(
                        R.id.fragmentContainerView,
                        ViewRecipeFragment.newInstance(recipe.id, targetReport.id)
                    )
                    .addToBackStack(null).commit()
            }
        }
        binding.lvRecipeReport.onItemLongClickListener =
            OnItemLongClickListener { _, _, position, _ ->
                lifecycleScope.launch {
                    val targetReport = recipeReportAdapter.getItem(position) as Report
                    viewModel.deleteReport(targetReport.id).join()
                }
                true
            }
    }
}