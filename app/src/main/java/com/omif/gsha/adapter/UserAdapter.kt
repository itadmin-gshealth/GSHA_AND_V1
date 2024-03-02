package com.omif.gsha.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.omif.gsha.ChatActivity
import com.omif.gsha.R
import com.omif.gsha.model.User
import com.squareup.picasso.Picasso

class UserAdapter(val context: Context, private val userList:ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.txtName.text = currentUser.name
        if(currentUser.uType == 2) {
            holder.txtDesignation.text = currentUser.qual
            val status = CommonMethods.getStatus(currentUser.status)
            holder.txtStatus.text = status
        }else{holder.txtDesignation.text = currentUser.gender + ", " + currentUser.age + " yrs"
            holder.txtStatus.text = "Active"}
        if(!currentUser.imageLink.isNullOrBlank())
        Picasso.get().load(currentUser.imageLink).into(holder.img)
        holder.itemView.setOnClickListener{
            val preferences =
                context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
            val uInternal = preferences.getInt("uInternal",0)
            if(uInternal == 0) {//Remove when external Doctors are integrated
                if (currentUser.status == 1) {
                    val intent = Intent(context, ChatActivity::class.java)
                    intent.putExtra("name", currentUser.name)
                    intent.putExtra("uid", currentUser.uid)
                    context.startActivity(intent)
                } else {
                    val preferences =
                        context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
                    val name = preferences.getString("uName", "")
                    CommonMethods.showDialog(
                        this@UserAdapter.context,
                        name.toString() + " tried to connect with you",
                        1
                    )
                }
            }
        }
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtName = itemView.findViewById<TextView>(R.id.name)
        val txtDesignation = itemView.findViewById<TextView>(R.id.designation)
        val txtStatus = itemView.findViewById<TextView>(R.id.status)
        val img = itemView.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.img)
    }
}