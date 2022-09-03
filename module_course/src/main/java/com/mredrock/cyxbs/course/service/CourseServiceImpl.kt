package com.mredrock.cyxbs.course.service

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.course.COURSE_SERVICE
import com.mredrock.cyxbs.api.course.ICourseService
import com.mredrock.cyxbs.course.page.course.ui.home.HomeCourseVpFragment

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/4 15:31
 */
@Route(path = COURSE_SERVICE, name = COURSE_SERVICE)
class CourseServiceImpl : ICourseService {
  
  override fun replaceHomeFragmentById(fm: FragmentManager, id: Int) {
    val fragment = findFragment<HomeCourseVpFragment>(fm, id)
    if (fragment == null) {
      fm.commit { replace(id, HomeCourseVpFragment()) }
    }
    /*
    * 由于 HomeCourseFragment 的 ViewModel 观察了当前登录人的学号，
    * 会自动根据不同登录人发送不同的数据，
    * 所以这里没必要去通知 HomeCourseFragment 刷新数据
    * */
  }
  
  override fun replaceStuCourseFragmentById(
    fm: FragmentManager,
    id: Int,
    arg: ICourseService.ICourseArgs
  ) {
  }
  
  override fun replaceStuCourseFragmentById(
    fm: FragmentManager,
    id: Int,
    args: List<ICourseService.ICourseArgs>
  ) {
  
  }
  
  override fun replaceTeaCourseFragmentById(
    fm: FragmentManager,
    id: Int,
    arg: ICourseService.ICourseArgs
  ) {
  }
  
  override fun init(context: Context) {
  }
  
  private inline fun <reified F: Fragment> findFragment(
    fm: FragmentManager,
    id: Int
  ): F? {
    val fragment = fm.findFragmentById(id)
    if (fragment != null) {
      if (fragment is F) {
        return fragment
      }
    }
    return null
  }
}