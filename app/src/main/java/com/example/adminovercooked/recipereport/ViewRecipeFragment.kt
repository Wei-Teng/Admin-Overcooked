package com.example.adminovercooked.recipereport

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.adminovercooked.R
import com.example.adminovercooked.data.model.Recipe
import com.example.adminovercooked.databinding.DialogDeleteBinding
import com.example.adminovercooked.databinding.FragmentViewRecipeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewRecipeFragment : Fragment(R.layout.fragment_view_recipe) {
    private lateinit var binding: FragmentViewRecipeBinding
    private val viewModel: RecipeViewModel by viewModels()

    companion object {
        fun newInstance(recipeId: String, reportId: String): ViewRecipeFragment {
            val args = Bundle()
            args.putString("recipeId", recipeId)
            args.putString("reportId", reportId)
            val viewRecipeFragment = ViewRecipeFragment()
            viewRecipeFragment.arguments = args
            return viewRecipeFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentViewRecipeBinding.bind(view)
        val recipeId = requireArguments().getString("recipeId").toString()
        val reportId = requireArguments().getString("reportId").toString()
        viewModel.getCurrentRecipe(recipeId)
        viewModel.recipe.observe(viewLifecycleOwner) { recipe ->
            setupRecipeInformation(recipe, reportId)
        }
        setupIbBackArrowListener()
    }

    private fun setupRecipeInformation(recipe: Recipe, reportId: String) {
        binding.title.text = recipe.title
        Glide.with(binding.ivRecipe.context)
            .load(recipe.image)
            .into(binding.ivRecipe)
        binding.tvRatingScore.text = recipe.rating.toString()
        binding.tvReviewNum.text = "(" + recipe.reviewNum.toString() + " review(s))"
        binding.tvServesNum.text = recipe.servesNum.toString() + " Serves"
        binding.tvCookTime.text = recipe.cookTimeInMin.toString() + " min cook time"
        binding.lvIngredient.adapter =
            IngredientFormat2Adapter(requireActivity(), recipe.ingredientList.toTypedArray())
        binding.tvInstructionStep.text = recipe.instruction
        setupBtDeleteListener(recipe, reportId)
        setupBtEditListener(recipe.id)
    }

    private fun setupBtDeleteListener(recipe: Recipe, reportId: String) {
        binding.btDelete.setOnClickListener {
            val customDialogBinding = DialogDeleteBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(activity)
                .setView(customDialogBinding.root)
            val alertDialog = builder.show()
            customDialogBinding.apply {
                dialogIbCancel.setOnClickListener {
                    alertDialog.dismiss()
                }
                dialogBtYes.setOnClickListener {
                    alertDialog.dismiss()
                    lifecycleScope.launch {
                        binding.btDelete.visibility = View.INVISIBLE
                        binding.pbLoading2.visibility = View.VISIBLE
                        viewModel.deleteRecipe(recipe, reportId).join()
                        binding.pbLoading2.visibility = View.INVISIBLE
                        binding.btDelete.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "Delete successfully!!!", Toast.LENGTH_SHORT)
                            .show()
                        requireActivity().onBackPressed()
                    }
                }
            }
        }
    }

    private fun setupBtEditListener(recipeId: String) {
        binding.btEdit.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
                .replace(R.id.fragmentContainerView, EditRecipeFragment.newInstance(recipeId))
                .addToBackStack(null).commit()
        }
    }

    private fun setupIbBackArrowListener() {
        binding.ibBackArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}