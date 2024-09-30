package com.omif.gsha.ui.services

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.omif.gsha.R
import com.omif.gsha.adapter.CommonMethods
import com.omif.gsha.databinding.FragmentServicesBinding
import com.omif.gsha.ui.appointment.AppointmentFragment
import com.omif.gsha.ui.messages.MessagesFragment
import com.omif.gsha.ui.pharma.PharmaFragment


class ServicesFragment : Fragment() {

    private var _binding: FragmentServicesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val servicesViewModel =
                ViewModelProvider(this).get(ServicesViewModel::class.java)

        _binding = FragmentServicesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textServices
        servicesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        attachClick()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onClick() {
        val preferences = activity?.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)

        val textView = TextView(context)
        textView.text = "Select an option"
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 20f
        textView.setBackgroundColor(resources.getColor(R.color.purple_700))
        textView.setTextColor(Color.WHITE)
        textView.typeface = Typeface.DEFAULT_BOLD;
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setCustomTitle(textView)
            .setPositiveButton("Ok") { dialog, which ->
                val lw = (dialog as AlertDialog).listView
                val checkedItem = lw.adapter.getItem(lw.checkedItemPosition)
                if(checkedItem == "Consult Now") {

                    var fragment: Fragment? = null
                    fragment = MessagesFragment()
                    replaceFragment(fragment)
                }
                else{
                    var fragment: Fragment? = null
                    fragment = AppointmentFragment()
                    replaceFragment(fragment)
                }
            }
            .setNegativeButton("Close") { dialog, which ->
                // Do something else.
            }
            .setSingleChoiceItems(
                arrayOf("Consult Now", "Book Appointment"), 0
            ) { dialog, which ->
                // Do something.
            }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(20, 0, 0, 0)

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.apply {
                getButton(DialogInterface.BUTTON_POSITIVE).apply{
                    setBackgroundColor(resources.getColor(R.color.purple_700))
                    setTextColor(Color.WHITE)
                    typeface = Typeface.DEFAULT_BOLD;
                    layoutParams = params;
                }
                getButton(DialogInterface.BUTTON_NEGATIVE).apply{
                    setBackgroundColor(resources.getColor(R.color.purple_700))
                    setTextColor(Color.WHITE)
                    typeface = Typeface.DEFAULT_BOLD;
                }
            }

    }

    private fun onDentalClick() {
        val textView = TextView(context)
        textView.text = "GSHA Says"
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 20f
        textView.setBackgroundColor(resources.getColor(R.color.purple_700))
        textView.setTextColor(Color.WHITE)
        textView.typeface = Typeface.DEFAULT_BOLD;
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setCustomTitle(textView)
            .setPositiveButton("Ok") { dialog, which ->
                val lw = (dialog as AlertDialog).listView
                    var fragment: Fragment? = null
                    fragment = AppointmentFragment()
                    replaceFragment(fragment)
            }
            .setNegativeButton("Close") { dialog, which ->
                // Do something else.
            }
            .setSingleChoiceItems(
                arrayOf("Book Appointment"), 0
            ) { dialog, which ->
                // Do something.
            }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(20, 0, 0, 0)

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.apply {
            getButton(DialogInterface.BUTTON_POSITIVE).apply{
                setBackgroundColor(resources.getColor(R.color.purple_700))
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD;
                layoutParams = params;
            }
            getButton(DialogInterface.BUTTON_NEGATIVE).apply{
                setBackgroundColor(resources.getColor(R.color.purple_700))
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD;
            }
        }

    }


    private fun onLabClick() {
        val textView = TextView(context)
        textView.text = "GSHA Says"
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 20f
        textView.setBackgroundColor(resources.getColor(R.color.purple_700))
        textView.setTextColor(Color.WHITE)
        textView.typeface = Typeface.DEFAULT_BOLD;
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setCustomTitle(textView)
            .setPositiveButton("Ok") { dialog, which ->
                val lw = (dialog as AlertDialog).listView
                var fragment: Fragment? = null
                fragment = AppointmentFragment()
                replaceFragment(fragment)
            }
            .setNegativeButton("Close") { dialog, which ->
                // Do something else.
            }
            .setSingleChoiceItems(
                arrayOf("Book Appointment"), 0
            ) { dialog, which ->
                // Do something.
            }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(20, 0, 0, 0)

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.apply {
            getButton(DialogInterface.BUTTON_POSITIVE).apply{
                setBackgroundColor(resources.getColor(R.color.purple_700))
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD;
                layoutParams = params;
            }
            getButton(DialogInterface.BUTTON_NEGATIVE).apply{
                setBackgroundColor(resources.getColor(R.color.purple_700))
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD;
            }
        }

    }
    private fun replaceFragment(someFragment: Fragment?) {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        if (someFragment != null) {
            transaction.replace(com.omif.gsha.R.id.nav_host_fragment_activity_main, someFragment)
        }
        //transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun attachClick()
    {
        val preferences = activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        var editor = preferences?.edit()
        val inflater = LayoutInflater.from(this@ServicesFragment.context)
        var fragmentManager: FragmentManager = parentFragmentManager

        binding.textDerm.setOnClickListener {
            editor?.putString("selectedDept","Dermatology")
            editor?.commit()
            val customLayout: View = inflater.inflate(R.layout.derma_layout, null)
            this@ServicesFragment.context?.let { it1 -> CommonMethods.showServicesDialog(it1, fragmentManager, customLayout, "Dermatology") }
        }
        binding.textDent.setOnClickListener {
            editor?.putString("selectedDept","Dentistry")
            editor?.commit()
            this.onDentalClick() }
        binding.textDiag.setOnClickListener {
            editor?.putString("selectedDept","Lab")
            editor?.commit()
            this.onLabClick() }
        binding.textPaed.setOnClickListener {
            editor?.putString("selectedDept","Paediatrics")
            editor?.commit()
            val customLayout: View = inflater.inflate(R.layout.paeds_layout, null)
            this@ServicesFragment.context?.let { it1 -> CommonMethods.showServicesDialog(it1, fragmentManager, customLayout, "Paediatrics") }
        }
        binding.textPsy.setOnClickListener {
            editor?.putString("selectedDept","Psychiatrist")
            editor?.commit()
            val customLayout: View = inflater.inflate(R.layout.psych_layout, null)
            this@ServicesFragment.context?.let { it1 -> CommonMethods.showServicesDialog(it1, fragmentManager, customLayout, "Psychiatry") }
        }
        binding.textGen.setOnClickListener {
            editor?.putString("selectedDept","General")
            editor?.commit()
            val customLayout: View = inflater.inflate(R.layout.general_layout, null)
            this@ServicesFragment.context?.let { it1 -> CommonMethods.showServicesDialog(it1, fragmentManager, customLayout, "General") }
        }
        binding.textEld.setOnClickListener {
            editor?.putString("selectedDept","General")
            editor?.commit()
            val customLayout: View = inflater.inflate(R.layout.general_layout, null)
            this@ServicesFragment.context?.let { it1 -> CommonMethods.showServicesDialog(it1, fragmentManager, customLayout, "General") }
        }
        binding.textDiab.setOnClickListener {
            editor?.putString("selectedDept","General")
            editor?.commit()
            this.onClick() }
        binding.textEmer.setOnClickListener {
            editor?.putString("selectedDept","General")
            editor?.commit()
            this.onClick() }
        binding.textIns.setOnClickListener{
            editor?.putString("selectedDept","General")
            editor?.commit()
            this.onClick()}
        binding.textPhar.setOnClickListener {
            editor?.putString("selectedDept","Pharma")
            editor?.commit()
            var fragment: Fragment? = null
            fragment = PharmaFragment()
            replaceFragment(fragment)
        }

    }
}