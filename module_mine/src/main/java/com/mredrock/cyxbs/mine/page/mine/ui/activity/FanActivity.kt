package com.mredrock.cyxbs.mine.page.mine.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.account.IUserService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.mine.adapter.FanPagerAdapter
import com.mredrock.cyxbs.mine.page.mine.ui.fragment.FollowFragment
import com.mredrock.cyxbs.mine.page.mine.ui.fragment.FanFragment
import kotlinx.android.synthetic.main.mine_activity_fan.*

/**
 * @class
 * @author YYQF
 * @data 2021/9/16
 * @description
 **/
class FanActivity : BaseActivity() {
    private var redId = "";

    private val userService: IUserService by lazy {
        ServiceManager.getService(IAccountService::class.java).getUserService()
    }

    companion object {
        fun activityStart(activity: Activity, redid: String) {
            activity.startActivity(Intent(activity, FanActivity::class.java).apply {
                putExtra("redid", redid)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_fan)
        redId = intent.getStringExtra("redid")
        initPager()
    }

    private fun initPager() {
        mine_fan_vp2.apply {
            val bundle = Bundle().apply { putString("redid",redId) }
            adapter = FanPagerAdapter(this@FanActivity).apply {
                addFragment(FanFragment().apply {
                    arguments = bundle
                })
                addFragment(FollowFragment().apply {
                    arguments = bundle
                })
            }
        }

        TabLayoutMediator(mine_fan_tl, mine_fan_vp2) { tab, position ->
            when (position) {
                0 -> tab.text =
                    if (redId == userService.getRedid()) "我的粉丝"
                    else "Ta的粉丝"
                1 -> tab.text =
                    if (redId == userService.getRedid()) "我的关注"
                    else "Ta的关注"
            }
        }.attach()
    }
}