package com.example.lifesaviour

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class RequestBloodrecyclerviewAdapter(var context: Context) : RecyclerView.Adapter<RequestBloodrecyclerviewAdapter.MyViewHolder>() {
    var postLists: MutableList<requestDataModel> = ArrayList()

    val lastItemId: String?
    get() = postLists[postLists.size -1].BloodUnitsID

    fun addAll(newRequestBlood: List<requestDataModel>) {
        val init = postLists.size
        postLists.addAll(newRequestBlood)
        notifyItemRangeChanged(init, newRequestBlood.size)
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var text_Name: TextView = itemView.findViewById(R.id.reqstUser)
        var text_bloodgroup: TextView = itemView.findViewById(R.id.targetBG)
        var text_Details: TextView = itemView.findViewById(R.id.reqstDetails)
        var text_cn: TextView = itemView.findViewById(R.id.targetCN)
        var text_loc: TextView = itemView.findViewById(R.id.reqstLocation)
        var text_date: TextView = itemView.findViewById(R.id.posted_date)
        var text_bg: TextView = itemView.findViewById(R.id.reqstBG)

        var make_call : ImageButton = itemView.findViewById(R.id.call_button)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val listitem: View = LayoutInflater.from(context).inflate(R.layout.row_data_request_blood_list, viewGroup, false)
        return MyViewHolder(listitem)

    }

    override fun onBindViewHolder(postHolder: MyViewHolder, position: Int) {

        postHolder.text_Name.text = postLists[position].Name
        postHolder.text_bloodgroup.text = postLists[position].Blood_Units
        postHolder.text_Details.text = postLists[position].Details
        postHolder.text_cn.text = postLists[position].Contact_No
        postHolder.text_loc.text = postLists[position].Location
        postHolder.text_date.text = postLists[position].Posted_Date
        postHolder.text_bg.text = postLists[position].Blood_Group

        postHolder.make_call.setOnClickListener{
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel: " + postHolder.text_cn.text)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return postLists.size
    }

}
