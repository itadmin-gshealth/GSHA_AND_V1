package com.omif.gsha.ui.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
import com.omif.gsha.MainActivity
import com.omif.gsha.R
import com.omif.gsha.adapter.MessageAdapter
import com.omif.gsha.databinding.ActivityChatBinding
import com.omif.gsha.databinding.FragmentAppointmentBinding
import com.omif.gsha.databinding.FragmentChatBinding
import com.omif.gsha.model.Message
import java.text.SimpleDateFormat
import java.util.Date


class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: TextView
    private lateinit var sendButton: ImageView
    private lateinit var homeButton: ImageView

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>

    private lateinit var mDbRef: DatabaseReference


    var receiverRoom: String? = null
    var senderRoom: String? = null


    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val chatViewModel =
            ViewModelProvider(this).get(ChatViewModel::class.java)

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val name = activity?.intent?.extras?.getString("name")
        //val name =  intent.getStringExtra("name")
        //val receiverUid = intent.getStringExtra("uid")
        val receiverUid = activity?.intent?.extras?.getString("uid")


        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().reference
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid+ receiverUid

        //supportActionBar?.title = name

        chatRecyclerView = binding.chatRecyclerView
        messageBox = binding.messageBox
        sendButton = binding.sendButton
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this@ChatFragment.context!!, messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this@ChatFragment.context)
        chatRecyclerView.adapter = messageAdapter


        mDbRef.child("tblChats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for(postSnapshot in snapshot.children)
                    {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(messageList.size-1)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        sendButton.setOnClickListener{
            val message = messageBox.text.toString()
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val messageObject = Message(message, senderUid, receiverUid, formatter.format(Date()) )

            mDbRef.child("tblChats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("tblChats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            messageBox.text=""
        }

        return root   }
}



