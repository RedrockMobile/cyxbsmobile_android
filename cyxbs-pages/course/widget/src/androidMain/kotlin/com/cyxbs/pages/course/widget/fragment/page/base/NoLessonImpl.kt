package com.cyxbs.pages.course.widget.fragment.page.base

import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.cyxbs.pages.course.widget.R
import com.cyxbs.pages.course.widget.item.affair.IAffairItem
import com.cyxbs.pages.course.widget.item.lesson.ILessonItem
import com.cyxbs.pages.course.widget.fragment.page.expose.INoLesson
import com.cyxbs.pages.course.widget.internal.item.IItem
import com.cyxbs.pages.course.widget.internal.item.IItemContainer
import com.cyxbs.components.utils.extensions.gone
import com.cyxbs.components.utils.extensions.visible

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 17:59
 */
@Suppress("LeakingThis")
abstract class NoLessonImpl : CourseDefaultTouchImpl(), INoLesson {
  
  override val viewNoLesson by R.id.course_view_no_lesson.view<View>()
  override val ivNoLesson by R.id.course_iv_no_lesson.view<ImageView>()
  override val tvNoLesson by R.id.course_tv_no_lesson.view<TextView>()
  
  /**
   * 是否隐藏没课图片
   *
   * 每添加一个 item 进课表就会回调该方法来判断是否需要继续显示没课图片
   */
  override fun isHideNoLessonImg(item: IItem): Boolean {
    return item is ILessonItem || item is IAffairItem
  }
  
  override fun showViewNoLesson() {
    if (viewNoLesson.isGone) {
      viewNoLesson.visible()
      viewNoLesson.startAnimation(mFadeInAnim)
    }
  }
  
  override fun hideViewNoLesson() {
    if (viewNoLesson.isVisible) {
      viewNoLesson.gone()
      viewNoLesson.startAnimation(mFadeOutAnim)
    }
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initNoLessonLogic(savedInstanceState)
  }
  
  /**
   * 初始化显示没课图片的逻辑
   */
  private fun initNoLessonLogic(savedInstanceState: Bundle?) {
    course.apply {
      addItemExistListener(
        object : IItemContainer.OnItemExistListener {
          override fun onItemAddedAfter(item: IItem, view: View?) {
            if (view == null) return
            if (isHideNoLessonImg(item)) mExhibitionItemCount++
            tryPostRefreshNoLessonRunnable()
          }
          
          override fun onItemRemovedAfter(item: IItem, view: View?) {
            if (view == null) return
            if (isHideNoLessonImg(item)) mExhibitionItemCount--
            tryPostRefreshNoLessonRunnable()
          }
        }
      )
    }
    /**
     * VP 中 Fragment 离开远了，再划回去的时候会重新加载，
     * 此时如果本来就有 item 显示，会出现没课图片闪动一下的场景，
     * 所以需要保存一下没课图片的显示状况
     */
    if (savedInstanceState != null) {
      if (savedInstanceState.getBoolean("没课图片之前是否显示")) {
        viewNoLesson.visible()
      } else {
        viewNoLesson.gone()
      }
    }
  }
  
  private var mExhibitionItemCount = 0
  
  private var _isInRefreshRunnable = false
  
  /**
   * 尝试发送没课图片显示的 Runnable
   *
   * 因为一次事件中存在先 clear 所有课程和事务，再添加的情况，所以是否显示没课图片需要移动到下一个 Runnable 中执行
   */
  private fun tryPostRefreshNoLessonRunnable() {
    if (!_isInRefreshRunnable) {
      _isInRefreshRunnable = true
      course.post {
        _isInRefreshRunnable = false
        if (mExhibitionItemCount == 0) {
          showViewNoLesson()
        } else {
          hideViewNoLesson()
        }
      }
    }
  }
  
  // 补间动画可以在设置了 gone() 后仍能显示
  private val mFadeInAnim = AlphaAnimation(0F, 1F).apply { duration = 360 }
  private val mFadeOutAnim = AlphaAnimation(1F, 0F).apply { duration = 360 }
  
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putBoolean("没课图片之前是否显示", viewNoLesson.isVisible)
  }
}