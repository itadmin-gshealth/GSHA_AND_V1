package com.omif.gsha.ui.uploads

import android.content.Context
import android.os.Bundle
import android.text.LoginFilter.UsernameFilterGeneric
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
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.omif.gsha.adapter.CommonMethods
import com.omif.gsha.adapter.ExpandableAdapter
import com.omif.gsha.databinding.FragmentPrescriptionPatientBinding
import com.omif.gsha.model.ExpandablePrescription


class PrescriptionPatientFragment : Fragment() {

    private lateinit var txtMeds: TextView
    private lateinit var txtRegNo: TextView
    private lateinit var txtDept: TextView
    private lateinit var btnGetMedicine: Button
    private lateinit var btnDownload: Button
    private lateinit var mAuth: FirebaseAuth
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
        mAuth = FirebaseAuth.getInstance()
        val preferences =
            activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val uName = preferences?.getString("uName", "").toString()
        val uGender = preferences?.getString("uGender", "").toString()
        val uAge = preferences?.getInt("uAge",0)!!
        _binding = FragmentPrescriptionPatientBinding.inflate(inflater, container, false)
        val root: View = binding.root
        expandableListViewExample = binding.expandableListViewSample


        expandableDetailList = ExpandablePrescription.getData(mAuth.currentUser?.uid.toString())
        expandableTitleList = ArrayList<String>(expandableDetailList?.keys)
        expandableListAdapter =
            this@PrescriptionPatientFragment.context?.let { expandableTitleList?.let { it1 ->
                expandableDetailList?.let { it2 ->
                    ExpandableAdapter(
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
            var fragmentManager: FragmentManager? = null
            fragmentManager = parentFragmentManager
            val date = expandableTitleList?.let {
                expandableDetailList?.get(
                    it[groupPosition]
                )?.get(
                    childPosition
                )
            }
            this@PrescriptionPatientFragment.context?.let { CommonMethods.showPresDialog(it, fragmentManager, date, uName, uAge, uGender ) }

           /* Toast.makeText(
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