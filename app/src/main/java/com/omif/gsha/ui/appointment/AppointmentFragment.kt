package com.omif.gsha.ui.appointment

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.omif.gsha.R
import com.omif.gsha.adapter.AppExecutors
import com.omif.gsha.adapter.Credentials
import com.omif.gsha.databinding.FragmentAppointmentBinding
import com.omif.gsha.ui.services.ServicesFragment
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class AppointmentFragment : Fragment() {

    private var _binding: FragmentAppointmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AppointmentViewModel
    private lateinit var btnAppointment: Button

    lateinit var appExecutors: AppExecutors

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val appointmentViewModel =
            ViewModelProvider(this).get(AppointmentViewModel::class.java)

        _binding = FragmentAppointmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        btnAppointment = binding.btnAppointment
        btnAppointment.setOnClickListener(View.OnClickListener {
            this.onClick()
        })
        return root   }

    override fun onAttach(context: Context) {
        appExecutors = AppExecutors()
        this@AppointmentFragment.context?.let { super.onAttach(it) }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AppointmentViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun onClick() {
        //sendSms("6505551212","testing", 0)
        //sendSms1("6505551212","Appointment",false)
        sendEmail()
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

        val alertDialog = AlertDialog.Builder(this@AppointmentFragment.context).create()
        alertDialog.setCustomTitle(textView)
        alertDialog.setMessage("Appointment has been requested. You will receive a confirmation text on your registered mobile number shortly. ")
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "OK"
        ) { dialog, which ->   var fragment: Fragment? = null
            fragment = ServicesFragment()
            replaceFragment(fragment) }
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

    private fun replaceFragment(someFragment: Fragment?) {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        if (someFragment != null) {
            transaction.replace(com.omif.gsha.R.id.nav_host_fragment_activity_main, someFragment)
        }
        transaction.setReorderingAllowed(true)
        transaction.commit()
    }
    private fun sendSms1(phonenumber: String, message: String, isBinary: Boolean) {
        try{
        val manager: SmsManager = SmsManager.getDefault()
        val piSend = PendingIntent.getBroadcast(this@AppointmentFragment.context, 0, Intent("SMS Sent"),
            PendingIntent.FLAG_IMMUTABLE)
        val piDelivered = PendingIntent.getBroadcast(this@AppointmentFragment.context, 0, Intent("SMS DELIVERED"),
            PendingIntent.FLAG_IMMUTABLE)
        if (isBinary) {
            val data = ByteArray(message.length)
            var index = 0
            while (index < message.length && index < 160) {
                data[index] = message[index].code.toByte()
                ++index
            }
            manager.sendDataMessage(phonenumber, null,8091 as Short, data, piSend, piDelivered)
        } else {
            val length = message.length
            if (length > 160) {
                val messagelist: ArrayList<String> = manager.divideMessage(message)
                manager.sendMultipartTextMessage(phonenumber, null, messagelist, null, null)
            } else {
                manager.sendTextMessage(phonenumber, null, message, piSend, piDelivered)
            }
        }
        } catch (e: Exception) {
            Toast.makeText(this@AppointmentFragment.context, e.message.toString(), Toast.LENGTH_LONG)
                .show()
        }
    }
    private fun sendSms(phoneNumber: String, message: String, isBinary: Int) {
        try {
            val smsManager:SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(this@AppointmentFragment.context, "Message Sent", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this@AppointmentFragment.context, e.message.toString(), Toast.LENGTH_LONG)
                .show()
        }
    }
    private fun sendEmail(){
        appExecutors.diskIO().execute {
            val props = System.getProperties()
            props.put("mail.smtp.host", "smtp.gmail.com")
            props.put("mail.smtp.socketFactory.port", "465")
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.port", "465")

            val session =  Session.getInstance(props,
                object : javax.mail.Authenticator() {
                    //Authenticating the password
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(Credentials.EMAIL, Credentials.PASSWORD)
                    }
                })

            try {
                //Creating MimeMessage object
                val mm = MimeMessage(session)
                val emailId = "sathiavathy.raph@gmail.com"
                //Setting sender address
                mm.setFrom(InternetAddress("gs-telehealth.dev@gsoim.org"))
                //Adding receiver
                mm.addRecipient(
                    Message.RecipientType.TO,
                    InternetAddress(emailId))
                //Adding subject
                mm.subject = "testing"
                //Adding message
                mm.setText("Your mail body.")
                //Sending email
                Transport.send(mm)

                appExecutors.mainThread().execute {
                    //Something that should be executed on main thread.
                }

            } catch (e: MessagingException) {
                e.printStackTrace()
            }
        }
    }
}

class Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // TODO Auto-generated method stub
        val phoneNumberReceiver = "7893838158"
        val message = "blablabla"
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumberReceiver, null, message, null, null)
        Toast.makeText(context, "Alarm Triggered and SMS Sent", Toast.LENGTH_LONG)
    }
}



