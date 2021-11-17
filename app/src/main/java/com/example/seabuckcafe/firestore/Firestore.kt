package com.example.seabuckcafe.firestore

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.R
import com.example.seabuckcafe.SplashScreenFragment
import com.example.seabuckcafe.adapters.*
import com.example.seabuckcafe.models.*
import com.example.seabuckcafe.services.NotificationService
import com.example.seabuckcafe.ui.admin.AdminFoodDetailFragment
import com.example.seabuckcafe.ui.home.AdminHomeFragment
import com.example.seabuckcafe.ui.login.LoginFragment
import com.example.seabuckcafe.ui.order.AdminOrderDeliveringListFragment
import com.example.seabuckcafe.ui.order.AdminOrderPendingListFragment
import com.example.seabuckcafe.ui.order.AdminOrderPrepareListFragment
import com.example.seabuckcafe.ui.register.RegisterFragment
import com.example.seabuckcafe.ui.user.UserAddressFragment
import com.example.seabuckcafe.ui.user.UserContactFragment
import com.example.seabuckcafe.ui.user.UserPaymentFragment
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
                    is SplashScreenFragment -> {
                        // Transfer the result to login fragment
                        if (user != null && user.isUser) {
                            activity.userLoginSuccess(user)
                        } else {
                            // Find admin result if not the users id
                            getAdminDetails(activity)
                        }
                    }

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
                    is SplashScreenFragment -> {
                        // Transfer the result to login fragment
                        if (admin != null && !admin.isUser) {
                            activity.adminLoginSuccess(admin)
                        }
                    }
                    is LoginFragment -> {
                        // Transfer the result to login fragment
                        if (admin != null && !admin.isUser) {
                            activity.adminLoginSuccess(admin)
                        }
                    }
                }
            }
    }

    fun uploadFoodMenuItem(activity: Fragment, context: Context, imageUri: Uri?, foodItem: AdminMenuItem) {

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

        when (activity) {
            is AdminFoodDetailFragment -> {
                activity.showProgress()
                storageReference.putFile(imageUri!!, metadata).addOnSuccessListener { result ->
                    Log.d("Image URL: ", result.metadata!!.reference!!.downloadUrl.toString() )

                    result.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        Log.d("Downloadable image URL ", uri.toString())
                        foodItem.image = uri.toString()


                        mFirestore.collection(Constants.MENUS).document(document.id)
                            .set(foodItem, SetOptions.merge())
                            .addOnSuccessListener {
                                activity.closeProgress()
                                Toast.makeText(context, "Added successful!", Toast.LENGTH_SHORT).show()
                                Utils().backward(activity, R.id.adminFoodItemListFragment)
                            }.addOnFailureListener {
                                activity.closeProgress()
                            }
                    }
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

    fun updateFoodMenuItem(activity: Fragment, context: Context, imageUri: Uri?, foodItem: AdminMenuItem) {

        when (activity) {
            is AdminFoodDetailFragment -> {
                activity.showProgress()
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
                            activity.closeProgress()
                            Toast.makeText(context, "Updated successful!", Toast.LENGTH_SHORT).show()
                            Utils().backward(activity, R.id.adminFoodItemListFragment)
                        }.addOnFailureListener {
                            activity.closeProgress()
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
                                    activity.closeProgress()
                                    Toast.makeText(context, "Updated successful!", Toast.LENGTH_SHORT).show()
                                    Utils().backward(activity, R.id.adminFoodItemListFragment)
                                }.addOnFailureListener {
                                    activity.closeProgress()
                                }
                        }
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

    fun addUserAddress(activity: Fragment, address: UserAddressList, uid: String) {
        mFirestore.collection(Constants.ADDRESS)
            .document(uid)
            .collection(Constants.USER_ADDRESS)
            .document()
            .set(address, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(activity.requireContext(), "Added successful!", Toast.LENGTH_SHORT).show()
            }
    }

    fun getUserAddress(
        activity: Fragment,
        userID: String,
        recyclerView: RecyclerView,
        userAddress: MutableList<UserAddressList>) {
        mFirestore.collection(Constants.ADDRESS)
            .document(userID)
            .collection(Constants.USER_ADDRESS)
            .get()
            .addOnSuccessListener { result ->

                for (document in result) {
                    userAddress.add(document.toObject(UserAddressList::class.java))
                }

                when (activity) {
                    is UserPaymentFragment -> {
                        recyclerView.adapter = UserPaymentAddressListAdapter(activity, activity.requireContext(), userAddress)
                        activity.selectAddress(recyclerView.adapter as UserPaymentAddressListAdapter)
                    }
                     is UserAddressFragment -> {
                        recyclerView.adapter = UserAddressAdapter(activity, activity.requireContext(), userAddress)
                    }

                }
            }
    }

    fun updateUserAddress(activity: Fragment, id: String?, newAddress: String) {
        val auth = FirebaseAuth.getInstance()

        if (id != null) {
            mFirestore.collection(Constants.ADDRESS)
                .document(auth.uid!!)
                .collection(Constants.USER_ADDRESS)
                .document(id)
                .update(mapOf(
                    "address" to newAddress
                )).addOnSuccessListener {
                    Toast.makeText(activity.requireContext(),"Edited Successful!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun getOneUserAddress(activity: Fragment, userID: String) {
        mFirestore.collection(Constants.ADDRESS)
            .document(userID)
            .collection(Constants.USER_ADDRESS)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val address = result.toObjects(UserAddressList::class.java)

                when (activity) {
                    is UserPaymentFragment -> {
                        activity.setAddress(address)
                    }
                }
            }
    }

    fun deleteUserAddress(activity: Fragment, documentId: String) {
        val auth = FirebaseAuth.getInstance()

        mFirestore.collection(Constants.ADDRESS)
            .document(auth.uid!!)
            .collection(Constants.USER_ADDRESS)
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(activity.requireContext(), "Deleted Successful!", Toast.LENGTH_SHORT).show()
            }

    }

    fun addUserOrderList(context: Context, orderList: UserOrderList) {
        mFirestore.collection(Constants.ORDERS)
            .document()
            .set(orderList, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(context, "Successful Payment", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e -> Log.e("error", "$e") }
    }

    fun getUserOrderList(
        activity: Fragment,
        userID: String,
        recyclerView: RecyclerView
    ) {
        mFirestore.collection(Constants.ORDERS)
            .whereEqualTo("userID", userID)
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->

                val orderItem = result.toObjects(UserOrderList::class.java)
                recyclerView.adapter = OrderListItemAdapter(activity, activity.requireContext(), orderItem)
            }
    }

    fun updateUserName(context: Context, name: String) {

        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(mapOf(
                "userName" to name
            ))
            .addOnSuccessListener {
                Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error while updating the user details.", e)
            }
    }

    fun updateUserPhoneNumber(context: Context, phoneNumber: String) {

        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(mapOf(
                "phoneNumber" to phoneNumber
            ))
            .addOnSuccessListener {
                Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error while updating the user details.", e)
            }
    }

    fun updateUserProfilePicture(context: Context, imageUri: Uri?) {

        val documentID = getCurrentUserID()

        // Set image path file
        val storageReference = FirebaseStorage.getInstance().reference.child(
            "profile/$documentID.jpg"
        )

        // Set image mime type
        val metadata = storageMetadata {
            contentType = "image/jpeg"
        }

        storageReference.putFile(imageUri!!, metadata).addOnSuccessListener { result ->
            Log.d("Image URL: ", result.metadata!!.reference!!.downloadUrl.toString() )

            result.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.d("Downloadable image URL ", uri.toString())

                mFirestore.collection(Constants.USERS)
                    .document(documentID)
                    .update(mapOf(
                        "image" to uri.toString()
                    ))
            }
        }
    }

    fun getAdminContactInfo(activity: Fragment) {
        mFirestore.collection(Constants.ADMIN)
            .get()
            .addOnSuccessListener {document ->

                val info = document.documents[0].data

                when (activity) {
                    is UserContactFragment -> activity.getCafeInfo(info)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getUserOrderListPending(
        activity: Fragment,
        recyclerView: RecyclerView?
    ) {
        mFirestore.collection(Constants.ORDERS)
            .whereEqualTo("status", Constants.STATUS_PENDING)
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->

                when (activity) {
                    is AdminOrderPendingListFragment -> {
                        val orderItem = result.toObjects(UserOrderList::class.java)
                        recyclerView!!.adapter = OrderListItemAdapter(activity, activity.requireContext(), orderItem)

                        for (document in result) {
                            if (document.get("status").toString() == "pending") {
                                // Call Notification
                                Intent(activity.requireContext(), NotificationService::class.java).also { intents ->
                                    activity.requireContext().startForegroundService(intents)
                                }
                            }
                        }
                    }
                    is AdminHomeFragment -> {
                        for (document in result) {
                            if (document.get("status").toString() == "pending") {
                                // Call Notification
                                Intent(activity.requireContext(), NotificationService::class.java).also { intents ->
                                    activity.requireContext().startForegroundService(intents)
                                }
                            }
                        }
                    }
                }
            }.addOnFailureListener { e -> Log.e("error", "$e") }
    }

    fun getUserOrderListPrepare(activity: Fragment, recyclerView: RecyclerView?) {
        mFirestore.collection(Constants.ORDERS)
            .whereEqualTo("status", Constants.STATUS_ON_PREPARE)
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->

                when (activity) {
                    is AdminOrderPrepareListFragment -> {
                        val orderItem = result.toObjects(UserOrderList::class.java)
                        recyclerView!!.adapter = OrderListItemAdapter(activity, activity.requireContext(), orderItem)
                    }
                }
            }
    }

    fun getUserOrderListDelivery(activity: Fragment, recyclerView: RecyclerView?) {
        mFirestore.collection(Constants.ORDERS)
            .whereEqualTo("status", Constants.STATUS_DELIVERING)
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->

                when (activity) {
                    is AdminOrderDeliveringListFragment -> {
                        val orderItem = result.toObjects(UserOrderList::class.java)
                        recyclerView!!.adapter = OrderListItemAdapter(activity, activity.requireContext(), orderItem)
                    }
                }
            }
    }

    fun updateUserOrderList(activity: Fragment, context: Context, status: String, id: String) {
        mFirestore.collection(Constants.ORDERS)
            .document(id)
            .update(mapOf(
                "status" to status
            ))
            .addOnSuccessListener {
                Toast.makeText(context, "Updated successful", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e -> Log.e("error", "$e") }
    }

    fun cancelUserOrderList(context: Context, status: String, reason: String, id: String) {
        mFirestore.collection(Constants.ORDERS)
            .document(id)
            .update(mapOf(
                "status" to status,
                "reason" to reason
            ))
            .addOnSuccessListener {
                Toast.makeText(context, "Canceled successful", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e -> Log.e("error", "$e") }
    }

    fun getOrderList(activity: Fragment, recyclerView: RecyclerView) {
        mFirestore.collection(Constants.ORDERS)
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val orderItem = result.toObjects(UserOrderList::class.java)
                recyclerView.adapter = OrderListItemAdapter(activity, activity.requireContext(), orderItem)
            }
    }

    fun filterOrderList(activity: Fragment, recyclerView: RecyclerView, month: String, year: String, dayOfMonth: String?) {
        if (dayOfMonth == null) {
            Log.d("is day of month?", "true")

            mFirestore.collection(Constants.ORDERS)
                .whereEqualTo("month", month)
                .whereEqualTo("year", year)
                .get()
                .addOnSuccessListener { result ->

                    val orderItem = result.toObjects(UserOrderList::class.java)

                    orderItem.sortByDescending { it.date }

                    recyclerView.adapter = OrderListItemAdapter(activity, activity.requireContext(), orderItem)
                }
        } else {
            mFirestore.collection(Constants.ORDERS)
                .whereEqualTo("month", month)
                .whereEqualTo("year", year)
                .whereEqualTo("dayOfMonth", dayOfMonth)
                .get()
                .addOnSuccessListener { result ->
                    val orderItem = result.toObjects(UserOrderList::class.java)

                    orderItem.sortByDescending { it.date }

                    recyclerView.adapter = OrderListItemAdapter(activity, activity.requireContext(), orderItem)
                }.addOnFailureListener {e -> Log.e("error", "$e") }
        }
    }
}

