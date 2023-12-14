package com.omif.gsha.ui.signin

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
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.omif.gsha.MainActivity
import com.omif.gsha.databinding.FragmentAccountBinding
import com.omif.gsha.databinding.FragmentSigninBinding
import com.omif.gsha.ui.account.AccountViewModel
import com.omif.gsha.ui.signup.SignUpFragment


class SignInFragment : Fragment() {

    private var _bindingSignIn: FragmentSigninBinding? = null
    private var _bindingAcc: FragmentAccountBinding? = null

    private lateinit var mAuth: FirebaseAuth


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val bindingSignIn get() = _bindingSignIn!!
    private val bindingAcc get() = _bindingAcc!!


    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var btnLogIn: Button
    private lateinit var btnSignUp: Button

    private lateinit var btnSignOut: Button


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        mAuth = FirebaseAuth.getInstance()
        if(mAuth.currentUser == null)
        {
            val signInViewModel =
                ViewModelProvider(this).get(SignInViewModel::class.java)

            _bindingSignIn = FragmentSigninBinding.inflate(inflater, container, false)
            val root: View = bindingSignIn.root

            val textView: TextView = bindingSignIn.textAccount
            signInViewModel.text.observe(viewLifecycleOwner) {
                textView.text = it
            }

            txtEmail=bindingSignIn.textEmail
            txtPassword = bindingSignIn.textPassword
            btnLogIn = bindingSignIn.SignIn
            btnSignUp = bindingSignIn.SignUp
            mAuth = FirebaseAuth.getInstance()

            btnLogIn.setOnClickListener{
                if(CheckAllFields()) {
                    login(txtEmail.text.toString(), txtPassword.text.toString())
                }
            }

            btnSignUp.setOnClickListener{
                var fragment: Fragment? = null
                fragment = SignUpFragment()
                replaceFragment(fragment)
            }

            return root

        }
        else
        {
            val accountViewModel =
                ViewModelProvider(this).get(AccountViewModel::class.java)
            _bindingAcc = FragmentAccountBinding.inflate(inflater, container, false)

            val root: View = bindingAcc.root

            val textView: TextView = bindingAcc.textAccount
            accountViewModel.text.observe(viewLifecycleOwner) {
                textView.text = it
            }

            btnSignOut = bindingAcc.SignOut

            btnSignOut.setOnClickListener{
                mAuth.signOut()
                val intent = Intent(this@SignInFragment.context, MainActivity::class.java)
                startActivity(intent)
            }

            return root
        }
    }

    private fun login(email: String, password: String)
    {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task->
            if(task.isSuccessful){
                val intent = Intent(this@SignInFragment.context, MainActivity::class.java)
                startActivity(intent)
            }
                else{
                Toast.makeText(this@SignInFragment.context, "Error logging-in, Contact system administrator", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun CheckAllFields(): Boolean {
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
        }
        // after all validation return true.
        return true
    }

    private fun replaceFragment(someFragment: Fragment?) {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        if (someFragment != null) {
            transaction.replace(com.omif.gsha.R.id.nav_host_fragment_activity_main, someFragment)
        }
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindingAcc = null
        _bindingSignIn = null
    }
}