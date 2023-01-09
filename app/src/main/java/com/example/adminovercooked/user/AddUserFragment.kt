package com.example.adminovercooked.user

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.adminovercooked.R
import com.example.adminovercooked.data.model.User
import com.example.adminovercooked.databinding.FragmentAddUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddUserFragment: Fragment(R.layout.fragment_add_user) {
    private lateinit var binding: FragmentAddUserBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestoreDb: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddUserBinding.bind(view)
        firebaseAuth = FirebaseAuth.getInstance()
        firestoreDb = Firebase.firestore
        setupIbBackArrowListener()
        setupBtSaveListener()
    }

    private fun setupIbBackArrowListener() {
        binding.ibBackArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupBtSaveListener() {
        binding.btSave.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val region = binding.etRegion.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (isValidUsername(username) && isValidEmail(email)
                && isValidRegion(region) && isValidPassword(password)
            ) {
                binding.pbLoading.visibility = View.VISIBLE
                binding.btSave.visibility = View.INVISIBLE
                val user = User(email, password, region, username)
                createAndStoreUser(user)
                clearCredentials()
            }
        }
    }

    private fun isValidUsername(name: String): Boolean {
        return if (name.length >= 2)
            true
        else {
            binding.etUsername.error = "At Least 2 Character Long"
            binding.etUsername.requestFocus()
            false
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return if (Patterns.EMAIL_ADDRESS.matcher(email).find())
            true
        else {
            binding.etEmail.error = "Email Format Is Incorrect!"
            binding.etEmail.requestFocus()
            false
        }
    }

    private fun isValidRegion(region: String): Boolean {
        val countryCodes = Locale.getISOCountries()
        val targetName =
            if (region.length >= 2)
                region.substring(0, 1).uppercase() + region.substring(1).lowercase()
            else
                ""
        for (countryCode in countryCodes) {
            val locale = Locale("en", countryCode)
            if (locale.displayCountry == targetName)
                return true
        }
        binding.etRegion.error = "Invalid Region"
        binding.etRegion.requestFocus()
        return false
    }

    private fun isValidPassword(password: String): Boolean {
        return if (password.length >= 6)
            true
        else {
            binding.etPassword.error = "At Least 6 Characters Long"
            binding.etPassword.requestFocus()
            false
        }
    }

    private fun createAndStoreUser(user: User) {
        firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnSuccessListener {
                user.firebaseAuthId = FirebaseAuth.getInstance().currentUser!!.uid
                firestoreDb.collection("users")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .set(user)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "User created successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().onBackPressed()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
                    }
                binding.pbLoading.visibility = View.INVISIBLE
                binding.btSave.visibility = View.VISIBLE
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                binding.pbLoading.visibility = View.INVISIBLE
                binding.btSave.visibility = View.VISIBLE
            }
    }

    private fun clearCredentials() {
        binding.etUsername.text.clear()
        binding.etEmail.text.clear()
        binding.etRegion.text.clear()
        binding.etPassword.text.clear()
    }
}