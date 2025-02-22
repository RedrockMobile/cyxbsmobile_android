package com.cyxbs.pages.noclass.page.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cyxbs.components.utils.extensions.color
import com.cyxbs.pages.noclass.R
import com.cyxbs.pages.noclass.bean.NoClassGroup

class NoClassAddToGroupAdapter(
    private val groupList: List<NoClassGroup>,
    val context: Context,
    val setDoneStatus: (isOk: Boolean, chooseGroup: List<NoClassGroup>) -> Unit
) : RecyclerView.Adapter<NoClassAddToGroupAdapter.MyHolder>() {

    /**
     * 选择分组缓冲区
     */
    private val chooseGroup by lazy { ArrayList<NoClassGroup>() }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val containerView: LinearLayout = itemView.findViewById(R.id.noclass_item_add_to_group)
        private val mImg: ImageView = itemView.findViewById(R.id.noclass_item_add_to_group_img)
        val mTv: TextView = itemView.findViewById(R.id.noclass_item_add_to_group_tv)

        init {
            containerView.setOnClickListener {
                //判断是否选中，选中需要更改背景,同时加入缓冲区
                val noClassGroup = groupList[bindingAdapterPosition]
                if (chooseGroup.contains(noClassGroup)) {
                    //这是是取消选中
                    chooseGroup.remove(noClassGroup)
                    mImg.setImageResource(R.drawable.noclass_ic_unchoose)
                    mTv.setTextColor(R.color.noclass_add_to_group_no_choose_color.color)
                } else {
                    //这里是选中
                    chooseGroup.add(noClassGroup)
                    mImg.setImageResource(R.drawable.noclass_ic_choose_group)
                    mTv.setTextColor(R.color.noclass_primary_text_color.color)
                }
                //判断分组缓冲区是否为空，如果为空，那么就回调传进来的点击事件
                if (chooseGroup.isEmpty()) {
                    setDoneStatus(false, chooseGroup)
                } else {
                    setDoneStatus(true, chooseGroup)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.noclass_item_add_to_group, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.mTv.text = groupList[position].name
    }

}