package com.example.sportsbooking.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.sportsbooking.R
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_auth, container, false)

        // Initialize the views
        profileImageView = view.findViewById(R.id.profileImageView)
        nameTextView = view.findViewById(R.id.nameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        // Load user data from Firebase Authentication
        loadUserData()

        return view
    }

    private fun loadUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            // Display Name
            val name = user.displayName
            nameTextView.text = name ?: "N/A"

            // Email
            val email = user.email
            emailTextView.text = email ?: "N/A"

            // Profile Picture
            val photoUrl = user.photoUrl
            if (photoUrl != null) {
                // Load profile picture using Glide
                Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.default_profile) // Replace with a local default image
                    .into(profileImageView)
            } else {
                // Set default profile image if no URL is available
                profileImageView.setImageResource(R.drawable.default_profile)
            }
        }
    }

}
