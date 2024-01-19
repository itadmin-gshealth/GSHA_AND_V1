package com.omif.gsha.ui.uploads

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.omif.gsha.databinding.FragmentVitalsBinding
import com.omif.gsha.model.Vitals
import java.text.SimpleDateFormat
import java.util.Date

class VitalsFragment : Fragment() {
    private var _binding: FragmentVitalsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val vitalsViewModel =
            ViewModelProvider(this).get(VitalsViewModel::class.java)

        _binding = FragmentVitalsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val txtWeightValue: TextView = binding.txtWeightValue
        val txtHeightValue: TextView = binding.txtHeightValue
        val txtbpSysValue: TextView = binding.txtbpSysValue
        val txtbpDiaValue: TextView = binding.txtbpDiaValue
        val txtTempValue: TextView = binding.txtTempValue
        val txtSugarValue: TextView = binding.txtSugarValue
        val txtPulseRateValue: TextView = binding.txtPulseRateValue
        val txtSPO2Value: TextView = binding.txtSPO2Value
        var uName  = ""
        var uType : Int = 0


        val btnSave: Button = binding.btnSaveVitals

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        if (mAuth.currentUser != null) {
            val preferences =
                activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
            uName = preferences?.getString("uName", "").toString()
            uType = preferences?.getInt("uType",0)!!
        }

        val textView: TextView = binding.txtVitals
        vitalsViewModel.text.observe(viewLifecycleOwner) {
                textView.text = it
        }

        btnSave.setOnClickListener{
            val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            var weight = if(txtWeightValue.text.toString().toDoubleOrNull() == null) 0.0 else txtWeightValue.text.toString().toDoubleOrNull()
            var height = if(txtHeightValue.text.toString().toDoubleOrNull() == null) 0.0 else txtHeightValue.text.toString().toDoubleOrNull()
            var bpDia = if(txtbpDiaValue.text.toString().toIntOrNull() == null) 0 else txtbpDiaValue.text.toString().toIntOrNull()
            var bpSys = if(txtbpSysValue.text.toString().toIntOrNull() == null) 0 else txtbpSysValue.text.toString().toIntOrNull()
            var temp = if(txtTempValue.text.toString().toIntOrNull() == null) 0 else txtTempValue.text.toString().toIntOrNull()
            var sugar = if(txtSugarValue.text.toString().toIntOrNull() == null) 0 else txtSugarValue.text.toString().toIntOrNull()
            var pulseRate = if(txtPulseRateValue.text.toString().toIntOrNull() == null) 0 else txtPulseRateValue.text.toString().toIntOrNull()
            var spO2 = if(txtSPO2Value.text.toString().toIntOrNull() == null) 0 else txtSPO2Value.text.toString().toIntOrNull()

            val messageObject = Vitals(
                weight,
                height,
                bpDia,
                bpSys,
                temp,
                sugar,
                pulseRate,
                spO2,
                mAuth.currentUser!!.uid.toString(),
                formatter.format(Date())
            )

            mDbRef.child("tblVitals").push()
                .setValue(messageObject)
            Toast.makeText(this@VitalsFragment.context, "Vitals saved Successfully", Toast.LENGTH_SHORT).show()

        }

        return root
    }
}