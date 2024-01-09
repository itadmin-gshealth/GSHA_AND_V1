package com.omif.gsha.ui.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.omif.gsha.MainActivity
import com.omif.gsha.model.User
import com.omif.gsha.databinding.FragmentSignupBinding


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
        mAuth = FirebaseAuth.getInstance()

        btnSignUp.setOnClickListener(){
            if(CheckAllFields()) {
                signUp(txtName.text.toString(),txtEmail.text.toString(), txtPassword.text.toString(), txtPhoneNumber.text.toString(), 0)
            }
        }

        btnSignUpDoctor.visibility = View.VISIBLE

        btnSignUpDoctor.setOnClickListener() {
            if (CheckAllFields()) {
                signUp(
                    txtName.text.toString(),
                    txtEmail.text.toString(),
                    txtPassword.text.toString(),
                    txtPhoneNumber.text.toString(),
                    2
                )
            }
        }

        return root
    }

    private fun signUp(name: String, email:String, password: String, phoneNumber: String, uType: Int)
    {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task->
            if(task.isSuccessful) {
                mAuth.uid?.let { addtoDatabase(name, email, it, phoneNumber, uType) }
                Toast.makeText(this@SignUpFragment.context, "User Created Successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@SignUpFragment.context, MainActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this@SignUpFragment.context, "Error Creating User, Contact system administrator", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addtoDatabase(name: String, email: String, uid: String, phoneNumber: String, uType: Int) {
        mdbRef = FirebaseDatabase.getInstance().reference
        mdbRef.child("userWithType").child(uid).setValue(User(name, email, uid,phoneNumber, uType))
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
    }
        // after all validation return true.
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}