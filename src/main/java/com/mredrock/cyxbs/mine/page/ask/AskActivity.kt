package com.mredrock.cyxbs.mine.page.ask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.util.ui.TabPagerAdapter
import kotlinx.android.synthetic.main.mine_activity_question.*
import kotlinx.android.synthetic.main.mine_item_tablayout_with_point.view.*


/**
 * Created by zzzia on 2018/8/14.
 * 问一问
 */
class AskActivity(override val isFragmentActivity: Boolean = false) : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_question)

        mine_activity_question_toolbar.init("")
        mine_activity_question_toolbar_title.text = "我的提问"

        init()
    }

    private fun init() {
        //初始化Fragment
        val askPostedFm = AskPostedFm()

        val askPostedFm2 = AskPostedFm()

        val fragmentList = listOf(askPostedFm, askPostedFm2)
        val titleList = listOf("已发布", "草稿箱")

        //设置viewPager
        mine_question_view_pager.adapter = TabPagerAdapter(supportFragmentManager, fragmentList, titleList)
        mine_question_view_pager.offscreenPageLimit = fragmentList.size

        mine_question_tab_layout.tabMode = TabLayout.MODE_FIXED
        mine_question_tab_layout.setupWithViewPager(mine_question_view_pager)

        //设置tabLayout，隐藏小红点
        for (i in 0 until mine_question_tab_layout.tabCount) {
            val view = LayoutInflater.from(this).inflate(R.layout.mine_item_tablayout_with_point, null)
            view.mine_item_tv_tab_title.text = titleList[i]
            view.mine_item_iv_tab_red.visibility = View.GONE
            mine_question_tab_layout.getTabAt(i)!!.customView = view
        }
    }
}
