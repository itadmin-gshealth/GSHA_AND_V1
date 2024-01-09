package com.omif.gsha.adapter

import android.content.res.Resources.NotFoundException
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.omif.gsha.ui.uploads.EhrFragment
import com.omif.gsha.ui.pharma.PharmaFragment
import com.omif.gsha.ui.prescription.PrescriptionFragment
import com.omif.gsha.ui.uploads.UploadsFragment
import com.omif.gsha.ui.uploads.VitalsFragment

class PagerAdapter(fragmentActivity: UploadsFragment):FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount()=3

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                VitalsFragment()
            }
            1 -> {
                EhrFragment()
            }
            2 -> {
                PrescriptionFragment()
            }
            else-> {throw NotFoundException("Position not found")}
        }
        }
    }
