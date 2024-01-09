package com.omif.gsha.ui.pharma

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.omif.gsha.adapter.CommonMethods
import com.omif.gsha.databinding.FragmentPharmaBinding


class PharmaFragment : Fragment() {

    private var _binding: FragmentPharmaBinding? = null
    private val binding get() = _binding!!

    private lateinit var hplLocator: TextView
    private lateinit var btnBrowse: Button
    private val pickFromGallery:Int = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val pharmaViewModel =
            ViewModelProvider(this).get(PharmaViewModel::class.java)

        _binding = FragmentPharmaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        hplLocator = binding.textPharmaLocator
        val content = SpannableString("Pharmacy Locator")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        hplLocator.text = content

       btnBrowse = binding.btnBrowse
        btnBrowse.setOnClickListener {
            this.context?.let { it1 -> CommonMethods(it1).openFolder() }
        }
        hplLocator.setOnClickListener{
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=17.506899546473374, 78.48131842900385")
            )
            startActivity(intent)
        }
        // Inflate the layout for this fragment
        return root
    }

    private fun sendEmail()
    {

    }


}