package com.omif.gsha.ui.signup

import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.omif.gsha.MainActivity
import com.omif.gsha.adapter.OnEmailCheckListener
import com.omif.gsha.databinding.FragmentSignupBinding
import com.omif.gsha.model.User
import com.squareup.picasso.Picasso
import kotlinx.coroutines.selects.select


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth
    private lateinit var storageRef: StorageReference
    private lateinit var mdbRef: DatabaseReference
    private lateinit var txtName: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var txtPhoneNumber: EditText
    private lateinit var btnAddImage: Button
    private lateinit var btnSignUp: Button
    private lateinit var btnSignUpDoctor: Button
    private lateinit var btnSignUpHW: Button
    private lateinit var ddlDept: Spinner
    private lateinit var ddlgender: Spinner
    private lateinit var txtAge: EditText
    private lateinit var userImg: de.hdodenhof.circleimageview.CircleImageView



    var isEmailRegistered = false
    var parentId = ""

    var dept = arrayOf(
        "Department","Gynecology ", "Paediatrics", "Dentistry ", "General", "Dermatology ", "Psychiatrist"
    )
    var selectedDept = ""
    var imageLink : String? = ""

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
        btnAddImage = binding.AddImage
        btnSignUp = binding.SignUp
        ddlgender = binding.ddlGender
        txtAge = binding.txtAge
        userImg = binding.userImg

        mAuth = FirebaseAuth.getInstance()
        storageRef = Firebase.storage.reference
        parentId = mAuth.currentUser?.uid.toString()

        btnSignUp.setOnClickListener(){
            if(CheckAllFields()) {
                val preferences = activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
                var memType = preferences?.getInt("memberType",0)
                var memInternal = preferences?.getInt("memberInternal",0)

                //for first time new outside patient
                if(memType == null)
                    memType = 1
                if(memInternal == null)
                    memInternal = 1

                if(memType == 2)
                {showDialog(memType, memInternal)}
                else{signUp(
                    txtName.text.toString(),
                    txtEmail.text.toString(),
                    txtPassword.text.toString(),
                    txtPhoneNumber.text.toString(),
                    ddlgender.selectedItem.toString(),
                    txtAge.text.toString().toInt(),
                    imageLink,
                    memType!!,
                    memInternal!!
                )}
                }
        }

       /* btnSignUpHW.setOnClickListener(){
            if(CheckAllFields()) {
                signUp(txtName.text.toString(),txtEmail.text.toString(), txtPassword.text.toString(), txtPhoneNumber.text.toString(),ddlgender.selectedItem.toString(),txtAge.text.toString().toInt(), imageLink, 1, 0)
            }
        }

        btnSignUpDoctor.visibility = View.VISIBLE

        btnSignUpDoctor.setOnClickListener() {
            if (CheckAllFields()) {
                showDialog()
            }
        }*/

        btnAddImage.setOnClickListener {
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            imagePickerActivityResult.launch(intent)
        }
        return root
    }

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                val imageUri: Uri? = result.data?.data
                val sd = this@SignUpFragment.context?.let { getFileName(it, imageUri!!) }
                val uploadTask = imageUri?.let { storageRef.child("user/$sd").putFile(it) }
                uploadTask?.addOnSuccessListener {
                    storageRef.child("user/$sd").downloadUrl.addOnSuccessListener {
                        Picasso.get().load(it.toString()).into(userImg)
                        imageLink = it.toString()
                    }.addOnFailureListener {
                        Toast.makeText(this@SignUpFragment.context, "Failed in downloading", Toast.LENGTH_SHORT).show();
                    }
                }?.addOnFailureListener {
                    Toast.makeText(this@SignUpFragment.context, "Failed in downloading", Toast.LENGTH_SHORT).show();
                    //Log.e("Firebase", "Image Upload fail")
                }
            }
        }

    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }

    private fun showDialog(memType:Int, memInternal:Int) {

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
                        imageLink,
                        memType!!,
                        memInternal!!
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


    private fun signUp(name: String, email:String, password: String, phoneNumber: String, gender: String, age:Int, imageLink:String?, uType: Int, internal: Int)
    {
        if(!mAuth.currentUser?.uid.toString().isNullOrBlank())
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task->
            if(task.isSuccessful) {
                mAuth.uid?.let { if(selectedDept == "" ){
                    addPatienttoDatabase(name, email, it, phoneNumber,"OutPatient", gender, age,"",imageLink, uType, parentId, internal)
                    addUsertoDatabase(name, email, it, phoneNumber,"OutPatient",gender, age,"",imageLink, uType, parentId, internal)
                    Toast.makeText(this@SignUpFragment.context, "User Created Successfully", Toast.LENGTH_SHORT).show()}
                else{ if(selectedDept == "Department"){
                    Toast.makeText(this@SignUpFragment.context, "Please select Department", Toast.LENGTH_SHORT).show();
                }
                else{
                    addDoctortoDatabase(name, email, it, phoneNumber, selectedDept, gender, age, "MBBS",imageLink, uType, internal)}}
                    addUsertoDatabase(name, email, it, phoneNumber, selectedDept, gender, age,"",imageLink, uType, parentId, internal)
                    Toast.makeText(this@SignUpFragment.context, "User Created Successfully", Toast.LENGTH_SHORT).show()}
                val intent = Intent(this@SignUpFragment.context, MainActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this@SignUpFragment.context, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addPatienttoDatabase(name: String, email: String, uid: String, phoneNumber: String,department:String, gender: String, age:Int, qual: String?, imageLink: String?, uType: Int, parentId: String?, internal: Int) {
        mdbRef = FirebaseDatabase.getInstance().reference
       /* if(parentId == "null")
            mdbRef.child("tblPatient").child(uid).setValue(User(name, email, uid,phoneNumber, department, gender, age, qual, imageLink, uType, "", internal))
        else*/
            mdbRef.child("tblPatient").child(uid).setValue(User(name, email, uid,phoneNumber, department, gender, age, qual, imageLink, uType, parentId,internal))
    }

    private fun addDoctortoDatabase(name: String, email: String, uid: String, phoneNumber: String, department: String, gender:String, age:Int, qual:String?, imageLink: String?, uType: Int, internal: Int) {
        mdbRef = FirebaseDatabase.getInstance().reference
        mdbRef.child("tblDoctor").child(uid).setValue(User(name, email, uid,phoneNumber, department, gender, age,qual, imageLink, uType, "", internal))
    }

    private fun addUsertoDatabase(name: String, email: String, uid: String, phoneNumber: String, department: String, gender:String, age:Int,qual:String?, imageLink: String?, uType: Int, parentId: String?, internal: Int) {
        mdbRef = FirebaseDatabase.getInstance().reference
        /*if(parentId=="null")
            mdbRef.child("tblUserWithType").child(uid).setValue(User(name, email, uid,phoneNumber, department, gender, age, qual, imageLink, uType, "", internal))
        else*/
            mdbRef.child("tblUserWithType").child(uid).setValue(User(name, email, uid,phoneNumber, department, gender, age, qual, imageLink, uType, parentId, internal))
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
        } else if (txtAge.text.toString().toIntOrNull()!! < 18 && mAuth.currentUser?.uid.toString().isNullOrBlank()) {
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