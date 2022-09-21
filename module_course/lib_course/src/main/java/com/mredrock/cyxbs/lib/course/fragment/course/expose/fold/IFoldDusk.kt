package com.mredrock.cyxbs.lib.course.fragment.course.expose.fold

import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.course.expose.period.dusk.IDuskPeriod

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:01
 */
interface IFoldDusk : IDuskPeriod {
  
  val viewDuskFold: View
  val viewDuskUnfold: View
  
  /**
   * 得到当前中午那一行的状态
   */
  fun getDuskRowState(): FoldState
  
  /**
   * 带有动画的强制折叠傍晚时间段。如果之前在运行不相同的动画，则会 cancel 掉之前的动画
   * @param duration 传入负数，则自动计算所需动画时间。注意：如果正在运行同样的动画，则该值无效
   */
  fun foldDusk(duration: Long = -1L, onChanged: ((weight: Float, fraction: Float) -> Unit)? = null)
  
  /**
   * 不带动画的立即折叠傍晚时间段。会 cancel 掉之前的动画
   */
  fun foldDuskWithoutAnim()
  
  /**
   * 带有动画的强制展开中午时间段。如果之前在运行不相同的动画，则会 cancel 掉之前的动画
   * @param duration 传入负数，则自动计算所需动画时间。注意：如果正在运行同样的动画，则该值无效
   */
  fun unfoldDusk(duration: Long = -1L, onChanged: ((weight: Float, fraction: Float) -> Unit)? = null)
  
  /**
   * 不带动画的立即展开傍晚时间段。会 cancel 掉之前的动画
   */
  fun unfoldDuskWithoutAnim()
  
  /**
   * 增加折叠监听
   */
  fun addDuskFoldListener(l: OnFoldListener)
}