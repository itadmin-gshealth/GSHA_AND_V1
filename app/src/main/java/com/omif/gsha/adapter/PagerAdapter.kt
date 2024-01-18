package com.omif.gsha.adapter

import android.content.Context
import android.content.res.Resources.NotFoundException
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.omif.gsha.ui.uploads.EhrFragment
import com.omif.gsha.ui.pharma.PharmaFragment
import com.omif.gsha.ui.prescription.PrescriptionFragment
import com.omif.gsha.ui.uploads.UploadsFragment
import com.omif.gsha.ui.uploads.VitalsFragment

class PagerAdapter(fragmentActivity: UploadsFragment, uType: Int):FragmentStateAdapter(fragmentActivity) {

    var uTypeVal = uType

    private fun getCount(): Int {
        if(uTypeVal == 2)
        return 2
        else return 3
    }
    override fun getItemCount() = getCount()

    override fun createFragment(position: Int): Fragment {
        if (uTypeVal == 2) {
            return when (position) {
                0 -> {
                    EhrFragment()
                }

                1 -> {
                    PrescriptionFragment()
                }

                else -> {
                    throw NotFoundException("Position not found")
                }
            }
        } else {
        return when (position) {
            0 -> {
                VitalsFragment()
            }

            1 -> {
                EhrFragment()
            }

            2 -> {
                PrescriptionFragment()
            }

            else -> {
                throw NotFoundException("Position not found")
            }
        }
    }
        }
    }
