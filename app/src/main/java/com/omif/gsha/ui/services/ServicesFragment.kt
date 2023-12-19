package com.omif.gsha.ui.services

import android.app.AlertDialog
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
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.omif.gsha.R
import com.omif.gsha.databinding.FragmentServicesBinding
import com.omif.gsha.ui.appointment.AppointmentFragment
import com.omif.gsha.ui.messages.MessagesFragment
import com.omif.gsha.ui.uploads.UploadsFragment


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
        binding.textDerm.setOnClickListener { this.onClick() }
        binding.textDent.setOnClickListener { this.onDentalClick() }
        binding.textDiag.setOnClickListener { this.onLabClick() }
        binding.textPaed.setOnClickListener { this.onClick() }
        binding.textPsy.setOnClickListener { this.onClick() }
        binding.textGen.setOnClickListener { this.onClick() }
        binding.textEld.setOnClickListener { this.onClick() }
        binding.textDiab.setOnClickListener { this.onClick() }
        binding.textEmer.setOnClickListener { this.onClick() }
        binding.textIns.setOnClickListener{this.onClick()}
        binding.textPhar.setOnClickListener {
            var fragment: Fragment? = null
            fragment = UploadsFragment()
            replaceFragment(fragment)
        }

    }
}