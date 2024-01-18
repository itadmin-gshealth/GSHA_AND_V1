package com.omif.gsha.ui.uploads

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.omif.gsha.adapter.PagerAdapter
import com.omif.gsha.databinding.FragmentUploadsBinding

class UploadsFragment : Fragment() {

    private var _binding: FragmentUploadsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val uploadsViewModel =
                ViewModelProvider(this).get(UploadsViewModel::class.java)

        _binding = FragmentUploadsBinding.inflate(inflater, container, false)
        val root: View = binding.root

      /*  val intent = Intent(binding.root.context,UploadsActivity::class.java)
        startActivity(intent)*/
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        val preferences =
            activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val uType = preferences?.getInt("uType",0)
        viewPager.adapter = uType?.let { PagerAdapter(this, it) }

        if(uType == 2)
        {
            TabLayoutMediator(tabLayout, viewPager){tab, index ->
                tab.text = when(index){
                    0->{"RECORDS"}
                    1->{"MEDS"}
                    else-> {throw Resources.NotFoundException("Position not found")
                    }
                }
            }.attach()
        }
        else
        {
            TabLayoutMediator(tabLayout, viewPager){tab, index ->
                tab.text = when(index){
                    0->{"VITALS"}
                    1->{"RECORDS"}
                    2->{"MEDS"}
                    else-> {throw Resources.NotFoundException("Position not found")
                    }
                }
            }.attach()
        }


       /* val textView: TextView = binding.textOffers
        uploadsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}