package com.omif.gsha

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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
import com.omif.gsha.model.User
import com.omif.gsha.model.Vitals
import java.text.SimpleDateFormat
import java.util.Date


class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: TextView
    private lateinit var sendButton: ImageView
    private lateinit var homeButton: ImageView
    private lateinit var vitalButton: ImageView

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var vitalsList: ArrayList<Vitals>

    private lateinit var mDbRef: DatabaseReference

    private lateinit var tableLayout: TableLayout

    var receiverRoom: String? = null
    var senderRoom: String? = null

    var name: String? = null
    var vitalNames = arrayOf(
        "Date", "P/R","BP", "spO2", "Temp", "Sugar", "Height", "Weight"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        name = intent.getStringExtra("name")
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
        vitalButton = findViewById(R.id.vitalButton)

        messageList = ArrayList()
        vitalsList = ArrayList()
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
        val preferences =
            getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val uType = preferences.getInt("uType",0)

        homeButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }

        if(uType != 2)
        {
            vitalButton.isVisible = false
            if(receiverUid!= null)
            {
                var editor = preferences?.edit()
                editor?.putString("patientName",name)
                editor?.putString("patientId",receiverUid.toString())
                editor?.commit()
            }
        }
        else
        {
            vitalButton.setOnClickListener{
                if (receiverUid != null) {
                    val formatter = SimpleDateFormat("yyyy-MM-dd")
                    mDbRef.child("tblVitals")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                vitalsList.clear()
                                for (postSnapshot in snapshot.children) {
                                    val vitals = postSnapshot.getValue(Vitals::class.java)
                                    if (vitals?.date.toString().substring(0,10) <= formatter.format(Date()) && vitals?.uid == receiverUid) {
                                        var check = vitals
                                        if (vitals != null) {
                                            vitalsList.add(vitals)
                                        }
                                    }
                                }
                                if(vitalsList.size > 0) {
                                    vitalsList.sortByDescending{it.date}
                                    dialogTable(vitalsList)
                                }
                                else
                                {
                                    Toast.makeText(this@ChatActivity, "Vitals are not recorded !!", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@ChatActivity, "", Toast.LENGTH_SHORT).show()
                            }
                        })
                }

            }
        }

    }

    private fun dialogTable(vitalsList: ArrayList<Vitals>) {
        val textView = TextView(this)
        textView.text = "VITALS"
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 20f
        textView.setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
        textView.setTextColor(Color.WHITE)
        textView.typeface = Typeface.DEFAULT_BOLD;

        val builder = AlertDialog.Builder(this)
        val dialogContext: Context = builder.context
        var i: Int = 0
        var j: Int = 0
        tableLayout = TableLayout(dialogContext)

        while (i < vitalNames.size) {
            val tableRow = TableRow(dialogContext)
            tableRow.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            val textView1 = TextView(dialogContext)
            textView1.text = vitalNames[i].toString()+" "
            textView1.setPadding(20, 30, 20, 30)
            textView1.textSize = 20f
            textView1.setTextColor(Color.BLACK)
            textView1.typeface = Typeface.DEFAULT_BOLD;
            tableRow.addView(textView1)

            val textView2 = TextView(dialogContext)
            if (vitalNames[i].toString() == "P/R") {
                textView2.text = vitalsList[j].pulseRate.toString() + " "
                textView2.setPadding(20, 30, 20, 30)
                textView2.textSize = 18f
                textView2.setTextColor(Color.BLACK)
                textView2.typeface = Typeface.DEFAULT_BOLD;
                tableRow.addView(textView2)
            }

            val textView9 = TextView(dialogContext)
            if (vitalNames[i].toString() == "Date") {
                textView9.text = vitalsList[j].date.toString() + " "
                textView9.setPadding(20, 30, 20, 30)
                textView9.textSize = 15f
                textView9.setTextColor(Color.BLACK)
                textView9.typeface = Typeface.DEFAULT_BOLD;
                tableRow.addView(textView9)
            }

            val textView3 = TextView(dialogContext)
            if (vitalNames[i].toString() == "BP") {
                textView3.text = vitalsList[j].bpDia.toString() + "/" + vitalsList[j].bpSys.toString() + " "
                textView3.setPadding(20, 30, 20, 30)
                textView3.textSize = 18f
                textView3.setTextColor(Color.BLACK)
                textView3.typeface = Typeface.DEFAULT_BOLD;
                tableRow.addView(textView3)
            }

            val textView4 = TextView(dialogContext)
            if (vitalNames[i].toString() == "spO2") {
                textView4.text = vitalsList[j].spO2.toString() + " "
                textView4.setPadding(20, 30, 20, 30)
                textView4.textSize = 18f
                textView4.setTextColor(Color.BLACK)
                textView4.typeface = Typeface.DEFAULT_BOLD;
                tableRow.addView(textView4)
            }

            val textView5 = TextView(dialogContext)
            if (vitalNames[i].toString() == "Temp") {
                textView5.text = vitalsList[j].temp.toString() + " "
                textView5.setPadding(20, 30, 20, 30)
                textView5.textSize = 18f
                textView5.setTextColor(Color.BLACK)
                textView5.typeface = Typeface.DEFAULT_BOLD;
                tableRow.addView(textView5)
            }

            val textView6 = TextView(dialogContext)
            if (vitalNames[i].toString() == "Sugar") {
                textView6.text = vitalsList[j].sugar.toString() + " "
                textView6.setPadding(20, 30, 20, 30)
                textView6.textSize = 18f
                textView6.setTextColor(Color.BLACK)
                textView6.typeface = Typeface.DEFAULT_BOLD;
                tableRow.addView(textView6)
            }

            val textView7 = TextView(dialogContext)
            if (vitalNames[i].toString() == "Height") {
                textView7.text = vitalsList[j].height.toString() + " "
                textView7.setPadding(20, 30, 20, 30)
                textView7.textSize = 18f
                textView7.setTextColor(Color.BLACK)
                textView7.typeface = Typeface.DEFAULT_BOLD;
                tableRow.addView(textView7)
            }

            val textView8 = TextView(dialogContext)
            if (vitalNames[i].toString() == "Weight") {
                textView8.text = vitalsList[j].weight.toString() + " "
                textView8.setPadding(20, 30, 20, 30)
                textView8.textSize = 18f
                textView8.setTextColor(Color.BLACK)
                textView8.typeface = Typeface.DEFAULT_BOLD;
                tableRow.addView(textView8)
            }
            tableLayout.addView(tableRow)
            i++
        }
        builder.setCancelable(true)
                .setCustomTitle(textView)
                .setView(tableLayout)
                .setPositiveButton("Ok") { dialog, which ->
                    i=0
                    j=0
            }
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(20, 0, 0, 0)
        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.apply {
            getButton(DialogInterface.BUTTON_POSITIVE).apply {
                setBackgroundColor(resources.getColor(com.omif.gsha.R.color.purple_700))
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD;
                layoutParams = params;
            }
        }
    }



}