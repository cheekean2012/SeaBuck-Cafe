package com.example.seabuckcafe.firestore

import android.content.ContentValues.TAG
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.seabuckcafe.models.User
import com.example.seabuckcafe.ui.login.LoginFragment
import com.example.seabuckcafe.ui.register.RegisterFragment
import com.example.seabuckcafe.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Firestore {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterFragment, userInfo: User) {

        // Create the users in start collection name. If already exists then it will not create again
        mFirestore.collection(Constants.USERS)
            // Create the user id in documentation for users field
            .document(userInfo.id)
            // Set userInfo in the field and SetOptions is set to merge if we needed it later on instead of replacing the field
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: $documentReference")

                // Call function from fragment for transfer toast message and forward to login screen
                activity.userSignUpSuccess()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun getCurrentUserID(): String {
        // An instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        // Check if current user is not empty and pass it to currentUserID
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Fragment) {
        // Get user in collection
        mFirestore.collection(Constants.USERS)
            // Get documentation id from the field of users
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.d(activity.javaClass.simpleName, document.toString())

                // Received the document ID and covert into the User Data model object
                val user = document.toObject(User::class.java)

                when(activity) {
                    is LoginFragment -> {
                        // Transfer the result to login fragment
                        if (user != null) {
                            activity.loginSuccess(user)
                        }
                    }
                }
            }
    }

    fun updateUserProfileData(activity: Fragment, userHashMap: HashMap<String, Any>) {

        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error while updating the user details.", e)
            }
    }
}