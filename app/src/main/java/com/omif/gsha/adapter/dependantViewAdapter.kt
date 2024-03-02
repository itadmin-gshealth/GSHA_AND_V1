package com.omif.gsha.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView


class dependantViewAdapter  // invoke the suitable constructor of the ArrayAdapter class
    (context: Context, arrayList: ArrayList<dependantView>) :
    ArrayAdapter<dependantView?>(context, 0, arrayList!! as List<dependantView?>) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // convertView which is recyclable view
        var currentItemView = convertView

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView =
                LayoutInflater.from(context).inflate(com.omif.gsha.R.layout.dependant_list, parent, false)
        }

        // get the position of the view from the ArrayAdapter
        val currentNumberPosition: dependantView? = getItem(position)

        // then according to the position of the view assign the desired image for the same
        val numbersImage = currentItemView!!.findViewById<ImageView>(com.omif.gsha.R.id.depImgView)
        assert(currentNumberPosition != null)
        numbersImage.setImageResource(currentNumberPosition?.numbersImageId!!)

        // then according to the position of the view assign the desired TextView 1 for the same
        val textView1 = currentItemView.findViewById<TextView>(com.omif.gsha.R.id.depTxtView1)
        textView1.setText(currentNumberPosition?.numberInDigit)

        // then according to the position of the view assign the desired TextView 2 for the same
        val textView2 = currentItemView.findViewById<TextView>(com.omif.gsha.R.id.depTxtView2)
        textView2.setText(currentNumberPosition?.numbersInText)

        // then return the recyclable view
        return currentItemView
    }
}