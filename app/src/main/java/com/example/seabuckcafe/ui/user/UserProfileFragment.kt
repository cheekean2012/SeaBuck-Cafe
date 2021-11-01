package com.example.seabuckcafe.ui.user

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentUserProfileBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.PersonalInfoViewModel
import com.example.seabuckcafe.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton.SIZE_MINI
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    private val personalInfoViewModel: PersonalInfoViewModel by activityViewModels()
    private var mImageUri: Uri? = null


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
                .into(binding.profileImage)

            personalInfoViewModel.setProfileImage(mImageUri.toString().toUri())
            Firestore().updateUserProfilePicture(requireContext(), mImageUri)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.userProfileFragment = this@UserProfileFragment

        checkProfilePicture()

        // Set fab button size mini
        binding.floatingCameraButton.size = SIZE_MINI
        binding.floatingCameraButton.setOnClickListener { openGallery() }
        binding.topAppBar.setNavigationOnClickListener{ backward() }
    }

    private fun checkProfilePicture() {
        if (personalInfoViewModel.image.value.toString() != "") {
            Glide.with(requireContext())
                .load(personalInfoViewModel.image.value.toString())
                .into(binding.profileImage)
        }
    }

    private fun openGallery() {

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

    private fun backward() {
        Utils().backward(this, R.id.homeUserFragment)
    }

    fun goToAccount() {
        Utils().forward(this, R.id.action_userProfile_to_userAccountFragment)
    }

    fun goToEmailAddress() {
        Utils().forward(this, R.id.action_userProfileFragment_to_userAddressFragment)
    }

    fun goToContactUs() {
        Utils().forward(this, R.id.action_userProfileFragment_to_userContactFragment)
    }

}