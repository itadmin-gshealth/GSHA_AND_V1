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
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.ListView
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.omif.gsha.BuildConfig
import com.omif.gsha.MainActivity
import com.omif.gsha.model.EHRecord
import com.omif.gsha.model.Prescription
import com.omif.gsha.model.User
import com.omif.gsha.ui.appointment.AppointmentFragment
import com.omif.gsha.ui.messages.MessagesFragment
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
        private lateinit var mdbRef: DatabaseReference
        private lateinit var ddlDept: Spinner
        var selectedDept = ""
        var i = 0
        var dept = arrayOf(
            " ","Gynecology ", "Paediatrics", "Dentistry ", "General", "Dermatology ", "Psychiatrist"
        )
        private var pins = arrayOf(
            "500010","500011 ", "500012",
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

        fun showDialog(context:Context, message: String, identifier: Int) {
            val textView = TextView(context)
            textView.apply {
                text = "GSHA Says"
                setPadding(20, 30, 20, 30)
                textSize = 20f
                setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD;
            }

            val textView1 = TextView(context)
            textView1.text = "You will be notified when the Doctor comes online"
            textView1.setPadding(20, 30, 20, 30)
            textView1.textSize = 15f
            textView1.setBackgroundColor(Color.WHITE)
            textView1.setTextColor(Color.BLACK)
            textView1.typeface = Typeface.DEFAULT_BOLD;
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder
                .setView(textView1)
                .setCustomTitle(textView)
                .setPositiveButton("Ok") { dialog, which ->
                    sendEmail("Notify Doctor",message)
                }
                .setNegativeButton("CANCEL") { dialog, which ->
                   dialog.dismiss()
                }

            val params = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
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
            dialog.apply {
                getButton(DialogInterface.BUTTON_NEGATIVE).apply {
                    setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                    setTextColor(Color.WHITE)
                    typeface = Typeface.DEFAULT_BOLD;
                    layoutParams = params;
                }
            }
        }

        fun showDialog(context: Context, medicine: String) {
            val tableLayout = TableLayout(context)
            val tableRow = TableRow(context)
            val tableRowAdd = TableRow(context)

            val params = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
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

        fun updateDoctorStatus(status: String) {
            mdbRef = FirebaseDatabase.getInstance().reference
            val key = FirebaseAuth.getInstance().currentUser!!.uid
            val map: HashMap<String, Int> = HashMap<String, Int>()
            map["status"] = castStatus(status)
            mdbRef.child("tblDoctor").child(key).updateChildren(map as Map<String, Int>);
            mdbRef.child("tblUserWithType").child(key).updateChildren(map as Map<String, Int>);
        }

        fun updatePatientStatus(status: String) {
            mdbRef = FirebaseDatabase.getInstance().reference
            val key = FirebaseAuth.getInstance().currentUser!!.uid
            val map: HashMap<String, Int> = HashMap<String, Int>()
            map["status"] = castStatus(status)
            mdbRef.child("tblPatient").child(key).updateChildren(map as Map<String, Int>);
            mdbRef.child("tblUserWithType").child(key).updateChildren(map as Map<String, Int>);
        }

        private fun castStatus(status: String) : Int {
            return if(status == "Offline")
                0
            else if(status == "Available in 30 minutes")
                2
            else if(status == "Available tomorrow")
                3
            else if(status == "Available in an hour")
                4
            else
                1
        }

        fun getStatus(status: Int) : String {
            return if(status == 0)
                "Offline"
            else if(status == 2)
                "Available in 30 minutes"
            else if(status == 3)
                "Available tomorrow"
            else if(status == 4)
                "Available in an hour"
            else
                "Online"
        }

        fun showDialog(context: Context) {
            val params = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            params.setMargins(20, 0, 0, 0)

            val cw = ContextThemeWrapper(context, com.omif.gsha.R.style.AlertDialogTheme)
            val builderSingle = AlertDialog.Builder(context)
            val textView = TextView(context)
            textView.apply {
                text = "Select Status"
                setPadding(20, 30, 20, 30)
                textSize = 20f
                setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD;
            }
            builderSingle.setCustomTitle(textView)

            var status = ""
            val arrayAdapter = ArrayAdapter<String>(cw, R.layout.select_dialog_singlechoice)
            arrayAdapter.add("Online")
            arrayAdapter.add("Available in 30 minutes")
            arrayAdapter.add("Available tomorrow")
            arrayAdapter.add("Available in an hour")
            arrayAdapter.add("Offline")

            builderSingle.setNegativeButton(
                "cancel"
            ) { dialog, which -> dialog.dismiss() }
            builderSingle.setAdapter(
                arrayAdapter
            ) { dialog, which ->
                val strName = arrayAdapter.getItem(which)
                if (strName != null) {
                    status = strName
                }
                val builderInner = AlertDialog.Builder(context)
                builderInner.setMessage(strName)
                builderInner.setPositiveButton(
                    "Ok"
                ) { dialog, which -> updateDoctorStatus(status) }

                val textView = TextView(context)
                textView.apply {
                    text = "Change Status to"
                    setPadding(20, 30, 20, 30)
                    textSize = 20f
                    setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                    setTextColor(Color.WHITE)
                    typeface = Typeface.DEFAULT_BOLD;
                }
                builderInner.setCustomTitle(textView)
                val dialog: AlertDialog = builderInner.create()
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
            val dialog: AlertDialog = builderSingle.create()
            dialog.show()
            dialog.apply {
                getButton(DialogInterface.BUTTON_NEGATIVE).apply {
                    setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                    setTextColor(Color.WHITE)
                    typeface = Typeface.DEFAULT_BOLD;
                    layoutParams = params;
                }
            }
        }

        fun showServicesDialog(context: Context, parentFragmentManager: FragmentManager, customLayout: View, dept:String) {
            mAuth = FirebaseAuth.getInstance()
            val textView = TextView(context)
            textView.apply {
                text = dept
                setPadding(20, 30, 20, 30)
                textSize = 20f
                setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD;
            }
            val params = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            params.setMargins(20, 0, 0, 0)

            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder
                .setView(customLayout)
                .setCustomTitle(textView)
                .setPositiveButton("CONSULT NOW") { dialog, which ->
                    var fragment: Fragment? = null
                    fragment = MessagesFragment()
                    replaceFragment(fragment, parentFragmentManager)
                    //innerDialog(context, params, "Under construction")
                }
                .setNeutralButton("CLOSE") { dialog, which ->
                    dialog.dismiss()
                }
                .setNegativeButton("BOOK APPOINTMENT") { dialog, which ->
                    //innerDialog(context, params, "Under construction")
                    var fragment: Fragment? = null
                    fragment = AppointmentFragment()
                    replaceFragment(fragment, parentFragmentManager)
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
            dialog.apply {
                getButton(DialogInterface.BUTTON_POSITIVE).apply {
                    setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                    setTextColor(resources.getColor(com.omif.gsha.R.color.white))
                    typeface = Typeface.DEFAULT_BOLD;
                    layoutParams = params;
                    textSize = 11f
                }
            }
            dialog.apply {
                getButton(DialogInterface.BUTTON_NEGATIVE).apply {
                    setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                    setTextColor(resources.getColor(com.omif.gsha.R.color.white))
                    typeface = Typeface.DEFAULT_BOLD;
                    layoutParams = params;
                    textSize = 11f
                }
            }
            dialog.apply {
                getButton(DialogInterface.BUTTON_NEUTRAL).apply {
                    setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                    setTextColor(resources.getColor(com.omif.gsha.R.color.white))
                    typeface = Typeface.DEFAULT_BOLD;
                    layoutParams = params;
                    textSize = 11f
                }
            }
        }

        fun getLink(date: String, patientId: String) : String
        {
            var link = ""
            mAuth = FirebaseAuth.getInstance()
            mdbRef.child("tblEHR").child(patientId).child(date).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                        val ehr = snapshot.getValue(EHRecord::class.java)
                    if (ehr != null) {
                        link = ehr.link.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //Toast.makeText(context, error.message, Toast.LENGTH_LONG)
                }
            })
            return link
        }


        fun showPresDialog(context: Context, parentFragmentManager: FragmentManager, date: String?, name:String, age:Int, gender:String) {
            mAuth = FirebaseAuth.getInstance()
            val inflater = LayoutInflater.from(context)
            val customLayout: View = inflater.inflate(com.omif.gsha.R.layout.prescription_listview, null)
            val dept = customLayout.findViewById<TextView>(com.omif.gsha.R.id.dept)
            val docName = customLayout.findViewById<TextView>(com.omif.gsha.R.id.docName)
            val docRegNo = customLayout.findViewById<TextView>(com.omif.gsha.R.id.docRegNo)
            val regNo = customLayout.findViewById<TextView>(com.omif.gsha.R.id.regNo)
            val medicine = customLayout.findViewById<TextView>(com.omif.gsha.R.id.meds)
            val patientName = customLayout.findViewById<TextView>(com.omif.gsha.R.id.patientName)
            val patientAge = customLayout.findViewById<TextView>(com.omif.gsha.R.id.patientAge)
            val patientGender = customLayout.findViewById<TextView>(com.omif.gsha.R.id.patientGender)

            mdbRef.child("tblPrescription").child(mAuth.currentUser?.uid.toString()).child(date.toString()).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                        val prescription = snapshot.getValue(Prescription::class.java)
                        dept.text = prescription?.department.toString()
                        docName.text = prescription?.doctorName.toString()
                        docRegNo.text = prescription?.doctorRegNo.toString()
                        regNo.text = "414/DM&HO/MED/2008"
                        medicine.text = prescription?.medicine.toString()
                        patientAge.text = age.toString() + " years"
                        patientName.text = name
                        patientGender.text = gender
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                }
            })

            val textView = TextView(context)
            textView.apply {
                text = "Prescription"
                setPadding(20, 30, 20, 30)
                textSize = 20f
                setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD;
            }
            val params = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            params.setMargins(20, 0, 0, 0)

            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder
                .setView(customLayout)
                .setCustomTitle(textView)
                .setPositiveButton("DOWNLOAD") { dialog, which ->
                    innerDialog(context, params, "Under construction")
                }
                .setNeutralButton("CLOSE") { dialog, which ->
                    dialog.dismiss()
                }
                .setNegativeButton("E-PHARMACY") { dialog, which ->
                    innerDialog(context, params, "Under construction")
                }

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
            dialog.apply {
                getButton(DialogInterface.BUTTON_NEGATIVE).apply {
                    setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                    setTextColor(Color.WHITE)
                    typeface = Typeface.DEFAULT_BOLD;
                    layoutParams = params;
                }
            }
            dialog.apply {
                getButton(DialogInterface.BUTTON_NEUTRAL).apply {
                    setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                    setTextColor(Color.WHITE)
                    typeface = Typeface.DEFAULT_BOLD;
                    layoutParams = params;
                }
            }
        }

        private fun innerDialog(context: Context, params:LayoutParams, message:String)
        {
            val builderInner = AlertDialog.Builder(context)
            builderInner.setMessage(message)
            builderInner.setPositiveButton(
                "Ok"
            ) { dialog, which -> dialog.dismiss() }

            val textView = TextView(context)
            textView.apply {
                text = "GSHA Says"
                setPadding(20, 30, 20, 30)
                textSize = 20f
                setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD;
            }
            builderInner.setCustomTitle(textView)
            val dialog: AlertDialog = builderInner.create()
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

        fun replaceFragment(someFragment: Fragment?, fragmentManager: FragmentManager) {
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            if (someFragment != null) {
                transaction.replace(com.omif.gsha.R.id.nav_host_fragment_activity_main, someFragment)
            }
            transaction.setReorderingAllowed(true)
            transaction.commit()
        }
        fun getChildren(context: Context):ArrayList<User>
        {
            val preferences = context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
            val parentId = preferences?.getString("uParentId","")

            mAuth = FirebaseAuth.getInstance()
            val userList = ArrayList<User>()
            mdbRef.child("tblPatient").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val currentUser = postSnapshot.getValue(User::class.java)
                        if ((mAuth.currentUser?.uid.toString() == currentUser?.parentId.toString() || currentUser?.parentId.toString() == parentId || mAuth.currentUser?.uid.toString() == parentId) && currentUser?.name.toString() != "admin") {
                            userList.add(currentUser!!)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                }
            })
            return userList
        }

        fun getParent(context: Context):ArrayList<User>
        {
            val preferences = context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
            val parentId = preferences?.getString("uParentId","")

            mAuth = FirebaseAuth.getInstance()
            val userList = ArrayList<User>()
            mdbRef.child("tblPatient").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val currentUser = postSnapshot.getValue(User::class.java)
                        if (currentUser?.uid.toString() == parentId && currentUser?.name.toString() != "admin") {
                            userList.add(currentUser!!)
                        }
                    }
                    var editor = preferences?.edit()
                    editor?.putString("parentEmail",userList[0].email)
                    editor?.commit()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                }
            })
            return userList
        }
        fun showDependantDialog(context: Context, nameList: List<String>, ageList:List<Int>, imageUrlList:List<String>, uidList:List<String>) {
            val params = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            params.setMargins(20, 0, 0, 0)

            val builderSingle = AlertDialog.Builder(context)
            val lstView = ListView(context)

            val arrayList: ArrayList<dependantView> = ArrayList<dependantView>()
            mdbRef = FirebaseDatabase.getInstance().reference

            for (i in 0 until nameList.size) {
                arrayList.add(dependantView(com.omif.gsha.R.drawable.avatar, nameList[i], ageList[i].toString()+" years"))
            }

            val numbersArrayAdapter = dependantViewAdapter(context, arrayList)
            lstView.adapter = numbersArrayAdapter

            val textView = TextView(context)
            textView.apply {
                text = "Select dependant"
                setPadding(20, 30, 20, 30)
                textSize = 20f
                setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD;
            }
            builderSingle.setCustomTitle(textView)

            var status = ""
            var email = ""

            builderSingle.setNegativeButton(
                "cancel"
            ) { dialog, which -> dialog.dismiss() }
            builderSingle.setAdapter(
                numbersArrayAdapter
            ) { dialog, which ->
                val strName = numbersArrayAdapter.getItem(which)
                val preferences = context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
                var parentEmail = preferences?.getString("parentEmail","")
                var type = preferences?.getInt("uType",0)

                if (strName != null) {
                    status = strName.numberInDigit.toString()
                }
                val nameList: List<String> = status.split(" ")
                if(type == 1){parentEmail = nameList[0]+"@gsha.com"}
                val builderInner = AlertDialog.Builder(context)
                builderInner.setMessage(status)
                builderInner.setPositiveButton(
                    "Ok"
                ) { dialog, which ->
                    if (parentEmail != null) {
                        mAuth.signInWithEmailAndPassword(parentEmail.lowercase(), nameList[0].lowercase()+"123").addOnCompleteListener { task->
                            if(task.isSuccessful){
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                            } else{
                                Toast.makeText(context, "Error logging-in, Contact system administrator", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                val textView = TextView(context)
                textView.apply {
                    text = "Sign-In as : "
                    setPadding(20, 30, 20, 30)
                    textSize = 20f
                    setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                    setTextColor(Color.WHITE)
                    typeface = Typeface.DEFAULT_BOLD;
                }
                builderInner.setCustomTitle(textView)
                val dialog: AlertDialog = builderInner.create()
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
            val dialog: AlertDialog = builderSingle.create()
            dialog.show()
            dialog.apply {
                getButton(DialogInterface.BUTTON_NEGATIVE).apply {
                    setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                    setTextColor(Color.WHITE)
                    typeface = Typeface.DEFAULT_BOLD;
                    layoutParams = params;
                }
            }
        }

    }
}