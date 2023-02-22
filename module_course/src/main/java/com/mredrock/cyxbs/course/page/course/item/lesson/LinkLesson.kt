package com.mredrock.cyxbs.course.page.course.item.lesson

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.animation.Animation
import com.mredrock.cyxbs.api.course.utils.parseClassRoom
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.course.data.ICourseItemData
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.item.BaseItem
import com.mredrock.cyxbs.course.page.course.item.lesson.lp.LinkLessonLayoutParams
import com.mredrock.cyxbs.course.page.course.item.view.IOverlapTag
import com.mredrock.cyxbs.course.page.course.item.view.OverlapTagHelper
import com.mredrock.cyxbs.course.page.course.utils.container.base.IDataOwner
import com.mredrock.cyxbs.course.page.course.utils.container.base.IRecycleItem
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableItemHelperConfig
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableListener
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.LocationUtil
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.MovableItemHelper
import com.mredrock.cyxbs.lib.course.item.view.ItemView
import com.mredrock.cyxbs.lib.utils.extensions.color

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:42
 */
class LinkLesson(private var lessonData: StuLessonData) :
  BaseItem<LinkLesson.LinkLessonView>(),
  IDataOwner<StuLessonData> ,
  ILessonItem,
  IRecycleItem,
  ITouchItem
{
  
  override fun setNewData(newData: StuLessonData) {
    getChildIterable().forEach {
      if (it is LinkLessonView) {
        it.setNewData(newData)
      }
    }
    lp.setNewData(newData)
    lessonData = newData
  }
  
  override fun createView(context: Context): LinkLessonView {
    return LinkLessonView(context, lessonData)
  }
  
  override val isHomeCourseItem: Boolean
    get() = true
  
  /**
   * 开启动画
   */
  fun startAnimation(anim: Animation) {
    getChildInParent().forEach {
      it.startAnimation(anim)
    }
  }
  
  @SuppressLint("ViewConstructor")
  class LinkLessonView(
    context: Context,
    override var data: StuLessonData
  ) : ItemView(context), IOverlapTag, IDataOwner<StuLessonData> {
    
    private val mBgColor = R.color.course_link_lesson_bg.color
    private val mTextColor = R.color.course_link_lesson_tv.color
    
    private val mHelper = OverlapTagHelper(this)
  
    override fun onDraw(canvas: Canvas) {
      super.onDraw(canvas)
      mHelper.drawOverlapTag(canvas)
    }
  
    override fun setIsShowOverlapTag(isShow: Boolean) {
      mHelper.setIsShowOverlapTag(isShow)
    }
  
    init {
      setNewData(data)
      tvTitle.setTextColor(mTextColor)
      tvContent.setTextColor(mTextColor)
      setCardBackgroundColor(mBgColor)
      mHelper.setOverlapTagColor(mTextColor)
    }
  
    override fun setNewData(newData: StuLessonData) {
      data = newData
      setText(data.course.course, parseClassRoom(data.course.classroom))
    }
  }
  
  override fun onRecycle(): Boolean {
    return true
  }
  
  override fun onReuse(): Boolean {
    val view = getView() ?: return true
    return view.run {
      parent == null && !isAttachedToWindow
    }
  }
  
  override val rank: Int
    get() = lp.rank
  override val iCourseItemData: ICourseItemData
    get() = lessonData
  
  override val lp: LinkLessonLayoutParams = LinkLessonLayoutParams(lessonData)
  override val week: Int
    get() = lessonData.week
  
  override val data: StuLessonData
    get() = lessonData
  
  override fun initializeTouchItemHelper(): List<ITouchItemHelper> {
    return super.initializeTouchItemHelper() + listOf(
      MovableItemHelper(
        object : IMovableItemHelperConfig by IMovableItemHelperConfig {
          override fun isMovableToNewLocation(
            parent: ICourseViewGroup, item: ITouchItem,
            child: View, newLocation: LocationUtil.Location
          ): Boolean {
            return false // 课程不能移动到新位置
          }
        }
      ).apply {
        addMovableListener(
          object : IMovableListener {
            override fun onLongPressStart(
              page: ICoursePage, item: ITouchItem, child: View,
              initialX: Int, initialY: Int, x: Int, y: Int
            ) {
              super.onLongPressStart(page, item, child, initialX, initialY, x, y)
              page.changeOverlap(this@LinkLesson, false) // 暂时取消重叠
            }
        
            override fun onOverAnimStart(
              newLocation: LocationUtil.Location?,
              page: ICoursePage, item: ITouchItem, child: View
            ) {
              super.onOverAnimEnd(newLocation, page, item, child)
              page.changeOverlap(this@LinkLesson, true) // 恢复重叠
            }
          }
        )
      }
    )
  }
}