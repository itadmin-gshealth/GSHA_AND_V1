package com.omif.gsha.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.omif.gsha.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth

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