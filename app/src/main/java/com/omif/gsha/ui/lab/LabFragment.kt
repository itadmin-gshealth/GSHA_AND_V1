package com.omif.gsha.ui.lab

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.omif.gsha.R
import com.omif.gsha.databinding.FragmentAppointmentBinding
import com.omif.gsha.databinding.FragmentLabBinding


class LabFragment : Fragment() {

    private var _binding: FragmentLabBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LabViewModel
    private lateinit var btnAppointment: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val labViewModel =
            ViewModelProvider(this).get(LabViewModel::class.java)

        _binding = FragmentLabBinding.inflate(inflater, container, false)
        val root: View = binding.root
        btnAppointment = binding.btnAppointment
        btnAppointment.setOnClickListener(View.OnClickListener {
            this.onClick()
        })
        return root   }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LabViewModel::class.java)
        // TODO: Use the ViewModel
    }


    private fun onClick() {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(20, 0, 0, 0)
        val textView = TextView(context)
        textView.text = "GSHA Says"
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 20f
        textView.setBackgroundColor(resources.getColor(R.color.purple_700))
        textView.setTextColor(Color.WHITE)
        textView.typeface = Typeface.DEFAULT_BOLD;

        val alertDialog = AlertDialog.Builder(this@LabFragment.context).create()
        alertDialog.setCustomTitle(textView)
        alertDialog.setMessage("Appointment has been requested. You will receive a confirmation text on your registered mobile number shortly. ")
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "OK"
        ) { dialog, which -> dialog.dismiss() }
        alertDialog.show()
        alertDialog.apply {
            getButton(DialogInterface.BUTTON_NEUTRAL).apply{
                setBackgroundColor(resources.getColor(R.color.purple_700))
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD;
                layoutParams = params;
            }
        }
    }


    private fun sendEmail() {
        val emailsend = "sathia.raphael@gsoim.org"
        val emailsubject = "Appointment Booking"
        val emailbody = "{name}, {phone number} - has requested a booking on {Date}"

        // define Intent object with action attribute as ACTION_SEND
        val intent = Intent(Intent.ACTION_SEND)

        // add three fields to intent using putExtra function
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailsend))
        intent.putExtra(Intent.EXTRA_SUBJECT, emailsubject)
        intent.putExtra(Intent.EXTRA_TEXT, emailbody)

        // set type of intent
        intent.type = "message/rfc822"

        // startActivity with intent with chooser as Email client using createChooser function
        startActivity(Intent.createChooser(intent, "Choose an Email client :"))
    }

}



