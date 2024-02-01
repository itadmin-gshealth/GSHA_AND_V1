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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
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
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.omif.gsha.R
import com.omif.gsha.adapter.CommonMethods
import com.omif.gsha.adapter.CustomizedExpandableListAdapter
import com.omif.gsha.databinding.FragmentEhrBinding
import com.omif.gsha.databinding.FragmentPharmaBinding
import com.omif.gsha.model.EHRecord
import com.omif.gsha.model.ExpandableEhr
import com.omif.gsha.model.User
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date

class EhrFragment : Fragment() {
    private val pickFromGallery:Int = 101
    private lateinit var storageRef: StorageReference
    private var _binding: FragmentEhrBinding? = null
    private lateinit var btnAttachFiles: ImageView
    private lateinit var ddlDept: Spinner
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
        storageRef = Firebase.storage.reference
        mAuth = FirebaseAuth.getInstance()
        val preferences =
            activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val uType = preferences?.getInt("uType",0)

        btnAttachFiles = binding.btnAddFiles
        if(uType == 2)
        {
            btnAttachFiles.isVisible = false
        }
        btnAttachFiles.setOnClickListener {
            showDialog()
        }
        loadExpandable()
        // Inflate the layout for this fragment
        return root
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
                        mdbRef.child("tblEHR").child(mAuth.currentUser?.uid.toString()).setValue(EHRecord(mAuth.currentUser?.uid.toString(), formatter.format(
                            Date()
                        ),imageLink, ddlDept.selectedItem.toString(),null))
                        Toast.makeText(this@EhrFragment.context,"File added successfully", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this@EhrFragment.context, "Failed in downloading", Toast.LENGTH_SHORT).show();
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

    private fun loadExpandable(){
        expandableListViewExample = binding.expandableListViewSample

        expandableDetailList = ExpandableEhr.getData()
        expandableTitleList = ArrayList<String>(expandableDetailList?.keys)
        expandableListAdapter =
            this@EhrFragment.context?.let { expandableTitleList?.let { it1 ->
                expandableDetailList?.let { it2 ->
                    CustomizedExpandableListAdapter(
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


    }

}