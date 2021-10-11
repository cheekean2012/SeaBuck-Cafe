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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seabuckcafe.R
import com.example.seabuckcafe.adapters.FoodTypeListItemAdapter
import com.example.seabuckcafe.databinding.DialogCustomListBinding
import com.example.seabuckcafe.databinding.FragmentAdminFoodDetailBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.AdminMenuItem
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
import java.util.*

class AdminFoodDetailFragment: Fragment() {
    private lateinit var binding: FragmentAdminFoodDetailBinding
    private lateinit var mDialog: Dialog
    private var mImageUri: Uri? = null
    private var mImageURL: String = ""

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
                requireContext().getContentResolver(),
                thumbnail,
                "",
                null
            )
            mImageUri = Uri.parse(path)

            // Set image and scale type
            binding.foodImage.setImageURI(mImageUri)
            binding.foodImage.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    // Gallery setting for new API
    private var galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            // Get gallery image uri
            mImageUri = it.data!!.data
            binding.foodImage.setImageURI(mImageUri)
            binding.foodImage.scaleType = ImageView.ScaleType.CENTER_CROP
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
        binding.adminFoodDetailFragment = this@AdminFoodDetailFragment

        binding.foodImage.setOnClickListener{ uploadPhoto() }
        binding.topAppBar.setNavigationOnClickListener { backward() }
        binding.foodTypeEditText.setOnClickListener { foodTypeDialog(resources.getString(R.string.select_food_type), Constants.foodType()) }
        binding.addFoodItem.setOnClickListener { uploadFoodItem() }
    }

    private fun uploadFoodItem() {

        val title = binding.foodTitleText.text.toString().trim { it <= ' ' }
        val type = binding.foodTypeEditText.text.toString().trim { it <= ' ' }
        val price = binding.foodPriceText.text.toString().trim { it <= ' ' }
        val description = binding.foodDescriptionText.text.toString().trim { it <= ' ' }
        val switch = binding.switches.isChecked

        if (mImageUri != null) {

            if (title.isNotEmpty() && type.isNotEmpty() && price.isNotEmpty() && description.isNotEmpty()) {

                val foodItem = AdminMenuItem(
                    "",
                    title,
                    type,
                    price.toLong(),
                    description,
                    switch
                )

                Firestore().uploadFoodImageToCloudStorage(this, mImageUri, foodItem)
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
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        galleryLauncher.launch(galleryIntent)
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

        // Set title
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

    private fun backward() {
        Utils().backward(this, R.id.adminFoodItemListFragment)
    }
}