package com.mredrock.cyxbs.common

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.utils.CrashHandler
import com.mredrock.cyxbs.common.utils.extensions.getDarkModeStatus


/**
 * Created By jay68 on 2018/8/7.
 */
open class BaseApp : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak", "CI_StaticFieldLeak")
        lateinit var context: Context
            private set

        //当前是不是黑夜模式
        val isNightMode: Boolean
            get() = context.getDarkModeStatus()

        const val foregroundService = "foreground"
        var time = 0L
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        context = base
        time = System.currentTimeMillis()
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
        InitService.init(applicationContext)
        initRouter()//ARouter放在子线程会影响使用
        //不用放到service里面，只是debug会用到，
        //而且这是轻量级操作，不会对启动速度造成太大的影响
        CrashHandler.init(applicationContext)
    }

    private fun initRouter() {
        if (BuildConfig.DEBUG) {
            ARouter.openDebug()
            ARouter.openLog()
        }
        ARouter.init(this)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "掌上重邮"
            val importance = NotificationManager.IMPORTANCE_MIN
            val channel = NotificationChannel(foregroundService, name, importance)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}