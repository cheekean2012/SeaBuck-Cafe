package com.example.seabuckcafe.ui.admin

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.R
import com.example.seabuckcafe.adapters.OrderListItemAdapter
import com.example.seabuckcafe.databinding.DialogCalendarSelectionBinding
import com.example.seabuckcafe.databinding.FragmentAdminReportListBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.UserOrderList
import com.example.seabuckcafe.utils.Utils
import com.google.firebase.Timestamp
import com.whiteelephant.monthpicker.MonthPickerDialog
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class AdminReportListFragment: Fragment() {

    private lateinit var binding: FragmentAdminReportListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var orderList: MutableList<UserOrderList>
    private lateinit var dateList: MutableList<Timestamp>
    private lateinit var mDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminReportListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderList = ArrayList()
        dateList = ArrayList()

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = OrderListItemAdapter(this, requireContext(), orderList)
        Firestore().getOrderList(this, recyclerView)

        binding.topAppBar.setNavigationOnClickListener { backward() }
        binding.topAppBar.setOnMenuItemClickListener { menuItem -> optionsItemSelected(menuItem) }
    }

    private fun optionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filterDate -> {

                mDialog = Dialog(requireContext())
                val binding: DialogCalendarSelectionBinding = DialogCalendarSelectionBinding.inflate(layoutInflater)
                mDialog.setContentView(binding.root)

                binding.all.setOnClickListener { filterAll() }
                binding.dateCalendar.setOnClickListener { openDateCalendar() }
                binding.monthCalendar.setOnClickListener { openMonthCalendar() }
                mDialog.show()
            }
        }
        return false
    }

    private fun filterAll() {
        mDialog.dismiss()
        Firestore().getOrderList(this, recyclerView)
    }

    private fun openMonthCalendar() {
        mDialog.dismiss()
        val calendar = Calendar.getInstance()

        val monthYearPicker = MonthPickerDialog.OnDateSetListener { month, year ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, (month + 1))

            val month = calendar.get(Calendar.MONTH).toString()
            val year = calendar.get(Calendar.YEAR).toString()

            Log.d("time", "$month $year")

            Firestore().filterOrderList(this, recyclerView, month, year, null)
        }

        val date = LocalDate.now()

        MonthPickerDialog.Builder(
            requireContext(),
            monthYearPicker,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH))
            .setMinYear(1970)
            .setActivatedYear(calendar.get(Calendar.YEAR))
            .setMaxYear(date.year)
            .setActivatedMonth(calendar.get(Calendar.MONTH))
            .build().show()
    }

    private fun openDateCalendar() {
        mDialog.dismiss()
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, (month + 1))
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH).toString()
            val month = calendar.get(Calendar.MONTH).toString()
            val year = calendar.get(Calendar.YEAR).toString()

            Log.d("date check", "$dayOfMonth $month $year")
            Firestore().filterOrderList(this, recyclerView, month, year, dayOfMonth)
        }

        val style = AlertDialog.THEME_DEVICE_DEFAULT_DARK
        DatePickerDialog(
            requireContext(),
            style,
            datePicker,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    // Back to previous layout
    private fun backward() {
        Utils().backward(this, R.id.homeAdminFragment)
    }
}