package com.example.sportsbooking.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.sportsbooking.R
import com.example.sportsbooking.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class SettingsActivity : AppCompatActivity() {

    private lateinit var usernameField: EditText
    private lateinit var profileImage: ImageView
    private lateinit var saveButton: TextView
    private lateinit var cancelButton: TextView

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // Initialize views
        usernameField = findViewById(R.id.usernameField)
        profileImage = findViewById(R.id.profileImageView)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)

        // Load current user data
        loadUserData()

        // Set up click listeners
        profileImage.setOnClickListener {
            openImagePicker()
        }

        saveButton.setOnClickListener {
            saveChanges()
        }

        cancelButton.setOnClickListener {
            finish() // Close the activity
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            logout()
        }
    }

    private fun loadUserData() {
        currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("username")
                        val profileImageUrl = document.getString("profileImageUrl")

                        // Set username
                        usernameField.setText(username)

                        // Load profile image using Glide
                        if (!profileImageUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(profileImageUrl)
                                .circleCrop() // Ensure the image is cropped to a circle
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .into(profileImage)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            Glide.with(this)
                .load(selectedImageUri)
                .circleCrop() // Ensure the image is cropped to a circle
                .into(profileImage)
        }
    }

    private fun saveChanges() {
        val newUsername = usernameField.text.toString().trim()

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Update username in Firestore
        currentUser?.uid?.let { userId ->
            val userUpdates = hashMapOf<String, Any>(
                "username" to newUsername
            )

            // If a new image is selected, upload it to Firebase Storage
            if (selectedImageUri != null) {
                uploadImageToFirebaseStorage(userId, userUpdates)
            } else {
                // If no new image is selected, just update the username
                updateFirestore(userId, userUpdates)
            }
        }
    }

    private fun uploadImageToFirebaseStorage(userId: String, userUpdates: HashMap<String, Any>) {
        val storageRef: StorageReference = storage.reference
        val imageRef = storageRef.child("profile_images/${UUID.randomUUID()}.jpg")

        selectedImageUri?.let { uri ->
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    // Get the download URL of the uploaded image
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        // Add the image URL to the user updates
                        userUpdates["profileImageUrl"] = downloadUri.toString()
                        // Update Firestore with the new username and image URL
                        updateFirestore(userId, userUpdates)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateFirestore(userId: String, userUpdates: HashMap<String, Any>) {
        db.collection("users").document(userId)
            .update(userUpdates)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                finish() // Close the activity
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 100
    }
}