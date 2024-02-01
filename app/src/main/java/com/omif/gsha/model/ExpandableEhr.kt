package com.omif.gsha.model

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ExpandableEhr {
    companion object {
        private lateinit var mdbRef: DatabaseReference

        fun getData(patientId: String): HashMap<String, List<String>>? {
            val expandableDetailList = HashMap<String, List<String>>()
            val gynaecology: MutableList<String> = ArrayList()
            val general: MutableList<String> = ArrayList()
            val paediatrics: MutableList<String> = ArrayList()
            val dermatology: MutableList<String> = ArrayList()
            val dentistry: MutableList<String> = ArrayList()

            paediatrics

            mdbRef = FirebaseDatabase.getInstance().reference
            mdbRef.child("tblEHR").child(patientId).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val ehr = postSnapshot.getValue(EHRecord::class.java)
                        if (patientId == ehr?.uid) {
                            if(ehr?.dept?.trim() =="Gynecology")
                            {
                                gynaecology.add(ehr.link.toString())
                            }
                            if(ehr?.dept?.trim() =="General")
                            {
                                general.add(ehr.link.toString())
                            }
                            if(ehr?.dept?.trim() =="General")
                            {
                                paediatrics.add(ehr.link.toString())
                            }
                            if(ehr?.dept?.trim() =="Dentistry")
                            {
                                dentistry.add(ehr.link.toString())
                            }
                            if(ehr?.dept?.trim() =="Dermatology")
                            {
                                dermatology.add(ehr.link.toString())
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //Toast.makeText(context, error.message, Toast.LENGTH_LONG)
                }
            })
        /*    if(paediatrics.isEmpty())
                paediatrics.add("No Data available")
            if(gynaecology.isEmpty())
                gynaecology.add("No Data available")
            if(dermatology.isEmpty())
                dermatology.add("No Data available")
            if(dentistry.isEmpty())
                dentistry.add("No Data available")
            if(general.isEmpty())
                general.add("No Data available")*/

            expandableDetailList["Paediatrics"] = paediatrics
            expandableDetailList["Gynecology"] = gynaecology
            expandableDetailList["Dermatology"] = dermatology
            expandableDetailList["Dentistry"] = dentistry
            expandableDetailList["General Medicine"] = general
            return expandableDetailList
        }
    }
}