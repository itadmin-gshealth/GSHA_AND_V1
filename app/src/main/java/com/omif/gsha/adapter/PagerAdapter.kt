package com.omif.gsha.adapter

import android.content.res.Resources.NotFoundException
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.omif.gsha.ui.uploads.EhrFragment
import com.omif.gsha.ui.uploads.PharmaFragment
import com.omif.gsha.ui.uploads.UploadsFragment

class PagerAdapter(fragmentActivity: UploadsFragment):FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount()=2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                PharmaFragment()
            }
            1 -> {
                EhrFragment()
            }
            else-> {throw NotFoundException("Position not found")}
        }
        }
    }
