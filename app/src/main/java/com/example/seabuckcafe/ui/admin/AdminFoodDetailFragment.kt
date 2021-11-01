package com.example.seabuckcafe.ui.admin

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.seabuckcafe.R
import com.example.seabuckcafe.adapters.FoodTypeListItemAdapter
import com.example.seabuckcafe.databinding.DialogCustomListBinding
import com.example.seabuckcafe.databinding.FragmentAdminFoodDetailBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.AdminMenuItem
import com.example.seabuckcafe.models.MenuSharedViewModel
import com.example.seabuckcafe.utils.Constants
import com.example.seabuckcafe.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.ByteArrayOutputStream

class AdminFoodDetailFragment: Fragment() {
    private lateinit var binding: FragmentAdminFoodDetailBinding
    private lateinit var mDialog: Dialog
    private var mImageUri: Uri? = null
    private val shareViewModel: MenuSharedViewModel by activityViewModels()

    // Camera setting for new API
    private var cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Get camera capture data
            val thumbnail= result.data!!.extras?.get("data") as Bitmap

            val bytes = ByteArrayOutputStream()
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

            val path: String = MediaStore.Images.Media.insertImage(
                requireContext().contentResolver,
                thumbnail,
                "",
                null
            )
            mImageUri = Uri.parse(path)

            // Set image and scale type
            Glide.with(requireContext())
                .load(mImageUri)
                .into(binding.foodImage)
        }
    }

    // Gallery setting for new API
    private var galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {

        if (it != null) {
            // Get gallery image uri
            mImageUri = it
            Log.d("image Uri", it.toString())

            Glide.with(requireContext())
                .load(mImageUri)
                .into(binding.foodImage)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminFoodDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            adminFoodDetailFragment = this@AdminFoodDetailFragment

            Glide.with(requireContext())
                .load(shareViewModel.image.value.toString())
                .into(foodImage)

            // Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner

            // Assign the view model to a property in the binding class
            viewModel = shareViewModel

            // Set to select use camera or gallery function to upload the picture on UI Controller
            foodImage.setOnClickListener{ uploadPhoto() }

            // Set top action bar item click
            topAppBar.setNavigationOnClickListener { backward() }

            // Set to list out food type in recycler view
            foodTypeEditText.setOnClickListener { foodTypeDialog(resources.getString(R.string.select_food_type), Constants.foodType()) }

            // Set to upload food menu item to firestore
            addFoodItem.setOnClickListener { uploadFoodMenuItem() }
        }
    }

    private fun uploadFoodMenuItem() {

        // Check the share view model retrieve data from admin food item list is not null
        // If not null, it will update to firebase
        if (shareViewModel.image.value != null && shareViewModel.title.value != null
            && shareViewModel.type.value != null && shareViewModel.description.value != null
            && shareViewModel.price.value != null) {

                binding.apply {
                    shareViewModel.setTitle(foodTitleText.text.toString())
                    shareViewModel.setType(foodTypeEditText.text.toString())
                    shareViewModel.setPrice(foodPriceText.text.toString())
                    shareViewModel.setDescription(foodDescriptionText.text.toString())
                    shareViewModel.setAvailable(switches.isChecked.toString().toBoolean())
                }

                val updateFoodItem = AdminMenuItem(
                    shareViewModel.id.value.toString(),
                    shareViewModel.image.value.toString(),
                    shareViewModel.title.value.toString(),
                    shareViewModel.type.value.toString(),
                    shareViewModel.price.value.toString(),
                    shareViewModel.description.value.toString(),
                    shareViewModel.available.value!!
            )
            Firestore().updateFoodMenuItem(this, mImageUri, updateFoodItem)

        // Upload to firebase
        } else {

            val title = binding.foodTitleText.text.toString().trim { it <= ' ' }
            val type = binding.foodTypeEditText.text.toString().trim { it <= ' ' }
            val price = binding.foodPriceText.text.toString().trim { it <= ' ' }
            val description = binding.foodDescriptionText.text.toString().trim { it <= ' ' }
            val switch = binding.switches.isChecked

            if (mImageUri != null) {

                if (title.isNotEmpty() && type.isNotEmpty() && price.isNotEmpty() && description.isNotEmpty()) {

                    val foodItem = AdminMenuItem(
                        "",
                        "",
                        title,
                        type,
                        price,
                        description,
                        switch
                    )

                    Firestore().uploadFoodMenuItem(this, mImageUri, foodItem)
                } else {
                    Toast.makeText(requireContext(),
                        "Please fill all the info before add food menu!", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(requireContext(),
                    "Please insert image before add food menu!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun uploadPhoto() {
        mDialog = Dialog(requireContext())
        val inflater = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_image_selector, null)
        mDialog.setContentView(inflater)

        // Camera function
        mDialog.findViewById<ImageView>(R.id.cameraImage).setOnClickListener {

            Dexter.withContext(requireContext())
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(object: MultiplePermissionsListener {
                    // Permission granted
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                        // Checked if report is not empty
                       report?.let {
                           if (report.areAllPermissionsGranted()) {
                               val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                               cameraLauncher.launch(intent)
                           }
                       }
                    }
                    // Permission denied checked
                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermission()
                    }

                }).onSameThread().check()

            mDialog.dismiss()
        }

        // Gallery function
        mDialog.findViewById<ImageView>(R.id.galleryImage).setOnClickListener{

            Dexter.withContext(requireContext())
                .withPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(object: PermissionListener {
                    // Permission granted
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        galleryLauncher.launch("image/*")
                    }
                    // Permission denied
                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Toast.makeText(requireContext(), "You have denied the storage permission to select image",
                            Toast.LENGTH_SHORT).show()
                    }
                    // Permission denied checked if the user or admin denied before
                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                        showRationalDialogForPermission()
                    }
                }).onSameThread().check()

            mDialog.dismiss()
        }
        mDialog.show()
    }

    // Permission denied dialog
    private fun showRationalDialogForPermission() {
        MaterialAlertDialogBuilder(requireContext()).setMessage("It looks like you have turned off permission" +
                " required for this feature. It can be enabled under application setting.")
            .setPositiveButton("Go to setting") {
                _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") {
                dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    // Set Item in food type
    private fun selectedItem(item: String) {
        binding.foodTypeEditText.setText(item)
    }

    // Create food type dialog in recyclerview
    private fun foodTypeDialog(title: String, itemList: List<String>) {

        mDialog = Dialog(requireContext())
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mDialog.setContentView(binding.root)

        // Set dialog title
        binding.dialogFoodTitle.text = title

        binding.dialogFoodRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = FoodTypeListItemAdapter(requireContext(), itemList)
        binding.dialogFoodRecyclerView.adapter = adapter
        mDialog.show()

        // After clicked get position and the food type from recyclerview
        adapter.setOnItemClickListener(object : FoodTypeListItemAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, foodType: String) {
                Log.d("item", "current clicked is $foodType")
                mDialog.dismiss()
                selectedItem(foodType)
            }
        })
    }

    // Back to previous layout
    private fun backward() {
        Utils().backward(this, R.id.adminFoodItemListFragment)
    }
}