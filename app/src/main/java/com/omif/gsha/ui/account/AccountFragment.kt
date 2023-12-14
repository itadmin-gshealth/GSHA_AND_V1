package com.omif.gsha.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.omif.gsha.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.omif.gsha.ui.members.AddMembersFragment


class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private lateinit var mAuth: FirebaseAuth

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var btnSignOut: Button
    private lateinit var btnAddMem: Button
    private lateinit var txtMessage: TextView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val accountViewModel =
            ViewModelProvider(this).get(AccountViewModel::class.java)

        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAccount
        accountViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        btnSignOut = binding.SignOut
        btnAddMem = binding.AddMembers
        mAuth = FirebaseAuth.getInstance()

        btnSignOut.setOnClickListener{
            mAuth.signOut()
        }
        btnAddMem.setOnClickListener{
            var fragment: Fragment? = null
            fragment = AddMembersFragment()
            replaceFragment(fragment)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun replaceFragment(someFragment: Fragment?) {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        if (someFragment != null) {
            transaction.replace(com.omif.gsha.R.id.nav_host_fragment_activity_main, someFragment)
        }
        transaction.commit()
    }
}