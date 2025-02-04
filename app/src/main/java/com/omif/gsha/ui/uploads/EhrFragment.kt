package com.omif.gsha.ui.uploads

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.omif.gsha.adapter.CommonMethods
import com.omif.gsha.adapter.ExpandableAdapter
import com.omif.gsha.databinding.FragmentEhrBinding
import com.omif.gsha.model.EHRecord
import com.omif.gsha.model.ExpandableEhr
import com.omif.gsha.model.Prescription
import java.text.SimpleDateFormat
import java.util.Date


class EhrFragment : Fragment() {
    private val pickFromGallery:Int = 101
    private lateinit var storageRef: StorageReference
    private var _binding: FragmentEhrBinding? = null
    private lateinit var btnAttachFiles: ImageView
    private lateinit var txtPatientSelect: TextView
    private lateinit var ddlDept: Spinner
    private lateinit var ddlPatient: Spinner
    private lateinit var mdbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    var dept = arrayOf(
        "Department","Gynecology ", "Paediatrics", "Dentistry ", "General", "Dermatology ", "Psychiatrist"
    )
    var selectedDept = ""
    var imageLink : String? = ""
    var expandableListViewExample: ExpandableListView? = null
    var expandableListAdapter: ExpandableListAdapter? = null
    var expandableTitleList: List<String>? = null
    var expandableDetailList: HashMap<String, List<String>>? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ehrViewModel =
            ViewModelProvider(this).get(EhrViewModel::class.java)

        _binding = FragmentEhrBinding.inflate(inflater, container, false)
        val root: View = binding.root
        ddlPatient = binding.ddlPatients
        expandableListViewExample = binding.expandableListViewSample
        storageRef = Firebase.storage.reference
        mAuth = FirebaseAuth.getInstance()
        val preferences =
            activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val uType = preferences?.getInt("uType",0)

