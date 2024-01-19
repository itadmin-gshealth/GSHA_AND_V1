package com.omif.gsha.ui.signup

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.omif.gsha.MainActivity
import com.omif.gsha.adapter.OnEmailCheckListener
import com.omif.gsha.databinding.FragmentSignupBinding
import com.omif.gsha.model.User
import kotlinx.coroutines.selects.select


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mdbRef: DatabaseReference
    private lateinit var txtName: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var txtPhoneNumber: EditText
    private lateinit var btnSignUp: Button
    private lateinit var btnSignUpDoctor: Button
    private lateinit var ddlDept: Spinner
    private lateinit var ddlgender: Spinner
    private lateinit var txtAge: EditText

    var isEmailRegistered = false

    var dept = arrayOf(
        "Department","Gynecology ", "Paediatrics", "Dentistry ", "General", "Dermatology ", "Psychiatrist"
    )
    var selectedDept = ""

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val signUpViewModel =
                ViewModelProvider(this).get(SignUpViewModel::class.java)

        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAccount
        signUpViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        txtName=binding.textName
        txtEmail=binding.textEmail
        txtPassword = binding.textPassword
        txtPhoneNumber = binding.textPhonenumber
        btnSignUp = binding.SignUp
        btnSignUpDoctor = binding.SignUpDoctor
        ddlgender = binding.ddlGender
        txtAge = binding.txtAge

        mAuth = FirebaseAuth.getInstance()

        btnSignUp.setOnClickListener(){
            if(CheckAllFields()) {
                signUp(txtName.text.toString(),txtEmail.text.toString(), txtPassword.text.toString(), txtPhoneNumber.text.toString(),ddlgender.selectedItem.toString(),txtAge.text.toString().toInt(), 1)
            }
        }

        btnSignUpDoctor.visibility = View.VISIBLE

        btnSignUpDoctor.setOnClickListener() {
            if (CheckAllFields()) {
                showDialog()
            }

        }
        return root
    }

    private fun showDialog() {

        ddlDept = Spinner(this@SignUpFragment.context)
        val adapter: ArrayAdapter<String>? = this@SignUpFragment.context?.let {
            ArrayAdapter<String>(
                it, R.layout.simple_spinner_item, dept
            )
        }
        adapter?.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        ddlDept.adapter = adapter

        val textView = TextView(context)
        textView.text = "Select Department"
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 20f
        textView.setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
        textView.setTextColor(Color.WHITE)
        textView.typeface = Typeface.DEFAULT_BOLD;
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setView(ddlDept)
            .setCustomTitle(textView)
            .setPositiveButton("Ok") { dialog, which ->
                selectedDept = ddlDept.selectedItem.toString()
                if(selectedDept == "Department")
                {
                    Toast.makeText(this@SignUpFragment.context, "Please select Department", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    signUp(
                        txtName.text.toString(),
                        txtEmail.text.toString(),
                        txtPassword.text.toString(),
                        txtPhoneNumber.text.toString(),
                        ddlgender.selectedItem.toString(),
                        txtAge.text.toString().toInt(),
                        2
                    )
                }
            }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
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
    }


    private fun signUp(name: String, email:String, password: String, phoneNumber: String, gender: String, age:Int, uType: Int)
    {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task->
            if(task.isSuccessful) {
                mAuth.uid?.let { if(selectedDept == "" ){
                    addPatienttoDatabase(name, email, it, phoneNumber,"OutPatient", gender, age,"", uType)
                    addUsertoDatabase(name, email, it, phoneNumber,"OutPatient",gender, age,"", uType)
                    Toast.makeText(this@SignUpFragment.context, "User Created Successfully", Toast.LENGTH_SHORT).show()}
                else{ if(selectedDept == "Department"){
                    Toast.makeText(this@SignUpFragment.context, "Please select Department", Toast.LENGTH_SHORT).show();
                }
                else{
                    addDoctortoDatabase(name, email, it, phoneNumber, selectedDept, gender, age, "MBBS", uType)}}
                    addUsertoDatabase(name, email, it, phoneNumber, selectedDept, gender, age,"",uType)
                    Toast.makeText(this@SignUpFragment.context, "User Created Successfully", Toast.LENGTH_SHORT).show()}
                val intent = Intent(this@SignUpFragment.context, MainActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this@SignUpFragment.context, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addPatienttoDatabase(name: String, email: String, uid: String, phoneNumber: String,department:String, gender: String, age:Int, qual: String?, uType: Int) {
        mdbRef = FirebaseDatabase.getInstance().reference
        mdbRef.child("tblPatient").child(uid).setValue(User(name, email, uid,phoneNumber, department, gender, age, qual, uType))
    }

    private fun addDoctortoDatabase(name: String, email: String, uid: String, phoneNumber: String, department: String, gender:String, age:Int, qual:String?, uType: Int) {
        mdbRef = FirebaseDatabase.getInstance().reference
        mdbRef.child("tblDoctor").child(uid).setValue(User(name, email, uid,phoneNumber, department, gender, age,qual, uType))
    }

    private fun addUsertoDatabase(name: String, email: String, uid: String, phoneNumber: String, department: String, gender:String, age:Int,qual:String?, uType: Int) {
        mdbRef = FirebaseDatabase.getInstance().reference
        mdbRef.child("tblUserWithType").child(uid).setValue(User(name, email, uid,phoneNumber, department, gender, age, qual, uType))
    }
    private fun CheckAllFields(): Boolean {

        if (txtName.text.length === 0) {
            txtName.error = "Email is required"
            return false
        }
        if (txtEmail.text.length === 0) {
            txtEmail.error = "Email is required"
            return false
        }
        if (txtPassword.text.length === 0) {
            txtPassword.error = "Password is required"
            return false
        } else if (txtPassword.text.length < 8) {
            txtPassword.error = "Password must be minimum 8 characters"
            return false
        } else if (txtPhoneNumber.text.length < 10) {
            txtPhoneNumber.error = "Phone number must be 10 digits"
        return false
        } else if (txtAge.text.length === 0) {
            txtAge.error = "Age is required"
            return false
        } else if (txtAge.text.toString().toIntOrNull()!! < 18) {
            txtAge.error = "Users below 18 years should be added as a dependant of an adult"
            return false
    }
        // after all validation return true.
        return true
    }

    private fun isEmailRegistered()
    {
        isCheckEmail(txtEmail.text.toString(), object : OnEmailCheckListener {
            override fun onSuccess(isRegistered: Boolean) {
                if(isRegistered)
                {
                    Toast.makeText(this@SignUpFragment.context, "Email found", Toast.LENGTH_SHORT).show()
                }
                else    {
                    Toast.makeText(this@SignUpFragment.context, "Email not found", Toast.LENGTH_SHORT).show()

                }
            }
        })
    }

    private fun isCheckEmail(email: String?, listener: OnEmailCheckListener) {
        if (email != null) {
            mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(OnCompleteListener<SignInMethodQueryResult> { task ->
                    val check: Boolean = !task.result.signInMethods?.isNotEmpty()!!
                    Toast.makeText(this@SignUpFragment.context, check.toString(), Toast.LENGTH_SHORT).show()
                    listener.onSuccess(check)

                })
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}