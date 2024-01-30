package com.omif.gsha.ui.home

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.omif.gsha.databinding.FragmentHomeBinding
import com.omif.gsha.model.User


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        if(mAuth.currentUser != null)
       {
           val preferences = activity?.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)

           val patientId = preferences?.getString("patientId","")
           val patientName = preferences?.getString("patientName","")

           mDbRef.child("tblUserWithType").child(mAuth.currentUser?.uid!!).addValueEventListener(object :
               ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {
                   val user = snapshot.getValue(User::class.java)
                   if (user?.uid == mAuth.currentUser?.uid) {
                       if (user != null) {
                           var editor = preferences?.edit()
                           editor?.putString("uName",user.name)
                           editor?.putString("uEmail",user.email)
                           editor?.putInt("uType",user.uType)
                           if(user.uType == 2)
                           {
                               editor?.putString("dept",user.department)
                           }
                           editor?.commit()
                       }
                   }
               }
               override fun onCancelled(error: DatabaseError) {
                   Toast.makeText(context, error.message, Toast.LENGTH_LONG)
               }
           })
       }

        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val scrollView: ScrollView = binding.scrollView
        scrollView.smoothScrollTo(0, binding.root!!.top)

        /*val videoView: VideoView = binding.videoAd
        videoView.setVisibility(View.VISIBLE);
        val mediaController = MediaController(this.context)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
       val rawId = resources.getIdentifier("gsha_ad", "raw", activity?.packageName ?: "gsha_ad")
        val path = "android.resource://" + (activity?.packageName ?: "gsha_ad") + "/" + rawId
        videoView.setVideoURI(Uri.parse(path))
        videoView.setOnPreparedListener {
            videoView.requestFocus()
         videoView.start()
        }*/
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}