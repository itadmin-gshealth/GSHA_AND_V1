package com.omif.gsha.ui.prescription

import android.R
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.omif.gsha.databinding.FragmentPrescriptionBinding
import com.omif.gsha.model.Prescription
import java.text.SimpleDateFormat
import java.util.Date

class PrescriptionFragment : Fragment() {

    private var _binding: FragmentPrescriptionBinding? = null
    private lateinit var mAuth: FirebaseAuth
    private val binding get() = _binding!!
    private lateinit var mdbRef: DatabaseReference
    private lateinit var viewModel: PrescriptionViewModel
    private lateinit var btnSave: Button
    private lateinit var ddlPatients: Spinner
    private lateinit var ddlDiagnosis: Spinner
    private lateinit var txtRegNo: TextView
    private lateinit var txtDocRegNo: TextView
    private lateinit var txtDept: TextView
    private lateinit var txtMedicine: TextView

    var pageHeight = 1120
    var pageWidth = 792

    lateinit var bmp: Bitmap
    lateinit var scaledbmp: Bitmap

    var PERMISSION_CODE = 101

    var diag = arrayOf(
        " ","Tuberclosis ", "Eczema", "Mumps ", "Dengue", "Malaria ", "Alzheimer's", "Parkinson","Typhoid", "Arthritis", "Viral Fever"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val prescriptionViewModel =
            ViewModelProvider(this).get(PrescriptionViewModel::class.java)

        _binding = FragmentPrescriptionBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        mAuth = FirebaseAuth.getInstance()
        val preferences = activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        var adapter: ArrayAdapter<String>?
        btnSave = binding.btnSavePrescription
        ddlPatients = binding.ddlPatients
        ddlDiagnosis = binding.ddlDiagnosis
        txtRegNo = binding.regNo
        txtDocRegNo = binding.docRegNo
        txtDept = binding.dept
        txtMedicine = binding.meds
        txtDept.text = preferences?.getString("dept", "").toString()
        txtRegNo.text ="414/DM&HO/MED/2008"
        var pNames = preferences?.getString("patientNames", "").toString()
        var pIds = preferences?.getString("patientUids", "").toString()
        txtDocRegNo.text = preferences?.getString("regNo", "").toString()
        var pos = 0
        var listPNames = listOf<String>()
        if(!pNames.isNullOrBlank()) {
            listPNames = pNames.split(",").map { it.trim() }
            listPNames = listOf(" ") + listPNames
            adapter = this@PrescriptionFragment.context?.let {
                ArrayAdapter<String>(
                    it, R.layout.simple_spinner_item, listPNames.dropLast(1)
                )
            }
            adapter?.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            ddlPatients.setAdapter(adapter)

            ddlPatients.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    ddlDiagnosis.setSelection(0,true);
                    }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    Toast.makeText(this@PrescriptionFragment.context, "Please select Patient", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            loadDiagnosis()
        }
        else{
            Toast.makeText(this@PrescriptionFragment.context, "Please visit waiting area to check on the patients", Toast.LENGTH_SHORT).show()
        }
                /*if (checkPermissions()) {
                    Toast.makeText(this@PrescriptionFragment.context, "Permissions Granted..", Toast.LENGTH_SHORT).show()
                } else {
                    requestPermission()
                }*/
        btnSave.setOnClickListener {
            var pid = ""
            if (checkAllFields()) {
                if (!pIds.isNullOrBlank()) {
                    pos = listPNames.indexOf(ddlPatients.selectedItem)
                    pos -= 1
                    var listPids = pIds.split(",").map { it.trim() }
                    pid = listPids[pos].toString()
                }
                mdbRef = FirebaseDatabase.getInstance().reference
                mdbRef.child("tblPrescription")
                    .child(pid).child(formatter.format(Date())).setValue(
                        Prescription(
                            txtMedicine.text.toString(),
                            preferences?.getString("uName", "").toString(),
                            mAuth.currentUser?.uid.toString(),
                            ddlPatients.selectedItem.toString(),
                            pid,
                            preferences?.getString("dept", "").toString(),
                            "",
                            "",
                            formatter.format(Date())
                        )
                    )
                Toast.makeText(
                    this@PrescriptionFragment.context,
                    "Prescription saved successfully",
                    Toast.LENGTH_SHORT
                ).show()
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    //generatePDF()
                }
            }
        }
        return root   }

    private fun loadDiagnosis()
    {
        var adapterDiag: ArrayAdapter<String>? = this@PrescriptionFragment.context?.let {
            ArrayAdapter<String>(
                it, R.layout.simple_spinner_item, diag
            )
        }
        adapterDiag?.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        ddlDiagnosis.adapter = adapterDiag
    }

    private fun checkAllFields(): Boolean {
        if (ddlPatients.selectedItem.toString().isNullOrBlank()) {
            Toast.makeText(
                this@PrescriptionFragment.context,
                "Select patient to prescribe",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
       else if (txtMedicine.text.toString().isNullOrBlank()) {
           Toast.makeText(
               this@PrescriptionFragment.context,
               "Prescription is empty",
               Toast.LENGTH_SHORT
           ).show()
           return false
        }
        // after all validation return true.
        return true
    }

/*    private fun checkPermissions(): Boolean {
        var writeStoragePermission = this@PrescriptionFragment.context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        var readStoragePermission = this@PrescriptionFragment.context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
        return writeStoragePermission == PackageManager.PERMISSION_GRANTED
                && readStoragePermission == PackageManager.PERMISSION_GRANTED
    }*/

   /* private fun requestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_CODE
        )
    }

    fun generatePDF() {
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.avatar)
        val scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false)
        var pdfDocument: PdfDocument = PdfDocument()
        var paint: Paint = Paint()
        var title: Paint = Paint()
        var myPageInfo: PdfDocument.PageInfo? =
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        var myPage: PdfDocument.Page = pdfDocument.startPage(myPageInfo)
        var canvas: Canvas = myPage.canvas
        canvas.drawBitmap(scaledbmp, 56F, 40F, paint)
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
        title.textSize = 15F
        title.color = resources.getColor(R.color.purple_200)
        canvas.drawText("A portal for IT professionals.", 209F, 100F, title)
        canvas.drawText("Geeks for Geeks", 209F, 80F, title)
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        this@PrescriptionFragment.context?.let { ContextCompat.getColor(it, R.color.purple_200) }
            ?.let { title.setColor(it) }
        title.textSize = 15F
        title.textAlign = Paint.Align.CENTER
        canvas.drawText("This is sample document which we have created.", 396F, 560F, title)
        pdfDocument.finishPage(myPage)
        val file: File = File(Environment.getExternalStorageDirectory(), "GFG.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this@PrescriptionFragment.context, "PDF file generated..", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this@PrescriptionFragment.context, e.message, Toast.LENGTH_SHORT)
                .show()
        }
        pdfDocument.close()
    }*/


  /*  override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.size > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]
                    == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this@PrescriptionFragment.context, "Permission Granted..", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@PrescriptionFragment.context, "Permission Denied..", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }*/
}







