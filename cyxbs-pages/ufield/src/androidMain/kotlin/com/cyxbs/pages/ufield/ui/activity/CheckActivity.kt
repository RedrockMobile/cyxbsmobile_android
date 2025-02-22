package com.cyxbs.pages.ufield.ui.activity

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.cyxbs.components.base.ui.BaseActivity
import com.cyxbs.components.utils.adapter.FragmentVpAdapter
import com.cyxbs.pages.ufield.R
import com.cyxbs.pages.ufield.ui.fragment.checkfragment.DoneFragment
import com.cyxbs.pages.ufield.ui.fragment.checkfragment.TodoFragment

/**
 * description ：审核中心的activity
 * author : lytMoon
 * email : yytds@foxmail.com
 * date :  2023/8/8 16:34
 * version: 1.0
 */
class CheckActivity : BaseActivity() {

    private val mTabLayout: TabLayout by R.id.uField_check_tab_layout.view()
    private val mVp: ViewPager2 by R.id.uField_check_view_pager.view()
    private val mBack: ImageView by R.id.uField_check_back.view()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ufield_activity_check)
        iniTab()
        iniBack()
    }


    /**
     * 初始化tabLayout
     */
    private fun iniTab() {
        mVp.adapter = FragmentVpAdapter(this)
            .add { TodoFragment() }
            .add { DoneFragment() }
        TabLayoutMediator(mTabLayout, mVp) { tab, position ->
            when (position) {
                0 -> tab.text = "待审核"
                1 -> tab.text = "已处理"
            }
        }.attach()

        //设置tab的左右间距
        for (i in 0 until mTabLayout.tabCount) {
            val tabView = (mTabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val params = tabView.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(75, params.topMargin, 75, params.bottomMargin)
            tabView.layoutParams = params
            tabView.requestLayout()
        }
    }

    /**
     * 处理返回监听事件
     */
    private fun iniBack() {
        mBack.apply { setOnClickListener { finish() } }
    }


}