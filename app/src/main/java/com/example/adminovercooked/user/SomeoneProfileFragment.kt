package com.example.adminovercooked.user

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.adminovercooked.R
import com.example.adminovercooked.data.model.User
import com.example.adminovercooked.databinding.DialogDeleteBinding
import com.example.adminovercooked.databinding.FragmentSomeoneProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SomeoneProfileFragment : Fragment(R.layout.fragment_someone_profile) {
    private lateinit var binding: FragmentSomeoneProfileBinding
    private val viewModel: UserViewModel by viewModels()

    companion object {
        fun newInstance(userId: String): SomeoneProfileFragment {
            val args = Bundle()
            args.putString("userId", userId)
            val someoneProfileFragment = SomeoneProfileFragment()
            someoneProfileFragment.arguments = args
            return someoneProfileFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSomeoneProfileBinding.bind(view)
        val userId = requireArguments().getString("userId").toString()
        viewModel.getTargetUser(userId)
        setupIbBackArrowListener()
        viewModel.user.observe(viewLifecycleOwner) { user ->
            setupUserProfileInformation(user)
            setupBtDeleteListener(user)
            setupBtEditListener(user)
        }
    }

    private fun setupUserProfileInformation(user: User) {
        if (user.image.isNotEmpty())
            Glide.with(binding.ivProfile.context)
                .load(user.image)
                .into(binding.ivProfile)
        binding.tvProfileName.text = user.username
        binding.tvBio.text = user.bio
    }

    private fun setupIbBackArrowListener() {
        binding.ibBackArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupBtDeleteListener(user: User) {
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
                        binding.pbLoading.visibility = View.VISIBLE
                        viewModel.deleteTargetUser(user).join()
                        Toast.makeText(requireContext(), "Delete successfully", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed()
                        viewModel.getPopularCreatorList().join()
                        binding.pbLoading.visibility = View.INVISIBLE
                        binding.btDelete.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupBtEditListener(user: User) {
        binding.btEdit.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
                .replace(R.id.fragmentContainerView, EditProfileFragment.newInstance(user.id))
                .addToBackStack(null).commit()
        }
    }
}