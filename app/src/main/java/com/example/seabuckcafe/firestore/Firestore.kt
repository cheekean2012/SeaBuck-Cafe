package com.example.seabuckcafe.firestore

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.R
import com.example.seabuckcafe.adapters.AdminFoodListItemAdapter
import com.example.seabuckcafe.models.Admin
import com.example.seabuckcafe.models.AdminMenuItem
import com.example.seabuckcafe.models.User
import com.example.seabuckcafe.ui.login.LoginFragment
import com.example.seabuckcafe.ui.register.RegisterFragment
import com.example.seabuckcafe.utils.Constants
import com.example.seabuckcafe.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

class Firestore {

    private val mFirestore = FirebaseFirestore.getInstance()
    private var imageUrl = ""

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

    private fun getCurrentUserID(): String {
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
                        if (user != null && user.isUser) {
                            activity.userLoginSuccess(user)
                        } else {
                            // Find admin result if not the users id
                            getAdminDetails(activity)
                        }
                    }
                }
            }
    }

    private fun getAdminDetails(activity: Fragment) {
        mFirestore.collection(Constants.ADMIN)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.d(activity.javaClass.simpleName, document.toString())

                // Received the document ID and covert into the Admin Data model object
                val admin = document.toObject(Admin::class.java)

                when(activity) {
                    is LoginFragment -> {
                        // Transfer the result to login fragment
                        if (admin != null && !admin.isUser) {
                            activity.adminLoginSuccess(admin)
                        }
                    }
                }
            }
    }

    fun uploadFoodImageToCloudStorage(activity: Fragment, imageUri: Uri?, foodItem: AdminMenuItem){
        val storageReference = FirebaseStorage.getInstance().reference.child(
            Constants.FOOD_IMAGE + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageUri
            )
        )

        storageReference.putFile(imageUri!!).addOnSuccessListener { result ->
            Log.d("Image URL: ", result.metadata!!.reference!!.downloadUrl.toString() )

            result.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.d("Downloadable image URL ", uri.toString())
                foodItem.image = uri.toString()
                addFoodMenuItem(activity, foodItem)
            }
        }
    }

    private fun addFoodMenuItem(activity: Fragment, menu: AdminMenuItem ) {

        mFirestore.collection(Constants.MENUS)
            .add(menu)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                Toast.makeText(activity.requireContext(), "Item has been added!", Toast.LENGTH_SHORT).show()
                Utils().backward(activity, R.id.adminFoodItemListFragment)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun getFoodMenuItem(activity: Fragment, foodItem: ArrayList<AdminMenuItem>, recyclerView: RecyclerView) {

        mFirestore.collection(Constants.MENUS)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    foodItem.add(
                        AdminMenuItem(
                        document.data["image"].toString(),
                        document.data["title"].toString(),
                        document.data["type"].toString(),
                        document.data["price"].toString().toLong(),
                        document.data["description"].toString(),
                        document.data["available"].toString().toBoolean())
                    )
                }
                recyclerView.adapter = AdminFoodListItemAdapter(activity.requireContext(), foodItem)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
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