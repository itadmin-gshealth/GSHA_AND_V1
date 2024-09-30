package com.omif.gsha.ui.messages

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.omif.gsha.adapter.UserAdapter
import com.omif.gsha.databinding.FragmentMessagesBinding
import com.omif.gsha.model.Message
import com.omif.gsha.model.User
import com.omif.gsha.ui.pharma.PharmaFragment
import com.omif.gsha.ui.signin.SignInFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class MessagesFragment : Fragment() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var loggedInUser:User
    private lateinit var emailList: ArrayList<String>
    private var _binding: FragmentMessagesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val messagesViewModel =
                ViewModelProvider(this).get(MessagesViewModel::class.java)

        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        userList = ArrayList()
        emailList = ArrayList<String>()
        loggedInUser = User()
        adapter = UserAdapter(binding.root.context, userList )

        userRecyclerView = binding.userRecyclerView
        userRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        userRecyclerView.adapter = adapter

        val preferences =
            activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        var uType = preferences?.getInt("uType",0)!!
        if(mAuth.currentUser !=null)
        {
            mDbRef.child("tblUserWithType").child(mAuth.currentUser?.uid!!).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user?.uid == mAuth.currentUser?.uid) {
                        if (user != null) {
                            loggedInUser = user
                        }
                    }
                    if(loggedInUser.uType == 2)
                        mDbRef.child("tblChats").addValueEventListener(object: ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                userList.clear()
                                var patientNames = ""
                                var patientuids = ""
                                val formatter = SimpleDateFormat("yyyy-MM-dd")
                                val cal: Calendar = Calendar.getInstance()
                                cal.add(Calendar.DATE, -1)
                                val yesterday = formatter.format(cal.time)
                                for (postSnapshot in snapshot.children) {
                                    val value = postSnapshot.children
                                    for(item in postSnapshot.children)
                                    {
                                        for(item in item.children)
                                        {
                                            val mess = item.getValue(Message::class.java)
                                            if (mAuth.currentUser?.uid == mess?.receiverId && (mess?.messageDate == formatter.format(Date()) || mess?.messageDate == yesterday.toString()))
                                            {

                                                mDbRef.child("tblPatient").addValueEventListener(object : ValueEventListener {
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        for (postSnapshot in snapshot.children) {
                                                            val currentUser = postSnapshot.getValue(User::class.java)
                                                            if (mess?.senderId == currentUser?.uid ) {
                                                                if(!emailList.contains(currentUser?.email.toString()))
                                                                {
                                                                    emailList.add(currentUser?.email.toString())
                                                                    userList.add(currentUser!!)
                                                                    patientNames = patientNames.plus(currentUser.name.plus(","))
                                                                    patientuids = patientuids.plus(currentUser.uid.plus(","))
                                                                }
                                                            }
                                                        }
                                                        var editor = preferences?.edit()
                                                        editor?.putString("patientNames",patientNames)
                                                        editor?.putString("patientUids",patientuids)
                                                        editor?.commit()
                                                        adapter.notifyDataSetChanged()
                                                    }


                                                    override fun onCancelled(error: DatabaseError) {
                                                        Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                                                    }
                                                })
                                            }

                                        }
                                    }
                                }

                                adapter.notifyDataSetChanged()
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                            }
                        })

                    if(loggedInUser.uType == 1 || loggedInUser.uType == 3) {
                        mDbRef.child("tblDoctor").addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                userList.clear()
                                for (postSnapshot in snapshot.children) {
                                    val currentUser = postSnapshot.getValue(User::class.java)
                                    if (mAuth.currentUser?.uid != currentUser?.uid && currentUser?.uType == 2) {
                                        if(preferences?.getString("selectedDept","").isNullOrBlank())
                                        {
                                            userList.add(currentUser!!)
                                        }
                                        else
                                        {
                                            if(currentUser?.department == preferences?.getString("selectedDept",""))
                                            {
                                                userList.add(currentUser!!)
                                            }
                                        }
                                    }
                                }
                                var editor = preferences?.edit()
                                editor?.putString("selectedDept","")
                                editor?.commit()
                                adapter.notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context, error.message, Toast.LENGTH_LONG)
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_LONG)
                }
            })
        }
        else
        {
            showDialog()
        }

        val textView: TextView = binding.textMessages
        messagesViewModel.text.observe(viewLifecycleOwner) {
            if(uType == 2) textView.text ="WAITING AREA" else
            {textView.text = it}
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
        textView1.textSize = 15f
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