package com.omif.gsha.ui.uploads

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.text.method.ScrollingMovementMethod
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
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.omif.gsha.adapter.CommonMethods
import com.omif.gsha.databinding.FragmentPrescriptionPatientBinding
import com.omif.gsha.ui.pharma.PharmaFragment

class PrescriptionPatientFragment : Fragment() {

    private lateinit var txtMeds: TextView
    private lateinit var txtRegNo: TextView
    private lateinit var txtDept: TextView
    private lateinit var btnGetMedicine: Button
    private lateinit var btnDownload: Button
    private var _binding: FragmentPrescriptionPatientBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PrescriptionPatientViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val prescriptionPatientViewModel =
            ViewModelProvider(this).get(PrescriptionPatientViewModel::class.java)

        val preferences = activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

        _binding = FragmentPrescriptionPatientBinding.inflate(inflater, container, false)
        txtMeds = binding.meds
        txtRegNo = binding.regNo
        txtDept = binding.dept
        btnGetMedicine = binding.btnGetMedicine
        btnDownload = binding.btnDownload
        btnDownload.setOnClickListener{
            Toast.makeText(this@PrescriptionPatientFragment.context, "Download complete",Toast.LENGTH_SHORT).show()
        }
        btnGetMedicine.setOnClickListener{
            this@PrescriptionPatientFragment.context?.let { it1 ->
                CommonMethods.showDialog(
                    it1, txtMeds.text.toString()
                )
            }
        }

        txtMeds.text = ""

        txtDept.text = preferences?.getString("dept", "").toString()
        txtMeds.movementMethod = ScrollingMovementMethod()
        txtRegNo.text ="414/DM&HO/MED/2008"
        val root: View = binding.root
        return root
    }



}