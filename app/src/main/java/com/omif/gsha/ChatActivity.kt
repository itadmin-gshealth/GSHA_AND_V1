package com.omif.gsha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.omif.gsha.adapter.MessageAdapter
import com.omif.gsha.databinding.ActivityChatBinding
import com.omif.gsha.model.Message
import java.text.SimpleDateFormat
import java.util.Date

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: TextView
    private lateinit var sendButton: ImageView
    private lateinit var homeButton: ImageView

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>

    private lateinit var mDbRef: DatabaseReference


    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().getReference()
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid+ receiverUid

        supportActionBar?.title = name

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sendButton)
        homeButton = findViewById(R.id.homeButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter



        mDbRef.child("tblChats").child(senderRoom!!).child("messages")
            .addValueEventListener(object :ValueEventListener{
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
            val messageObject = Message(message, senderUid, receiverUid, formatter.format(Date()))

            mDbRef.child("tblChats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("tblChats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            messageBox.text=""
        }

        homeButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }
    }
}