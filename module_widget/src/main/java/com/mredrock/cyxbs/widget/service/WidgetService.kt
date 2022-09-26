package com.mredrock.cyxbs.widget.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.content.edit
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.affair.IAffairService
import com.mredrock.cyxbs.api.course.ILessonService
import com.mredrock.cyxbs.api.widget.IWidgetService
import com.mredrock.cyxbs.api.widget.WIDGET_SERVICE
import com.mredrock.cyxbs.widget.repo.database.AffairDatabase
import com.mredrock.cyxbs.widget.repo.database.LessonDatabase
import com.mredrock.cyxbs.widget.repo.database.LessonDatabase.Companion.MY_STU_NUM
import com.mredrock.cyxbs.widget.repo.database.LessonDatabase.Companion.OTHERS_STU_NUM
import com.mredrock.cyxbs.widget.util.defaultSp
import com.mredrock.cyxbs.widget.util.getMyLessons
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/**
 * description ： IWidgetService接口的实现类，通过发送延时广播通知小组件刷新
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/8/3 15:22
 */
@Route(path = WIDGET_SERVICE, name = WIDGET_SERVICE)
class WidgetService : IWidgetService {
    private var mContext: Context? = null

    override fun notifyWidgetRefresh(
        myLessons: List<ILessonService.Lesson>,
        otherStuLessons: List<ILessonService.Lesson>,
        affairs: List<IAffairService.Affair>,
    ) {
        LessonDatabase.INSTANCE.getLessonDao().deleteAllLessons()
        AffairDatabase.INSTANCE.getAffairDao().deleteAllAffair()
        //设置两者的学号，用于数据库查询
        if (myLessons.isNotEmpty()) {
            myLessons[0].stuNum.let {
                defaultSp.edit { putString(MY_STU_NUM, it) }
            }
        }
        if (otherStuLessons.isNotEmpty()) {
            defaultSp.edit {
                putString(OTHERS_STU_NUM, otherStuLessons[0].stuNum)
            }
        }
        Observable.create<Int> {
            //将传入的来自api模块数据转化为该模块的对应数据并存入数据库
            getMyLessons(3).size
            LessonDatabase.INSTANCE.getLessonDao()
                .insertLessons(com.mredrock.cyxbs.widget.repo.bean.LessonEntity.convertFromApi(
                    myLessons))
            LessonDatabase.INSTANCE.getLessonDao()
                .insertLessons(com.mredrock.cyxbs.widget.repo.bean.LessonEntity.convertFromApi(
                    otherStuLessons))
            AffairDatabase.INSTANCE.getAffairDao()
                .insertAffairs(com.mredrock.cyxbs.widget.repo.bean.AffairEntity.convert(affairs))
            it.onNext(1)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            //延迟100ms,确保发送广播时已经将数据插入数据库
            .delay(100, TimeUnit.MILLISECONDS).subscribe {
                widgetList.forEach { pkg ->
                    mContext?.sendBroadcast(Intent(actionFlush).apply {
                        component = ComponentName(mContext!!, pkg)
                    })
                }
            }
    }

    override fun deleteAffair(affair: IAffairService.Affair) {
        thread { AffairDatabase.INSTANCE.getAffairDao().deleteAffair(affair.onlyId) }
    }

    override fun init(context: Context?) {
        mContext = context
    }
}

const val actionFlush = "flush"
const val littleWidgetPkg = "com.mredrock.cyxbs.widget.widget.little.LittleWidget"
const val littleWidgetTransPkg = "com.mredrock.cyxbs.widget.widget.little.LittleTransWidget"
const val normalWidget = "com.mredrock.cyxbs.widget.widget.normal.NormalWidget"
const val oversizedAppWidget = "com.mredrock.cyxbs.widget.widget.oversize.OversizedAppWidget"
val widgetList = listOf(
    littleWidgetPkg,
    littleWidgetTransPkg,
    normalWidget,
    oversizedAppWidget
)