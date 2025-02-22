package com.cyxbs.pages.course.page.course.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import com.cyxbs.pages.course.page.course.ui.home.utils.AffairManager
import com.cyxbs.pages.course.page.course.ui.home.utils.PageFragmentHelper
import com.cyxbs.pages.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.cyxbs.pages.course.page.course.utils.container.AffairContainerProxy
import com.cyxbs.pages.course.page.course.utils.container.LinkLessonContainerProxy
import com.cyxbs.pages.course.page.course.utils.container.SelfLessonContainerProxy
import com.cyxbs.pages.course.widget.fragment.page.CourseWeekFragment
import com.cyxbs.pages.course.widget.helper.affair.expose.ITouchAffairItem
import com.cyxbs.pages.course.widget.internal.item.IItem

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/20 19:25
 */
class HomeWeekFragment : CourseWeekFragment(), IHomePageFragment {
  
  companion object {
    fun newInstance(week: Int): HomeWeekFragment {
      return HomeWeekFragment().apply {
        arguments = bundleOf(
          this::mWeek.name to week
        )
      }
    }
  }
  
  override val mWeek by arguments<Int>()
  
  // 供外部调用
  override val week: Int
   get() = mWeek
  
  override val parentViewModel by createViewModelLazy(
    HomeCourseViewModel::class,
    { requireParentFragment().viewModelStore }
  )
  
  // 大部分的初始化方法在这里面
  private val mPageFragmentHelper = PageFragmentHelper<HomeWeekFragment>()
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    // 因为 HomeSemesterFragment 与 HomeWeekFragment 很多逻辑一样，所以统一放在这里面初始化
    mPageFragmentHelper.init(this, savedInstanceState)
    initObserve()
  }
  
  /**
   * 是否显示关联人，但需要点击一次关联人图标，才会激活该变量
   * 初始值为 null 是表示没有点击过关联人图标
   */
  private var mIsShowLinkEventAfterClick: Boolean? = null
  
  override val selfLessonContainerProxy = SelfLessonContainerProxy(this)
  override val linkLessonContainerProxy = LinkLessonContainerProxy(this)
  override val affairContainerProxy = AffairContainerProxy(this, AffairManager(this))
  
  private fun initObserve() {
    parentViewModel.showLinkEvent
      .collectLaunch {
        mIsShowLinkEventAfterClick = it
      }
    
    parentViewModel.homeWeekData
      .map { it[mWeek] ?: HomeCourseViewModel.HomePageResult }
      .distinctUntilChanged()
      .observe {
        affairContainerProxy.diffRefresh(it.affair)
        selfLessonContainerProxy.diffRefresh(it.self)
        linkLessonContainerProxy.diffRefresh(it.link) {
          if (mIsShowLinkEventAfterClick == true
            && parentViewModel.currentItem.value == mWeek
            && it.link.isNotEmpty()
          ) {
            // 这时说明触发了关联人的显示，需要开启入场动画
            linkLessonContainerProxy.startAnimation()
          }
        }
      }
  }
  
  override fun isHideNoLessonImg(item: IItem): Boolean {
    return super.isHideNoLessonImg(item) || item is ITouchAffairItem
  }
}