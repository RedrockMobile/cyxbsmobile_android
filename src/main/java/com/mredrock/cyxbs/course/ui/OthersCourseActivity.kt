package com.mredrock.cyxbs.course.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.JToolbar
import com.mredrock.cyxbs.common.config.COURSE_OTHER_COURSE
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.event.TabIsFoldEvent
import com.mredrock.cyxbs.course.event.WeekNumEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = COURSE_OTHER_COURSE)
class OthersCourseActivity : BaseActivity() {

    companion object {
        const val OTHERS_STU_NUM = "others_stu_num"
    }

    @Autowired(name = "stuNum")
    @JvmField
    var mOthersStuNum: String = ""

    override val isFragmentActivity: Boolean
        get() = true

    private lateinit var toolbar:JToolbar

    // 用于记录当前Toolbar要显示的字符串
    private lateinit var mToolbarTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course_activity_others_course)
        common_toolbar.init("同学课表")
        toolbar = common_toolbar
        ARouter.getInstance().inject(this)
        replaceFragment(CourseContainerFragment.getOthersCourseContainerFragment(mOthersStuNum))

    }


    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl, fragment)
        transaction.commit()
    }

    /**
     * 在[CourseContainerFragment]中的ViewPager的Page发生了变化后进行接收对应的事件来对[mToolbar]的标题进行
     * 修改
     *
     * @param weekNumEvent 包含当前周字符串的事件。
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getWeekNumFromFragment(weekNumEvent: WeekNumEvent) {
        mToolbarTitle = weekNumEvent.weekString
        setToolbar()
    }

    private fun setToolbar() {
        // 设置当前Toolbar的内容
        toolbar.titleTextView.text = mToolbarTitle
    }
}
