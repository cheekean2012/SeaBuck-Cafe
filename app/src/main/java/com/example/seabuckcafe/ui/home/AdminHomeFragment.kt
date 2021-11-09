package com.example.seabuckcafe.ui.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentHomeAdminBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.services.NotificationService
import com.example.seabuckcafe.utils.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*







class AdminHomeFragment: Fragment() {

    private lateinit var binding: FragmentHomeAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeAdminFragment = this@AdminHomeFragment
        createNotificationChannel()
        Firestore().getUserOrderListPending(this, null)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun sendNotification() {
//        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(activity, AlarmReceiver::class.java)
//
//        pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, 0)
//
//        alarmManager.set(
//            AlarmManager.RTC_WAKEUP,
//            System.currentTimeMillis(),
//            pendingIntent
//        )

        Intent(context, NotificationService::class.java).also { intents ->
            requireContext().startService(intents)
        }
//        val startIntent = Intent(context, RingtonePlayingService::class.java)
//        requireContext().startService(startIntent);
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name: CharSequence = "foxAndroidReminderChannel"
            val descriptionText = "Channel For Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("foxAndroid", name, importance).apply {
                description = descriptionText
            }

            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun backToLogin() {
        Firebase.auth.signOut()
        Utils().forward(this, R.id.action_homeAdminFragment_to_loginFragment)
    }

    fun goToMenu() {
        Utils().forward(this, R.id.action_homeAdminFragment_to_adminFoodItemListFragment)
    }

    fun goToOrder() {
        Utils().forward(this, R.id.action_homeAdminFragment_to_adminOrderListFragment)
    }

    fun goToReport() {
        Utils().forward(this, R.id.action_homeAdminFragment_to_adminReportListFragment)
    }
}