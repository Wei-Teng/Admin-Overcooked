package com.example.adminovercooked.user

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.adminovercooked.R
import com.example.adminovercooked.data.model.User
import com.example.adminovercooked.databinding.FragmentEditProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    private lateinit var binding: FragmentEditProfileBinding
    private var imageUri: Uri? = null
    private val viewModel: UserViewModel by viewModels()

    companion object {
        fun newInstance(userId: String): EditProfileFragment {
            val args = Bundle()
            args.putString("userId", userId)
            val editProfileFragment = EditProfileFragment()
            editProfileFragment.arguments = args
            return editProfileFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditProfileBinding.bind(view)
        val userId = requireArguments().getString("userId").toString()
        setupIbBackArrowListener()
        viewModel.getTargetUser(userId)
        viewModel.user.observe(viewLifecycleOwner) { user ->
            setupBtSaveListener(user)
            binding.etProfileName.hint = user.username
            binding.etBio.hint = user.bio
        }
        binding.ivProfile.setOnClickListener {
            ImagePicker.with(requireActivity())
                .crop()
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
    }

    private fun setupIbBackArrowListener() {
        binding.ibBackArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    imageUri = fileUri
                    Glide.with(binding.ivProfile.context)
                        .load(imageUri)
                        .into(binding.ivProfile)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun setupBtSaveListener(user: User) {
        binding.btSave.setOnClickListener {
            val newName = binding.etProfileName.text.toString().trim()
            val newBio = binding.etBio.text.toString().trim()
            if (newName.isNotEmpty() || newBio.isNotEmpty() || imageUri != null) {
                lifecycleScope.launch {
                    binding.btSave.visibility = View.INVISIBLE
                    binding.pbLoading.visibility = View.VISIBLE
                    if (newName.isNotEmpty())
                        viewModel.uploadUsername(newName, user.firebaseAuthId).join()
                    if (newBio.isNotEmpty())
                        viewModel.uploadBio(newBio, user.firebaseAuthId).join()
                    if (imageUri != null)
                        viewModel.uploadUserProfileImage(imageUri!!, user.firebaseAuthId).join()
                    viewModel.getTargetUser(user.id).join()
                    Toast.makeText(
                        requireContext(),
                        "Info updated successfully",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    requireActivity().onBackPressed()
                    binding.btSave.visibility = View.VISIBLE
                    binding.pbLoading.visibility = View.INVISIBLE
                }
            } else
                Toast.makeText(
                    requireContext(),
                    "Please fill up at least one field",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }
}