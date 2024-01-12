package com.omif.gsha.ui.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
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

        val textView: TextView = binding.textMessages
        messagesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        userList = ArrayList()
        emailList = ArrayList<String>()
        loggedInUser = User()
        adapter = UserAdapter(binding.root.context, userList )

        userRecyclerView = binding.userRecyclerView
        userRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        userRecyclerView.adapter = adapter

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
                                                             }
                                                         }
                                                     }
                                                     adapter.notifyDataSetChanged()
                                                 }

                                                 override fun onCancelled(error: DatabaseError) {
                                                     Toast.makeText(context, error.message, Toast.LENGTH_LONG)
                                                 }
                                             })
                                         }

                                     }
                                 }
                             }

                             adapter.notifyDataSetChanged()
                         }
                         override fun onCancelled(error: DatabaseError) {
                             Toast.makeText(context, error.message, Toast.LENGTH_LONG)
                         }
                     })

                 if(loggedInUser.uType == 1) {
                     mDbRef.child("tblDoctor").addValueEventListener(object : ValueEventListener {
                         override fun onDataChange(snapshot: DataSnapshot) {
                             userList.clear()
                             for (postSnapshot in snapshot.children) {
                                 val currentUser = postSnapshot.getValue(User::class.java)
                                 if (mAuth.currentUser?.uid != currentUser?.uid && currentUser?.uType == 2) {
                                     userList.add(currentUser!!)
                                 }
                             }
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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}