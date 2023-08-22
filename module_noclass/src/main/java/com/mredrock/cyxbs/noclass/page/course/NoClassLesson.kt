package com.mredrock.cyxbs.noclass.page.course

import android.content.Context
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mredrock.cyxbs.api.affair.DateJson
import com.mredrock.cyxbs.lib.course.item.lesson.BaseLessonLayoutParams
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.page.ui.dialog.NoClassGatherDialog

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.item
 * @ClassName:      NoClassLesson
 * @Author:         Yan
 * @CreateDate:     2022年09月06日 17:23:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */
class NoClassLesson(
  val data: NoClassLessonData,
  private val mNumNameIsSpare : HashMap<Pair<String,String>,Boolean>,
  private val mLastingTime : Pair<Int,Int>,
  private val mWeek : Int  //第几周
) : ILessonItem{
  
  override fun initializeView(context: Context): View {   //添加item到课表中
    return NoClassLesson.newInstance(context,data ,mNumNameIsSpare ,mLastingTime,mWeek)
  }
  
  override val lp: BaseLessonLayoutParams
    get() = NoClassLessonLayoutParams(data).apply {
      leftMargin = 1.2F.dp2px
      rightMargin = 1.2F.dp2px
      topMargin = 1.2F.dp2px
      bottomMargin = 1.2F.dp2px
    }
  // 星期几
  override val weekNum: Int
    get() = data.weekNum
  override val startNode: Int
    get() = data.startNode
  override val length: Int
    get() = data.length
  
  private class NoClassLesson private constructor(
    context: Context
  ) : NoClassItemView(context){
    companion object {
      fun newInstance(context: Context, data: NoClassLessonData,mNumNameIsSpare : HashMap<Pair<String,String>,Boolean> ,mLastingTime : Pair<Int,Int>,mWeek: Int): NoClassLesson {
        return NoClassLesson(context).apply {
          val sparePeopleNameList = mNumNameIsSpare.filter { it.value }.keys.map { it.second }
          val noSparePeopleNameList = mNumNameIsSpare.filter { !it.value }.keys.map { it.second }
          val busyMode = if (noSparePeopleNameList.isEmpty()){
            // 如果没有忙碌的人，那么NAN
            BusyMode.NAN
          }else if (sparePeopleNameList.isEmpty()){
            BusyMode.ALL
          }else if (sparePeopleNameList.size > noSparePeopleNameList.size){
            BusyMode.LESS
          }else{
            BusyMode.MORE
          }
          setColor(busyMode)
          setText(names = data.names,height = data.length)
          setOnClickListener {
            val duration = mLastingTime.second - mLastingTime.first
            //开始与结束序列
            val begin = mLastingTime.first
            val end = mLastingTime.second - 1
            
            val beginTime = com.mredrock.cyxbs.api.course.utils.getShowStartTimeStr(begin)
            val endTime = com.mredrock.cyxbs.api.course.utils.getShowEndTimeStr(end)
            
            val beginLesson =  if(mLastingTime.first >= 10) mLastingTime.first - 1 else if (mLastingTime.first<=3) mLastingTime.first + 1 else mLastingTime.first

            val month = when(data.weekNum){
               1 -> " 周一"
               2 -> " 周二"
               3 -> " 周三"
               4 -> " 周四"
               5 -> " 周五"
               6 -> " 周六"
               7 -> " 周日"
              else -> ""
            }
            val textTime = "时间：${month} ${beginLesson}-${beginLesson + duration - 1} ${beginTime}-${endTime}"
            //todo waiting
            val dateJson = DateJson(beginLesson,data.weekNum,duration,mWeek)
            Log.d("lx", "beginLesson: = ${beginLesson} ")
            Log.d("lx", "data.weekNum: = ${data.weekNum} ")
            Log.d("lx", "duration: = ${duration} ")
            Log.d("lx", "mWeek: = ${mWeek} ")
            NoClassGatherDialog(dateJson,mNumNameIsSpare, textTime).show((context as AppCompatActivity).supportFragmentManager, "NoClassGatherDialog")
          }
        }
      }
    }
  

    private val mAmBgColor = R.color.noclass_course_am_lesson_color.color
    private val mPmBgColor = R.color.noclass_course_pm_lesson_color.color
    private val mNightBgColor = R.color.noclass_course_night_lesson_color.color
    private val mNoonBgColor = R.color.noclass_course_noon_lesson_color.color
    private val mDuskBgColor = R.color.noclass_course_dusk_lesson_color.color
    
    private val mAmTextColor = R.color.noclass_course_am_text_color.color
    private val mPmTextColor = R.color.noclass_course_pm_text_color.color
    private val mNightTextColor = R.color.noclass_course_night_text_color.color
    private val mNoonTextColor = R.color.noclass_course_noon_text_color.color
    private val mDuskTextColor = R.color.noclass_course_dusk_text_color.color
  
    private fun setColor(busyMode : BusyMode) {
      when (busyMode) {
        BusyMode.NAN -> {
          mTvNames.setTextColor(mAmTextColor)
          setCardBackgroundColor(mAmBgColor)
          setOverlapTagColor(mAmTextColor)
        }
        BusyMode.LESS -> {
          mTvNames.setTextColor(mNoonTextColor)
          setCardBackgroundColor(mNoonBgColor)
          setOverlapTagColor(mNoonTextColor)
        }
        BusyMode.MORE -> {
          mTvNames.setTextColor(mPmTextColor)
          setCardBackgroundColor(mPmBgColor)
          setOverlapTagColor(mPmTextColor)
        }
        BusyMode.ALL -> {
          mTvNames.setTextColor(mDuskTextColor)
          setCardBackgroundColor(mDuskBgColor)
          setOverlapTagColor(mDuskTextColor)
        }
      }
    }
    enum class BusyMode{
      NAN,LESS,MORE,ALL
    }
  }
  
}