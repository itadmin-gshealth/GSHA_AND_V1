package com.omif.gsha.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.omif.gsha.R
import com.omif.gsha.model.Message

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ITEM_RECEIVE = 1
    val ITEM_SEND = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(viewType == 1)
        {
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            return ReceiveViewHolder(view)
        }
        else
        {
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentMessage = messageList[position]
        if(holder.javaClass == SentViewHolder::class.java){
          val viewHolder = holder as SentViewHolder
          holder.sentMessage.text = currentMessage.message
      }
        else
      {
          val viewHolder = holder as ReceiveViewHolder
          holder.receiveMessage.text = currentMessage.message
      }
    }
    override fun getItemViewType(position: Int): Int
        {
            val currentMessage = messageList[position]
            if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId))
            {
                return ITEM_SEND
            }
            else
            {
                return ITEM_RECEIVE
            }
    }
    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentMessage: TextView = itemView.findViewById<TextView>(R.id.Sent_Message)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiveMessage: TextView = itemView.findViewById<TextView>(R.id.Receive_Message)
    }
}