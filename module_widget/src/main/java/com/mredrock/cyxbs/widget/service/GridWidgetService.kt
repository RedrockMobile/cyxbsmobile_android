package com.mredrock.cyxbs.widget.service

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.mredrock.cyxbs.lib.base.BaseApp
import com.mredrock.cyxbs.widget.R
import com.mredrock.cyxbs.widget.repo.bean.Affair
import com.mredrock.cyxbs.widget.repo.bean.Lesson
import com.mredrock.cyxbs.widget.repo.database.AffairDatabase
import com.mredrock.cyxbs.widget.util.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 */
class GridWidgetService : RemoteViewsService() {

    companion object {
        private lateinit var myLessons: List<Lesson>
        private lateinit var otherLessons: Array<Array<Lesson?>>
        private lateinit var affairs: Array<Array<Affair?>>

        fun getLesson(position: Int): Lesson? {
            val mPosition = position - 7
            return makeSchedules(BaseApp.baseApp).let { it[mPosition / 7][mPosition % 7] }
        }

        private fun makeSchedules(context: Context): Array<Array<Lesson?>> {
            val weekOfTerm = SchoolCalendar().weekOfTerm
            myLessons = getMyLessons(context, weekOfTerm)
            otherLessons = getOthersStuNum(context, weekOfTerm).schedule()
            affairs =
                AffairDatabase.getInstance(BaseApp.baseApp).getAffairDao()
                    .queryAllAffair(weekOfTerm).schedule()
            return myLessons.schedule()
        }

        private fun List<Lesson>.schedule(): Array<Array<Lesson?>> {
            val mScheduleLessons = Array(6) { arrayOfNulls<Lesson>(7) }
            for (i in 0 until size) {
                get(i).let {
                    val row = it.beginLesson / 2 + 1
                    val column = it.hashDay
                    mScheduleLessons[row][column] = it
                    if (it.period == 3) {
                        mScheduleLessons[row + 1][column] = it.apply { period = 1 }
                    } else if (it.period == 4) {
                        mScheduleLessons[row + 1][column] = it
                    }
                }
            }
            return mScheduleLessons
        }

        private fun List<Affair>.schedule(): Array<Array<Affair?>> {
            val mScheduleAffairs = Array(6) { arrayOfNulls<Affair>(7) }
            for (i in 0 until size) {
                get(i).let {
                    val row = it.beginLesson / 2 + 1
                    val column = it.day
                    mScheduleAffairs[row][column] = it
                }
            }
            return mScheduleAffairs
        }
    }

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return GridRemoteViewsFactory(this, intent)
    }

    private inner class GridRemoteViewsFactory(val mContext: Context, val intent: Intent) :
        RemoteViewsFactory {

        private var mScheduleLessons: Array<Array<Lesson?>>? = null

        override fun getViewAt(position: Int): RemoteViews? {
            //获取当前时间
            val time = intent.getIntExtra("time", 0)
            //如果是前7个说明是显示今天是星期几
            if (position < 7) {
                //获取当前item是否应该高亮
                val id = if (time == position)
                    R.layout.widget_grid_view_day_of_week_selected_item
                else R.layout.widget_grid_view_day_of_week_item
                //返回对应的item
                return RemoteViews(mContext.packageName, id).apply {
                    setTextViewText(R.id.tv_day_of_week, "周${
                        if (position != 6) Num2CN.number2ChineseNumber((position + 1).toLong()) else "日"
                    }")
                }
            }
            val remoteViews = mScheduleLessons?.let { mSchedulesArray ->
                val mPosition = position - 7
                val lesson = mSchedulesArray[mPosition / 7][mPosition % 7]
                if (lesson == null) {
                    if (affairs[mPosition / 7][mPosition % 7] != null)
                    /** 如果是事务，设置事务的背景*/
                        RemoteViews(mContext.packageName, R.layout.widget_grid_view_item_affair)
                    else
                    /** 无事务也无课*/
                        RemoteViews(mContext.packageName, R.layout.widget_grid_view_item_blank)
                } else {
                    val remoteViews: RemoteViews =
                        when {
                            /**根据早上，下午，晚上的课设置不同背景*/
                            mPosition < 14 && lesson.period == 1 -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_item_half_moring)
                            mPosition < 14 -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_moring_item)
                            mPosition < 28 && lesson.period == 1 -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_item_half_afternoon)
                            mPosition < 28 -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_afternoon_item)
                            lesson.period == 1 -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_item_half_night)
                            else -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_night_item)
                        }
                    /**如果有事务，则在上面添加小红点*/
                    remoteViews.setViewVisibility(R.id.affair, View.GONE)
                    if (affairs[mPosition / 7][mPosition % 7] != null) {
                        remoteViews.setViewVisibility(R.id.affair, View.VISIBLE)
                    }
                    remoteViews.setTextViewText(R.id.top, lesson.course)
                    remoteViews.setTextViewText(R.id.bottom,
                        ClassRoomParse.parseClassRoom(lesson.classroom))
                    remoteViews.setOnClickFillInIntent(R.id.background,
                        Intent().putExtra("POSITION", position))
                    return remoteViews
                }
            }
            return remoteViews
        }

        override fun onCreate() {
            /**初始化GridView的数据*/
            Observable.just(0).observeOn(Schedulers.io())
                .subscribe { mScheduleLessons = makeSchedules(mContext) }
        }

        override fun getCount(): Int { // 返回“集合视图”中的数据的总数
            return 7 * 7
        }

        override fun getItemId(position: Int): Long { // 返回当前项在“集合视图”中的位置
            return position.toLong()
        }

        override fun getLoadingView() =
            RemoteViews(mContext.packageName, R.layout.widget_grid_view_item_blank)

        override fun getViewTypeCount() = 10

        override fun hasStableIds() = true

        override fun onDataSetChanged() {}
        override fun onDestroy() {}
    }
}