package com.mredrock.cyxbs.widget.util

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.content.edit
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.main.MAIN_MAIN
import com.mredrock.cyxbs.config.sp.defaultSp
import com.mredrock.cyxbs.lib.base.BaseApp
import com.mredrock.cyxbs.lib.utils.extensions.CyxbsToast
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.widget.repo.bean.Lesson
import com.mredrock.cyxbs.widget.repo.database.LessonDatabase
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by zia on 2018/10/10.
 * 精力憔悴，这些方法直接揉在一起了*/

private const val SP_DayOffset = "dayOffset"

//天数偏移量，用于LittleWidget切换明天课程
fun saveDayOffset(context: Context, offset: Int) {
    defaultSp.edit { putInt(SP_DayOffset, offset) }
}

fun getDayOffset(context: Context): Int {
    return defaultSp.getInt(SP_DayOffset, 0)
}


fun isNight(): Boolean {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.HOUR_OF_DAY) > 19
}

/**
 * hash_lesson == 0 第1节 返回8:00*/


fun getStartCalendarByNum(hash_lesson: Int): Calendar {
    val calendar = Calendar.getInstance()
    when (hash_lesson) {
        0 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 8)
            calendar.set(Calendar.MINUTE, 0)
        }
        1 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 10)
            calendar.set(Calendar.MINUTE, 15)
        }
        2 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 14)
            calendar.set(Calendar.MINUTE, 0)
        }
        3 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 16)
            calendar.set(Calendar.MINUTE, 15)
        }
        4 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 19)
            calendar.set(Calendar.MINUTE, 0)
        }
        5 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 20)
            calendar.set(Calendar.MINUTE, 50)
        }
    }
    return calendar
}

fun getWeekDayChineseName(weekDay: Int): String {
    return when (weekDay) {
        1 -> "周日"
        2 -> "周一"
        3 -> "周二"
        4 -> "周三"
        5 -> "周四"
        6 -> "周五"
        7 -> "周六"
        else -> "null"
    }
}

fun getClickPendingIntent(
    context: Context,
    @IdRes resId: Int,
    action: String,
    clazz: Class<*>,
): PendingIntent {
    val intent = Intent()
    intent.setClass(context, clazz)
    intent.action = action
    intent.data = Uri.parse("id:$resId")

    return PendingIntent.getBroadcast(context, 0, intent, getPendingIntentFlags())
}

//给按钮返回PendingIntent
fun getClickIntent(
    context: Context,
    widgetId: Int,
    viewId: Int,
    requestCode: Int,
    action: String,
    clazz: Class<*>,
): PendingIntent? {
    //pendingintent中需要的intent，绑定这个类和当前context
    val i = Intent(context, clazz)
    //设置action，方便在onReceive中区别点击事件
    i.action = action //设置更新动作
    //设置bundle
    val bundle = Bundle()
    //将widgetId放进bundle
    bundle.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
    //放进需要设置的viewId
    bundle.putInt("Button", viewId)
    i.putExtras(bundle)
    return PendingIntent.getBroadcast(context, requestCode, i, getPendingIntentFlags())
}

fun filterClassRoom(classRoom: String): String {
    return if (classRoom.length > 8) {
        classRoom.replace(Regex("[\\u4e00-\\u9fa5()（）]"), "")
    } else {
        classRoom
    }
}

fun startOperation(lesson: Lesson) {
    if (IAccountService::class.impl.getVerifyService().isLogin()) {
        CyxbsToast.show(BaseApp.baseApp, "请登录之后再点击查看详细信息", Toast.LENGTH_SHORT)
    } else {
/*        ARouter.getInstance().build(MAIN_MAIN).navigation()
//        Todo,此处等郭神提供课表的接口*/
    }
}

fun getLessonByCalendar(context: Context, calendar: Calendar): ArrayList<Lesson>? {
    val weekOfTerm = SchoolCalendar().weekOfTerm
    val myStuNum =
        context.getSharedPreferences(LessonDatabase.MY_STU_NUM, Context.MODE_PRIVATE)
            .getString(LessonDatabase.MY_STU_NUM, "")
    val lesson = LessonDatabase.getInstance(context).getLessonDao()
        .queryAllLessons(myStuNum!!, weekOfTerm)
    if (lesson.isEmpty()) return null
    /*
    * 转换表，老外从周日开始计数,orz
    * 7 1 2 3 4 5 6 老外
    * 1 2 3 4 5 6 7 Calendar.DAY_OF_WEEK
    * 6 0 1 2 3 4 5 需要的结果(hash_day)
    * */
    val hashDay = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7

    val list = ArrayList<Lesson>()
    lesson.forEach {
        if (it.hashDay == hashDay && it.week == weekOfTerm) {
            list.add(it)
        }
    }
//    list.sortBy { it.hash_lesson }
    return list
}

fun getErrorLessonList(): ArrayList<Lesson> {
    val data = Lesson(course = "数据异常，请刷新")
    val list = ArrayList<Lesson>()
    list.add(data)
    return list
}

fun getNoCourse(): Lesson {
    return Lesson(course = "无课")
}

private fun getPendingIntentFlags(isMutable: Boolean = true) =
    when {
        isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        !isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
            PendingIntent.FLAG_UPDATE_CURRENT
        !isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
            PendingIntent.FLAG_CANCEL_CURRENT
        else -> PendingIntent.FLAG_UPDATE_CURRENT
    }

/**获取登录用户的本周所有Lesson,
 * 这里需要传入RemoteViewService的context，因为小组件运行在桌面进程时，可能应用已经被关闭，此时无法获得application作为context*/
fun getMyLessons(context: Context, weekOfTerm: Int): List<Lesson> {
    val myStuNum =
        context.getSharedPreferences(LessonDatabase.MY_STU_NUM, Context.MODE_PRIVATE)
            .getString(LessonDatabase.MY_STU_NUM, "")
    return LessonDatabase.getInstance(context).getLessonDao()
        .queryAllLessons(myStuNum!!, weekOfTerm)
}

/**同上*/
fun getOthersStuNum(context: Context, weekOfTerm: Int): List<Lesson> {
    val othersStuNum =
        context.getSharedPreferences(LessonDatabase.OTHERS_STU_NUM, Context.MODE_PRIVATE)
            .getString(LessonDatabase.OTHERS_STU_NUM, "")
    return LessonDatabase.getInstance(context).getLessonDao()
        .queryAllLessons(othersStuNum!!, weekOfTerm)
}
