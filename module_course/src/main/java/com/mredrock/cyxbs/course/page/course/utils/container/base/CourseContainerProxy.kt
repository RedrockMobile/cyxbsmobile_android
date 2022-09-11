package com.mredrock.cyxbs.course.page.course.utils.container.base

import androidx.collection.arraySetOf
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/11 17:28
 */
@Suppress("UNCHECKED_CAST")
abstract class CourseContainerProxy<Item, Data : Any>(
  val course: ICourseViewGroup
) : BaseDiffRefresh<Data>() where Item : IItem, Item : IDataOwner<Data> {
  
  // 旧数据与 item 的 map
  private val mOldDataMap = hashMapOf<Data, Item>()
  
  // 没有使用的 item 池子
  private val mFreePool = arraySetOf<Item>()
  
  init {
    // 这里需要以添加监听的方式来修改 mOldDataMap
    // 因为 item 存在被其他地方移除的情况
    course.addItemExistListener(
      object : IItemContainer.OnItemExistListener {
        override fun onItemAddedAfter(item: IItem) {
          if (checkItem(item)) {
            item as Item
            val data = item.data
            if (!mOldDataMap.contains(data)) {
              // 不包含的时候说明是通过其他方式添加进来的
              mOldDataMap[data] = item
              addNewData(data)
            }
          }
        }
      
        override fun onItemRemovedAfter(item: IItem) {
          if (checkItem(item)) {
            item as Item
            // 当你使用其他方式移除时
            val data = item.data
            if (mOldDataMap.remove(data) != null) {
              // 移除成功说明是通过其他方式移除的
              mFreePool.add(item)
              removeData(data)
            }
          }
        }
      }
    )
  }
  
  abstract fun checkItem(item: IItem): Boolean
  
  abstract fun addItem(item: Item)
  
  abstract fun removeItem(item: Item)
  
  abstract fun newItem(data: Data): Item
  
  final override fun onInserted(newData: Data) {
    val item = newData.getFreeAffair()
    mOldDataMap[newData] = item
    addItem(item)
  }
  
  final override fun onRemoved(oldData: Data) {
    val item = mOldDataMap[oldData]
    if (item != null) {
      mOldDataMap.remove(oldData)
      mFreePool.add(item)
      // 这个需要放在 mOldDataMap.remove() 和 mFreePool.add() 后
      // 因为在 onItemRemovedAfter() 回调中会再次 remove() 和 add()
      removeItem(item)
    }
  }
  
  final override fun onChanged(oldData: Data, newData: Data) {
    val item = mOldDataMap[oldData]
    item?.setData(newData)
  }
  
  private fun Data.getFreeAffair(): Item {
    if (mFreePool.isEmpty()) {
      return newItem(this)
    }
    val last = mFreePool.removeAt(mFreePool.size - 1)
    last.setData(this)
    return last
  }
}