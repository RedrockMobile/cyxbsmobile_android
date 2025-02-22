package com.cyxbs.pages.discover.pages.discover.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cyxbs.pages.discover.R
import com.cyxbs.components.utils.extensions.pressToZoomOut
import com.cyxbs.components.utils.extensions.setOnSingleClickListener


/**
 * Created by yyfbe, Date on 2020/8/14.
 */
class DiscoverMoreFunctionRvAdapter(
    private val picUrls: List<Int>,
    private val texts: List<String>,
    private val onItemClick: (pos: Int) -> Unit
) : RecyclerView.Adapter<DiscoverMoreFunctionRvAdapter.MoreFunctionViewHolder>() {


    class MoreFunctionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val functionTextView: TextView = itemView.findViewById(R.id.tv_discover_function)
        val functionImageView: ImageView = itemView.findViewById(R.id.iv_discover_function)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreFunctionViewHolder {
        return MoreFunctionViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.discover_item_more_function, parent, false)
        )
    }

    override fun getItemCount(): Int = picUrls.size


    override fun onBindViewHolder(holder: MoreFunctionViewHolder, position: Int) {
        holder.itemView.setOnSingleClickListener {
            it.pressToZoomOut()
            onItemClick(position)
        }
        holder.functionImageView.setImageResource(picUrls[position])
        holder.functionTextView.text = texts[position]
    }

}