package com.omif.gsha.ui.uploads

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ExpandableListView.OnChildClickListener
import android.widget.ExpandableListView.OnGroupCollapseListener
import android.widget.ExpandableListView.OnGroupExpandListener
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.omif.gsha.adapter.CustomizedExpandableListAdapter
import com.omif.gsha.databinding.FragmentPrescriptionPatientBinding
import com.omif.gsha.model.ExpandableEhr
import com.omif.gsha.model.ExpandableListDataItems
import com.omif.gsha.model.ExpandablePrescription


class PrescriptionPatientFragment : Fragment() {

    private lateinit var txtMeds: TextView
    private lateinit var txtRegNo: TextView
    private lateinit var txtDept: TextView
    private lateinit var btnGetMedicine: Button
    private lateinit var btnDownload: Button
    private var _binding: FragmentPrescriptionPatientBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PrescriptionPatientViewModel

    var expandableListViewExample: ExpandableListView? = null
    var expandableListAdapter: ExpandableListAdapter? = null
    var expandableTitleList: List<String>? = null
    var expandableDetailList: HashMap<String, List<String>>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val prescriptionPatientViewModel =
            ViewModelProvider(this).get(PrescriptionPatientViewModel::class.java)

        _binding = FragmentPrescriptionPatientBinding.inflate(inflater, container, false)
        val root: View = binding.root
        expandableListViewExample = binding.expandableListViewSample

        expandableDetailList = ExpandablePrescription.getData()
        expandableTitleList = ArrayList<String>(expandableDetailList?.keys)
        expandableListAdapter =
            this@PrescriptionPatientFragment.context?.let { expandableTitleList?.let { it1 ->
                expandableDetailList?.let { it2 ->
                    CustomizedExpandableListAdapter(
                        it,
                        it1, it2,
                    )
                }
            } }
        expandableListViewExample!!.setAdapter(expandableListAdapter)

        expandableListViewExample!!.setOnGroupExpandListener(OnGroupExpandListener { groupPosition ->
           /* Toast.makeText(
                this@PrescriptionPatientFragment.context,
                expandableTitleList?.get(groupPosition) + " List Expanded.",
                Toast.LENGTH_SHORT
            ).show()*/
        })

        expandableListViewExample!!.setOnGroupCollapseListener(OnGroupCollapseListener { groupPosition ->
           /* Toast.makeText(
                this@PrescriptionPatientFragment.context,
                expandableTitleList?.get(groupPosition) + " List Collapsed.",
                Toast.LENGTH_SHORT
            ).show()*/
        })

        expandableListViewExample!!.setOnChildClickListener(OnChildClickListener { parent, v, groupPosition, childPosition, id ->
         /*   Toast.makeText(
                this@PrescriptionPatientFragment.context, expandableTitleList?.get(groupPosition)
                        + " -> "
                        + expandableTitleList?.let {
                    expandableDetailList?.get(
                        it[groupPosition]
                    )?.get(
                        childPosition
                    )
                }, Toast.LENGTH_SHORT
            ).show()*/
            false
        })


        return root
    }



}