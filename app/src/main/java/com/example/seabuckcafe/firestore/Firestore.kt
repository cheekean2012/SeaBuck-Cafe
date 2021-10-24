package com.example.seabuckcafe.firestore

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.R
import com.example.seabuckcafe.adapters.AdminFoodListItemAdapter
import com.example.seabuckcafe.adapters.UserAddressAdapter
import com.example.seabuckcafe.adapters.UserFoodListItemAdapter
import com.example.seabuckcafe.models.*
import com.example.seabuckcafe.ui.login.LoginFragment
import com.example.seabuckcafe.ui.register.RegisterFragment
import com.example.seabuckcafe.utils.Constants
import com.example.seabuckcafe.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata

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

    fun uploadFoodMenuItem(activity: Fragment, imageUri: Uri?, foodItem: AdminMenuItem) {

        // Generate a document id
        val document = mFirestore.collection(Constants.MENUS).document()

        // Set image path file
        val storageReference = FirebaseStorage.getInstance().reference.child(
            "menus/" + document.id  + "menu.jpg"
        )

        // Set image mime type
        val metadata = storageMetadata {
            contentType = "image/jpeg"
        }

        storageReference.putFile(imageUri!!, metadata).addOnSuccessListener { result ->
            Log.d("Image URL: ", result.metadata!!.reference!!.downloadUrl.toString() )

            result.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.d("Downloadable image URL ", uri.toString())
                foodItem.image = uri.toString()

                mFirestore.collection(Constants.MENUS).document(document.id)
                    .set(foodItem, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(activity.requireContext(), "Added successful!", Toast.LENGTH_SHORT).show()
                        Utils().backward(activity, R.id.adminFoodItemListFragment)
                    }
            }
        }
    }

    fun getFoodMenuItem(activity: Fragment,
                        recyclerView: RecyclerView) {

        mFirestore.collection(Constants.MENUS)
            .orderBy("title", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->

                /**
                Custom object to return the data field of the document in QuerySnapShot
                Adapter's MutableList can works in MutableList and ArrayList
                But if Adapter is ArrayList, it doesn't works MutableList
                 */
                val foodItem = result.toObjects(AdminMenuItem::class.java) as ArrayList<AdminMenuItem>

                recyclerView.adapter = AdminFoodListItemAdapter(activity, activity.requireContext(), foodItem)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    fun updateFoodMenuItem(activity: Fragment, imageUri: Uri?, foodItem: AdminMenuItem) {

        // If the admin didn't change picture, it will only update others information and save same picture
        if (imageUri == null) {
            mFirestore.collection(Constants.MENUS)
                .document(foodItem.id!!)
                .update(mapOf(
                    "image" to foodItem.image,
                    "title" to foodItem.title,
                    "type" to foodItem.type,
                    "price" to foodItem.price,
                    "description" to foodItem.description,
                    "available" to foodItem.available
                )).addOnSuccessListener {
                    Toast.makeText(activity.requireContext(), "Updated successful!", Toast.LENGTH_SHORT).show()
                    Utils().backward(activity, R.id.adminFoodItemListFragment)
                }
        } else {
            // Set image path file
            val storageReference = FirebaseStorage.getInstance().reference.child(
                "menus/" + foodItem.id  + "menu.jpg"
            )
            // Set image mime type
            val metadata = storageMetadata {
                contentType = "image/jpeg"
            }
            // Upload picture to storage
            storageReference.putFile(imageUri, metadata).addOnSuccessListener { result ->
                Log.d("Image URL: ", result.metadata!!.reference!!.downloadUrl.toString() )

                result.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("Downloadable image URL ", uri.toString())
                    // Updated all information
                    mFirestore.collection(Constants.MENUS)
                        .document(foodItem.id!!)
                        .update(mapOf(
                            "image" to uri.toString(),
                            "title" to foodItem.title,
                            "type" to foodItem.type,
                            "price" to foodItem.price,
                            "description" to foodItem.description,
                            "available" to foodItem.available
                        )).addOnSuccessListener {
                            Toast.makeText(activity.requireContext(), "Updated successful!", Toast.LENGTH_SHORT).show()
                            Utils().backward(activity, R.id.adminFoodItemListFragment)
                        }
                }
            }
        }
    }

    fun deleteFoodMenuItem(activity: Fragment, id: String) {
        // Delete picture in storage
        FirebaseStorage.getInstance().reference.child(
            "menus/"+ id + "menu.jpg"
        ).delete()
        // Delete menu item
        mFirestore.collection(Constants.MENUS).document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(activity.requireContext(), "Item has been deleted", Toast.LENGTH_SHORT).show()
            }
    }

    fun filterFoodMenuItemType(
        activity: Fragment,
        foodType: String,
        recyclerView: RecyclerView
    ) {

        mFirestore.collection(Constants.MENUS)
            .whereEqualTo("type", foodType)
            .orderBy("title", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->

                /**
                Custom object to return the data field of the document in QuerySnapShot
                Adapter's MutableList can works in MutableList and ArrayList
                But if Adapter is ArrayList, it doesn't works MutableList
                 */
                val foodItem = result.toObjects(AdminMenuItem::class.java)

                recyclerView.adapter = AdminFoodListItemAdapter(activity, activity.requireContext(), foodItem)
            }
    }

    fun userGetFoodMenuItem(activity: Fragment, type:String, recyclerView: RecyclerView) {
        mFirestore.collection(Constants.MENUS)
            .whereEqualTo("type", type)
            .whereEqualTo("available", true)
            .orderBy("title", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->

                val foodItem = result.toObjects(UserMenuItem::class.java)

                recyclerView.adapter = UserFoodListItemAdapter(activity, activity.requireContext(), foodItem)
            }
    }

    fun addUserAddress(activity: Fragment, address: UserAddressData, uid: String) {
        mFirestore.collection(Constants.ADDRESS)
            .document(uid)
            .collection(Constants.ADDRESS)
            .document()
            .set(address, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(activity.requireContext(), "Added successful!", Toast.LENGTH_SHORT).show()
            }
    }

    fun getUserAddress(
        activity: Fragment,
        userID: String,
        recyclerView: RecyclerView, userAddress: MutableList<UserAddressData>) {
        mFirestore.collection(Constants.ADDRESS)
            .document(userID)
            .collection(Constants.ADDRESS)
            .get()
            .addOnSuccessListener { result ->

                for (document in result) {
                    userAddress.add(document.toObject(UserAddressData::class.java))
                }

                recyclerView.adapter = UserAddressAdapter(activity, activity.requireContext(), userAddress)
            }
    }

    fun deleteUserAddress(activity: Fragment, documentId: String) {
        val auth = FirebaseAuth.getInstance()

        mFirestore.collection(Constants.ADDRESS)
            .document(auth.uid!!)
            .collection(Constants.ADDRESS)
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(activity.requireContext(), "Deleted Successful!", Toast.LENGTH_SHORT).show()
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

