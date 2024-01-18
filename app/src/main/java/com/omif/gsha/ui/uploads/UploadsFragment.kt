package com.omif.gsha.ui.uploads

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.omif.gsha.adapter.PagerAdapter
import com.omif.gsha.databinding.FragmentUploadsBinding
import com.omif.gsha.ui.signin.SignInFragment

class UploadsFragment : Fragment() {

    private var _binding: FragmentUploadsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth

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

        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        mAuth = FirebaseAuth.getInstance()

        if(mAuth.currentUser != null)
        {
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
        }
        else
        {
            showDialog()
        }

        return root
    }

    private fun showDialog() {
        val textView = TextView(context)
        textView.text = "GSHA Says"
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 20f
        textView.setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
        textView.setTextColor(Color.WHITE)
        textView.typeface = Typeface.DEFAULT_BOLD;

        val textView1 = TextView(context)
        textView1.text = "Sign-Up/Log-In to connect with a Doctor"
        textView1.setPadding(20, 30, 20, 30)
        textView1.textSize = 20f
        textView1.setBackgroundColor(Color.WHITE)
        textView1.setTextColor(Color.BLACK)
        textView1.typeface = Typeface.DEFAULT_BOLD;
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setView(textView1)
            .setCustomTitle(textView)
            .setPositiveButton("Ok") { dialog, which ->
                var fragment: Fragment? = null
                fragment = SignInFragment()
                replaceFragment(fragment)
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

    private fun replaceFragment(someFragment: Fragment?) {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        if (someFragment != null) {
            transaction.replace(com.omif.gsha.R.id.nav_host_fragment_activity_main, someFragment)
        }
        //transaction.addToBackStack(null)
        transaction.commit()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}