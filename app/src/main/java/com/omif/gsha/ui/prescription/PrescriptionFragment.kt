package com.omif.gsha.ui.prescription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.omif.gsha.databinding.FragmentPrescriptionBinding


class PrescriptionFragment : Fragment() {

    private var _binding: FragmentPrescriptionBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PrescriptionViewModel
    private lateinit var btnAppointment: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val prescriptionViewModel =
            ViewModelProvider(this).get(PrescriptionViewModel::class.java)

        _binding = FragmentPrescriptionBinding.inflate(inflater, container, false)
        val root: View = binding.root
        btnAppointment = binding.btnPrescription
        btnAppointment.setOnClickListener(View.OnClickListener {
            this.onClick()
        })
        return root   }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PrescriptionViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun onClick() {

    }
}



