package com.cyxbs.pages.map.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cyxbs.pages.map.R
import com.cyxbs.components.utils.extensions.pressToZoomOut

class DetailAttributeRvAdapter(val context: Context, private val mList: MutableList<String>) : RecyclerView.Adapter<DetailAttributeRvAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tag: TextView =view.findViewById<TextView>(R.id.map_tv_recycle_item_detail_attribute).apply { pressToZoomOut() }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.map_recycle_item_detail_attribute, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tag.text = mList[position]
    }


    fun setList(list: List<String>) {
        mList.clear()
        mList.addAll(list)
    }

}