package com.omif.gsha.model

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ExpandablePrescription {
    companion object {
        private lateinit var mdbRef: DatabaseReference
        fun getData(patientId: String): HashMap<String, List<String>>? {
            val expandableDetailList = HashMap<String, List<String>>()

            val paediatrics: MutableList<String> = ArrayList()
            val gynaecology: MutableList<String> = ArrayList()
            val dermatology: MutableList<String> = ArrayList()
            val dentistry: MutableList<String> = ArrayList()
            val general: MutableList<String> = ArrayList()

            mdbRef = FirebaseDatabase.getInstance().reference
            mdbRef.child("tblPrescription").child(patientId).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val prescription = postSnapshot.getValue(Prescription::class.java)
                        if (patientId == prescription?.patientId) {
                            if(prescription?.department?.trim() =="Gynecology")
                            {
                                gynaecology.add(prescription.date.toString())
                            }
                            if(prescription?.department?.trim() =="General")
                            {
                                general.add(prescription.date.toString())
                            }
                            if(prescription?.department?.trim() =="Paediatrics")
                            {
                                paediatrics.add(prescription.date.toString())
                            }
                            if(prescription?.department?.trim() =="Dentistry")
                            {
                                dentistry.add(prescription.date.toString())
                            }
                            if(prescription?.department?.trim() =="Dermatology")
                            {
                                dermatology.add(prescription.date.toString())
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //Toast.makeText(context, error.message, Toast.LENGTH_LONG)
                }
            })

            expandableDetailList["Paediatrics"] = paediatrics
            expandableDetailList["Gynecology"] = gynaecology
            expandableDetailList["Dermatology"] = dermatology
            expandableDetailList["Dentistry"] = dentistry
            expandableDetailList["General Medicine"] = general

            return expandableDetailList
        }
    }
}