        btnAttachFiles = binding.btnAddFiles
        txtPatientSelect = binding.textSelectPatient
        if(uType == 2)
        {
            btnAttachFiles.isVisible = false
            var pNames = preferences?.getString("patientNames", "").toString()
            var pIds = preferences?.getString("patientUids", "").toString()
            var pos = 0
            var listPNames = listOf<String>()
            var pId = listOf<String>()
            if(!pNames.isNullOrBlank()) {
                listPNames = pNames.split(",").map { it.trim() }
                pId = pIds.split(",").map { it.trim() }
                listPNames = listOf(" ") + listPNames
                var adapter = this@EhrFragment.context?.let {
                    ArrayAdapter<String>(
                        it, android.R.layout.simple_spinner_item, listPNames.dropLast(1)
                    )
                }
                adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                ddlPatient.adapter = adapter

                ddlPatient.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>?,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        if(!ddlPatient.selectedItem.toString().isNullOrBlank()) {
                            expandableListViewExample?.isVisible = true
                            loadExpandable(pId[position-1])
                        }
                        else {
                            expandableListViewExample?.isVisible = false
                            Toast.makeText(
                                this@EhrFragment.context,
                                "Please select Patient",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>?) {
                        Toast.makeText(this@EhrFragment.context, "Please select Patient", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
        btnAttachFiles.setOnClickListener {
            showDialog()
        }
        if(uType == 1 || uType == 3)
        {
            ddlPatient.isVisible = false
            txtPatientSelect.isVisible = false
            loadExpandable(mAuth.currentUser?.uid.toString())
            expandableListViewExample!!.isVisible = true
        }
        return root
    }

    private fun sendData() : Array<ArrayList<String>>
    {
        val arrayOfArray : Array<ArrayList<String>> = arrayOf()


        return arrayOfArray
    }

    private fun showDialog() {

        ddlDept = Spinner(this@EhrFragment.context)
        val adapter: ArrayAdapter<String>? = this@EhrFragment.context?.let {
            ArrayAdapter<String>(
                it, android.R.layout.simple_spinner_item, dept
            )
        }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ddlDept.adapter = adapter

        val textView = TextView(context)
        textView.text = "Select Department"
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 20f
        textView.setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
        textView.setTextColor(Color.WHITE)
        textView.typeface = Typeface.DEFAULT_BOLD;
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setView(ddlDept)
            .setCustomTitle(textView)
            .setNegativeButton("CLOSE") { dialog, which -> dialog.dismiss()}
            .setPositiveButton("Ok") { dialog, which ->
                selectedDept = ddlDept.selectedItem.toString()
                if(selectedDept == "Department")
                {
                    Toast.makeText(this@EhrFragment.context, "Please select Department", Toast.LENGTH_SHORT).show();
                }
                else
                {
                   /* this.context?.let { it1 -> CommonMethods(it1).openFolder() }*/
                    val intent = Intent()
                    intent.type = "*/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    imagePickerActivityResult.launch(intent)
                }
            }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(20, 0, 0, 0)

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.apply {
            getButton(DialogInterface.BUTTON_POSITIVE).apply {
                setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD;
                layoutParams = params;
            }
            getButton(DialogInterface.BUTTON_NEGATIVE).apply {
                setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD;
                layoutParams = params;
            }
        }
    }

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                val imageUri: Uri? = result.data?.data
                val sd = this@EhrFragment.context?.let { getFileName(it, imageUri!!) }
                val uploadTask = imageUri?.let { storageRef.child("ehr/$sd").putFile(it) }
                uploadTask?.addOnSuccessListener {
                    storageRef.child("ehr/$sd").downloadUrl.addOnSuccessListener {
                       // Picasso.get().load(it.toString()).into(userImg)
                        imageLink = it.toString()
                        mdbRef = FirebaseDatabase.getInstance().reference
                        mdbRef.child("tblEHR").child(mAuth.currentUser?.uid.toString()).child(formatter.format(Date())).setValue(EHRecord(mAuth.currentUser?.uid.toString(), formatter.format(
                            Date()
                        ),imageLink, ddlDept.selectedItem.toString(),null))
                        loadExpandable(mAuth.currentUser?.uid.toString())
                        Toast.makeText(this@EhrFragment.context,"File added successfully", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this@EhrFragment.context, "Error in upload", Toast.LENGTH_SHORT).show();
                    }
                }?.addOnFailureListener {
                    Toast.makeText(this@EhrFragment.context, "Failed in downloading", Toast.LENGTH_SHORT).show();
                    //Log.e("Firebase", "Image Upload fail")
                }
            }
        }

    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }

    private fun loadExpandable(patientId: String){
        expandableDetailList = ExpandableEhr.getData(patientId)
        expandableTitleList = ArrayList<String>(expandableDetailList?.keys)
        expandableListAdapter =
            this@EhrFragment.context?.let { expandableTitleList?.let { it1 ->
                expandableDetailList?.let { it2 ->
                    ExpandableAdapter(
                        it,
                        it1, it2,
                    )
                }
            } }
        expandableListViewExample!!.setAdapter(expandableListAdapter)

        expandableListViewExample!!.setOnGroupExpandListener(ExpandableListView.OnGroupExpandListener { groupPosition ->
            /* Toast.makeText(
                 this@PrescriptionPatientFragment.context,
                 expandableTitleList?.get(groupPosition) + " List Expanded.",
                 Toast.LENGTH_SHORT
             ).show()*/
        })

        expandableListViewExample!!.setOnGroupCollapseListener(ExpandableListView.OnGroupCollapseListener { groupPosition ->
            /* Toast.makeText(
                 this@PrescriptionPatientFragment.context,
                 expandableTitleList?.get(groupPosition) + " List Collapsed.",
                 Toast.LENGTH_SHORT
             ).show()*/
        })

        expandableListViewExample!!.setOnChildClickListener(ExpandableListView.OnChildClickListener { parent, v, groupPosition, childPosition, id ->
              /* Toast.makeText(
                   this@EhrFragment.context, expandableTitleList?.get(groupPosition)
                           + " -> "
                           + expandableTitleList?.let {
                       expandableDetailList?.get(
                           it[groupPosition]
                       )?.get(
                           childPosition
                       )
                   }, Toast.LENGTH_SHORT
               ).show()*/
            val date = expandableTitleList?.let {
                expandableDetailList?.get(
                    it[groupPosition]
                )?.get(
                    childPosition
                )
            }

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(date))
            startActivity(browserIntent)
            false
        })


    }

}