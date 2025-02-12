package com.omif.gsha.ui.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.view.marginLeft
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.omif.gsha.MainActivity
import com.omif.gsha.adapter.CommonMethods
import com.omif.gsha.adapter.ExpandableAdapter
import com.omif.gsha.databinding.FragmentAccountBinding
import com.omif.gsha.databinding.FragmentSigninBinding
import com.omif.gsha.model.ExpandableAccount
import com.omif.gsha.model.Prescription
import com.omif.gsha.model.User
import com.omif.gsha.ui.account.AccountViewModel
import com.omif.gsha.ui.signup.SignUpFragment


class SignInFragment : Fragment() {

    private var _bindingSignIn: FragmentSigninBinding? = null
    private var _bindingAcc: FragmentAccountBinding? = null

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val bindingSignIn get() = _bindingSignIn!!
    private val bindingAcc get() = _bindingAcc!!

    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var btnLogIn: Button
    private lateinit var btnSignUp: Button
    private lateinit var btnAddMem: Button
    private lateinit var btnChangeStatus: Button
    private lateinit var btnSignOut: Button
    private lateinit var btnSwitchUser: Button
    private lateinit var txtPhone: TextView
    private lateinit var btnSignUpDoctor: Button
    private lateinit var btnSignUpHW: Button

    var expandableListViewExample: ExpandableListView? = null
    var expandableListAdapter: ExpandableListAdapter? = null
    var expandableTitleList: List<String>? = null
    var expandableDetailList: HashMap<String, List<String>>? = null


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
            val preferences = activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

            val name = preferences?.getString("uName","")
            val type = preferences?.getInt("uType",0)
            val internal = preferences?.getInt("uInternal",0)
            val email = preferences?.getString("uEmail","")
            val phone = preferences?.getString("uPhone","")

            val textView: TextView = bindingAcc.textAccount
            accountViewModel.text.observe(viewLifecycleOwner) {
                textView.text = it + name
            }

            txtPhone = bindingAcc.textPhone
            txtPhone.text  = phone + " | " + email
            btnSignOut = bindingAcc.SignOut
            btnChangeStatus = bindingAcc.changeStatus
            btnSwitchUser= bindingAcc.SwitchUser
            btnAddMem = bindingAcc.AddMembers
            btnSignUpDoctor = bindingAcc.SignUpDoctor
            btnSignUpHW = bindingAcc.SignUpHealth
            btnSignUpHW.isVisible = false
            btnSignUpDoctor.isVisible = false


            if(name =="admin")
            {
                btnAddMem.isVisible = false
                btnChangeStatus.isVisible = false
                btnSwitchUser.isVisible = false
                btnSignUpHW.isVisible = true
                btnSignUpDoctor.isVisible = true

            }
            else if(type == 2)
            {
                btnAddMem.isVisible = false
                btnChangeStatus.isVisible = true
                btnSwitchUser.isVisible = false
                val param = btnSignOut.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(220,35,0,0)
                btnSignOut.layoutParams = param
            }
            else if(type == 3)
            {
                btnAddMem.isVisible = false
                btnChangeStatus.isVisible = false
                btnSwitchUser.isVisible = true
                val param = btnSignOut.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(240,35,0,0)
                btnSignOut.layoutParams = param
            }
            else
            {
                btnChangeStatus.isVisible = false
            }
            btnAddMem.setOnClickListener{
                var editor = preferences?.edit()
                editor?.putInt("memberType",3)
                editor?.putBoolean("isMember",true)
                if(type == 1 && internal == 0)
                    editor?.putInt("memberInternal",0)
                else if(type == 1 && internal == 1)
                    editor?.putInt("memberInternal",1)
                editor?.commit()

                var fragment: Fragment? = null
                fragment = SignUpFragment()
                replaceFragment(fragment)
            }

            btnSignUpHW.setOnClickListener{
                var editor = preferences?.edit()
                editor?.putInt("memberType",1)
                editor?.putInt("memberInternal",0)

                editor?.commit()

                var fragment: Fragment? = null
                fragment = SignUpFragment()
                replaceFragment(fragment)
            }

            btnSignUpDoctor.setOnClickListener{
                var editor = preferences?.edit()
                editor?.putInt("memberType",2)
                editor?.commit()

                var fragment: Fragment? = null
                fragment = SignUpFragment()
                replaceFragment(fragment)
            }

            btnSignOut.setOnClickListener{
                var editor = preferences?.edit()
                editor?.clear()
                editor?.apply()
                mAuth.signOut()
                val intent = Intent(this@SignInFragment.context, MainActivity::class.java)
                startActivity(intent)
            }

            val nameList = ArrayList<String>()
            val ageList = ArrayList<Int>()
            val imageUrlList = ArrayList<String>()
            val uidList = ArrayList<String>()
            val userList = if(type == 1 && internal == 1){
                this@SignInFragment.context?.let { CommonMethods.getChildren(it) }

            }else if(type == 3) {
                this@SignInFragment.context?.let { CommonMethods.getParent(it) }
            }
            else{
                this@SignInFragment.context?.let { CommonMethods.getChildren(it) }
            }

            btnSwitchUser.setOnClickListener{
                if (userList != null && userList.size >0) {
                   for(i in 0 until userList.size)
                   {
                       if(!nameList.contains(userList[i].name)) {
                           userList[i].name?.let { it1 -> nameList.add(it1) }
                           userList[i].age?.let { it1 -> ageList.add(it1) }
                           userList[i].imageLink?.let { it1 -> imageUrlList.add(it1) }
                           userList[i].uid?.let { it1 -> uidList.add(it1) }
                       }
                   }
                }
                else
                {

                }
                this@SignInFragment.context?.let { it1 -> CommonMethods.showDependantDialog(it1, nameList, ageList, imageUrlList, uidList) }
            }

            btnChangeStatus.setOnClickListener {
                this@SignInFragment.context?.let { it1 -> CommonMethods.showDialog(it1) }
            }

            expandableListViewExample = bindingAcc.expandableListViewSample

            expandableDetailList = ExpandableAccount.getData()
            expandableTitleList = ArrayList<String>(expandableDetailList?.keys)
            expandableListAdapter =
                root.context?.let { expandableTitleList?.let { it1 ->
                    expandableDetailList?.let { it2 ->
                        ExpandableAdapter(it,
                            it1, it2
                        )
                    }
                } }
            expandableListViewExample!!.setAdapter(expandableListAdapter)

            expandableListViewExample!!.setOnGroupExpandListener(ExpandableListView.OnGroupExpandListener { groupPosition ->
                Toast.makeText(
                    root.context,
                    expandableTitleList?.get(groupPosition) + " List Expanded.",
                    Toast.LENGTH_SHORT
                ).show()
            })

            expandableListViewExample!!.setOnGroupCollapseListener(ExpandableListView.OnGroupCollapseListener { groupPosition ->
                Toast.makeText(
                    root.context,
                    expandableTitleList?.get(groupPosition) + " List Collapsed.",
                    Toast.LENGTH_SHORT
                ).show()
            })

            expandableListViewExample!!.setOnChildClickListener(ExpandableListView.OnChildClickListener { parent, v, groupPosition, childPosition, id ->
                Toast.makeText(
                    root.context,
                    expandableTitleList?.get(groupPosition)
                            + " -> "
                            + expandableTitleList?.let {
                        expandableDetailList?.get(
                            it[groupPosition]
                        )?.get(
                            childPosition
                        )
                    },
                    Toast.LENGTH_SHORT
                ).show()
                false
            })

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