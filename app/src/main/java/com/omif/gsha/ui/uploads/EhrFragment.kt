package com.omif.gsha.ui.uploads

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.omif.gsha.R
import com.omif.gsha.adapter.CommonMethods
import com.omif.gsha.databinding.FragmentEhrBinding
import com.omif.gsha.databinding.FragmentPharmaBinding

class EhrFragment : Fragment() {
    private val pickFromGallery:Int = 101
    private var _binding: FragmentEhrBinding? = null
    private lateinit var btnAttachFiles: Button
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ehrViewModel =
            ViewModelProvider(this).get(EhrViewModel::class.java)

        _binding = FragmentEhrBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val preferences =
            activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val uType = preferences?.getInt("uType",0)

        btnAttachFiles = binding.btnAddFiles
        if(uType == 2)
        {
            btnAttachFiles.isVisible = false
        }
        btnAttachFiles.setOnClickListener {
            this.context?.let { it1 -> CommonMethods(it1).openFolder() }
            //openFolder()
        }
        // Inflate the layout for this fragment
        return root
    }

    //private fun openFolder() {
       // val intent = Intent()
       // intent.type = "*/*"
       // intent.action = Intent.ACTION_GET_CONTENT
        //intent.putExtra("return-data", true)
        //startActivityForResult(Intent.createChooser(intent, "Complete action using"), pickFromGallery)
    //}
}