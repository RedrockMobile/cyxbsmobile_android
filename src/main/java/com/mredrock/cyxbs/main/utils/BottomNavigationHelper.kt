package com.mredrock.cyxbs.main.utils

import android.animation.ValueAnimator
import android.widget.CheckedTextView
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @author  Jovines
 * @date  2020/4/6 18:18
 * description：底部导航栏，这里没有用nav为了方便以后增加或者更改
 */
class BottomNavigationHelper(private val tabList: Array<CheckedTextView>,
                             private val selectIcons: Array<Int>,
                             private val unSelectIcons: Array<Int>,
                             private var selectedListener: ((Int) -> Unit)? = null
) {

    //前一个选中的位置
    var peeCheckedItemPosition = 0

    init {
        for ((i, value) in tabList.withIndex()) {
            value.onClick {
                if (!value.isChecked) {
                    value.toggle()
                }
                if (value.isChecked) {
                    selectTab(i)
                }
            }
            value.setCompoundDrawablesWithIntrinsicBounds(0, unSelectIcons[i], 0, 0)
        }
    }


    /**
     * 清楚其他tab德点击状态
     * @param i 当前点击德tab是哪个
     */
    private fun removeOtherCheckStatus(i: Int) {
        for ((p, value) in tabList.withIndex()) {
            if (p != i) {
                value.isChecked = false
                value.scaleX = 1f
                value.scaleY = 1f
                value.setCompoundDrawablesWithIntrinsicBounds(0, unSelectIcons[p], 0, 0)
            }
        }
    }

    fun selectTab(i: Int) {
        //将当前tab点击状态设置为true
        tabList[i].isChecked = true
        //增加点击动画
        ValueAnimator.ofFloat(1f,1.1f).apply {
            duration = 400
            addUpdateListener {
                tabList[i].scaleX = it.animatedValue as Float
                tabList[i].scaleY = it.animatedValue as Float
            }
        }.start()
        //清除其他tab状态
        removeOtherCheckStatus(i)
        //设置图片
        tabList[i].setCompoundDrawablesWithIntrinsicBounds(0, selectIcons[i], 0, 0)
        //调用回调
        selectedListener?.invoke(i)
        //上一个tab是哪个
        peeCheckedItemPosition = i
    }
}