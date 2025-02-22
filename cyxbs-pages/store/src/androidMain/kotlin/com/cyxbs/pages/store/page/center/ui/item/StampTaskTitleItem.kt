package com.cyxbs.pages.store.page.center.ui.item

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cyxbs.pages.store.R
import com.cyxbs.pages.store.utils.SimpleRvAdapter

/**
 * 自己写了个用于解耦不同的 item 的 Adapter 的封装类, 详情请看 [SimpleRvAdapter]
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/8/9
 */
class StampTaskTitleItem(
  titleMap: Map<Int, String>
) : SimpleRvAdapter.VHItem<StampTaskTitleItem.StampTaskMoreVH, String>(
  titleMap, R.layout.store_recycler_item_stamp_task_title
) {
  
  /**
   * 该方法调用了 [diffRefreshAllItemMap] 用于自动刷新
   *
   * 因为我在 Item 中整合了 DiffUtil 自动刷新, 只有你全部的 Item 都调用了 [diffRefreshAllItemMap],
   * 就会自动启动 DiffUtil
   */
  fun resetData(titleMap: Map<Int, String>) {
    diffRefreshAllItemMap(
      titleMap,
      isSameName = { oldData, newData ->
        oldData == newData
      }, isSameData = { oldData, newData ->
        oldData == newData
      })
  }
  
  class StampTaskMoreVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvTitle: TextView = itemView.findViewById(R.id.store_tv_stamp_task_title)
  }
  
  override fun getNewViewHolder(itemView: View): StampTaskMoreVH {
    return StampTaskMoreVH(itemView)
  }
  
  override fun onCreate(holder: StampTaskMoreVH, map: Map<Int, String>) {
  }
  
  override fun onRefactor(holder: StampTaskMoreVH, position: Int, value: String) {
    holder.tvTitle.text = value
  }
}