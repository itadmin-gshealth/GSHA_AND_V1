package com.omif.gsha.ui.members

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.omif.gsha.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.omif.gsha.databinding.FragmentAddmembersBinding


class AddMembersFragment : Fragment() {

    private var _binding: FragmentAddmembersBinding? = null
    private lateinit var mAuth: FirebaseAuth

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var btnSignOut: Button
    private lateinit var txtMessage: TextView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val addMembersViewModel =
            ViewModelProvider(this).get(AddMembersViewModel::class.java)

        _binding = FragmentAddmembersBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}