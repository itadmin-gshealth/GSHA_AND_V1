package com.omif.gsha.adapter

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.createChooser
import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.omif.gsha.BuildConfig
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class CommonMethods(val context: Context){

    private val pickFromGallery:Int = 101
    fun openFolder() {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra("return-data", true)
        context.startActivity(createChooser(intent, "Complete action using"))
    }

    companion object {
        private lateinit var appExecutors: AppExecutors
        private lateinit var mAuth: FirebaseAuth
        var i = 0
        private var pins = arrayOf(
            "500010","500011 ", "500012"
        )
        fun sendEmail(subject: String, message: String) {
            mAuth = FirebaseAuth.getInstance()
            appExecutors = AppExecutors()
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
                    val emailIdTo = BuildConfig.mailAppointmentTo
                    //Setting sender address
                    mm.setFrom(InternetAddress(BuildConfig.mailAppointmentFrom))
                    //Adding receiver
                    mm.addRecipient(
                        Message.RecipientType.TO,
                        InternetAddress(emailIdTo)
                    )
                    mm.subject = subject
                    mm.setText(message)
                    Transport.send(mm)
                    appExecutors.mainThread().execute {
                        //Something that should be executed on main thread.
                    }

                } catch (e: MessagingException) {
                    e.printStackTrace()
                }
            }
        }

        fun showDialog(context: Context, medicine: String) {
            val tableLayout = TableLayout(context)
            val tableRow = TableRow(context)
            val tableRowAdd = TableRow(context)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val txtAddress = TextView(context)
            txtAddress.apply {
                setPadding(20, 30, 20, 30)
                text="ADDRESS "
                textSize = 15f
                setBackgroundColor(Color.WHITE)
                setTextColor(Color.BLACK)
                typeface = Typeface.DEFAULT_BOLD;
            }
            tableRowAdd.addView(txtAddress)
            val textViewAddValue = EditText(context)
            textViewAddValue.apply {
                setPadding(20, 30, 20, 30)
                width = 500
                height = 700
                textSize = 15f
                setBackgroundColor(Color.WHITE)
                setBackgroundResource(R.drawable.edit_text)
                setTextColor(Color.BLACK)
                typeface = Typeface.DEFAULT_BOLD;
            }
            tableRowAdd.addView(textViewAddValue)

            val textViewPin = EditText(context)
            textViewPin.apply {
                setPadding(20, 30, 20, 30)
                width = 250
                textSize = 15f
                setBackgroundColor(Color.CYAN)
                setBackgroundResource(R.drawable.edit_text)
                setTextColor(Color.BLACK)
                typeface = Typeface.DEFAULT_BOLD;
                inputType = InputType.TYPE_CLASS_NUMBER
            }

            tableRow.addView(textViewPin)
            val btnCheckPin = Button(context)

            btnCheckPin.apply {
                text = "Check Delivery"
                height = 20
                width = 10
                setPadding(20, 30, 20, 30)
                setBackgroundColor(resources.getColor(com.omif.gsha.R.color.light_blue_200))
                setTextColor(Color.BLACK)
                typeface = Typeface.DEFAULT_BOLD;
                setOnClickListener{
                    val address = textViewAddValue.text.toString()
                    val pin = textViewPin.text.toString()
                    checkPin(pin,address, context)
                }
            }
            tableRow.addView(btnCheckPin)
            tableLayout.addView(tableRowAdd)
            tableLayout.addView(tableRow)

            tableLayout.apply {
                    setPadding(220, 50, 20, 30)
                }

            val textView = TextView(context)
            textView.apply {
                text = "Enter your Address"
                setPadding(20, 30, 20, 30)
                textSize = 20f
                setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD;
            }

            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder
                .setView(tableLayout)
                .setCustomTitle(textView)
                .setPositiveButton("OK") { dialog, which ->
                    if(i == 1) {
                        sendEmail("Medicine Ordered", medicine)
                    }
                    else
                    {
                        Toast.makeText(context, "Error in ordering medicine. Try again later", Toast.LENGTH_SHORT).show()
                    }
                    i=0
                }

            params.setMargins(20, 0, 0, 0)

            val dialog: AlertDialog = builder.create()
            dialog.show()
            dialog.apply {
                getButton(DialogInterface.BUTTON_POSITIVE).apply {
                    setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                    setTextColor(Color.WHITE)
                    typeface = Typeface.DEFAULT_BOLD;
                    layoutParams = params;
                }
            }
        }

        private fun checkAllFields(pin: String, address: String, context: Context): Boolean {
            if (address.length === 0) {
                Toast.makeText(context, "Enter the Address", Toast.LENGTH_SHORT).show()
                return false
            }
            if(pin.isNullOrBlank())
            {
                Toast.makeText(context, "Enter the PinCode", Toast.LENGTH_SHORT).show()
                return false
            }
            i = 1
            return true
        }

       private fun checkPin(pin: String,address: String, context: Context) {
           if(checkAllFields(pin,address, context))
           {
               if(pins.contains(pin))
               {
                   Toast.makeText(context, "Delivery is available to your location. Please click Ok.", Toast.LENGTH_SHORT).show()
               }
               else
               {
                   Toast.makeText(context, "Delivery is not available to your location", Toast.LENGTH_SHORT).show()
               }
           }
       }
    }


}