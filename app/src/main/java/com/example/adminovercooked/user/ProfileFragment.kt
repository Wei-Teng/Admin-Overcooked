package com.example.adminovercooked.user

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.adminovercooked.R
import com.example.adminovercooked.data.model.User
import com.example.adminovercooked.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        setupBtAddUserListener()
        viewModel.getPopularCreatorList()
        viewModel.creators.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                binding.tvNoAnyUser.visibility = View.INVISIBLE
            val userAdapter = UserAdapter(requireContext(), it)
            binding.lvUser.adapter = userAdapter
            setupSvCreatorQueryTextListener(userAdapter)
            setupLvListener(userAdapter)
        }
    }

    private fun setupSvCreatorQueryTextListener(userAdapter: UserAdapter) {
        binding.svUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                binding.svUser.clearFocus()
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                userAdapter.filter().filter(p0)
                return false
            }
        })
    }

    private fun setupLvListener(userAdapter: UserAdapter) {
        binding.lvUser.setOnItemClickListener { _, _, i, _ ->
            val targetUser = userAdapter.getItem(i) as User
            requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
                .replace(
                    R.id.fragmentContainerView,
                    SomeoneProfileFragment.newInstance(targetUser.id)
                )
                .addToBackStack(null).commit()
        }
    }

    private fun setupBtAddUserListener() {
        binding.btAdd.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
                .replace(
                    R.id.fragmentContainerView,
                    AddUserFragment()
                )
                .addToBackStack(null).commit()
        }
    }
}