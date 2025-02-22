package com.cyxbs.pages.volunteer.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.common.utils.extensions.sp
import com.cyxbs.pages.volunteer.R
import com.cyxbs.pages.volunteer.bean.VolunteerTime

class VolunteerFeedAdapter(private val volunteerTime: VolunteerTime) : BaseFeedFragment.Adapter() {


    override fun onCreateView(context: Context, parent: ViewGroup): View {
        view = LayoutInflater.from(context).inflate(R.layout.volunteer_discover_feed, parent, false)
        refresh(volunteerTime)
        return view!!
    }

    fun refresh(volunteerTime: VolunteerTime) {
        val context = view?.context ?: return
        view?.let {
            it.findViewById<AppCompatTextView>(R.id.tv_volunteer_feed_total_time).text =
                SpannableStringBuilder(volunteerTime.hours?.toInt().toString().plus("时"))
                    .apply {
                        setSpan(
                            AbsoluteSizeSpan(context.sp(36)),
                            0,
                            this.length - 1,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                        setSpan(
                            AbsoluteSizeSpan(context.sp(8)),
                            this.length - 1,
                            this.length,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                    }
            val tv_volunteer_feed_activity_time =
                it.findViewById<AppCompatTextView>(R.id.tv_volunteer_feed_activity_time)
            if (volunteerTime.hours == 0.0) {
                tv_volunteer_feed_activity_time.text = "还没有志愿时长"
                return
            }
            it.findViewById<AppCompatTextView>(R.id.tv_volunteer_feed_activity_name).text =
                volunteerTime.record?.get(0)?.title
            if (!volunteerTime.record?.get(0)?.start_time.isNullOrEmpty() && volunteerTime.record?.get(
                    0
                )?.start_time?.length!! > 10
            ) {
                it.findViewById<AppCompatTextView>(R.id.tv_volunteer_feed_activity_date).text =
                    volunteerTime.record?.get(0)?.start_time?.substring(0, 10)
            }
            tv_volunteer_feed_activity_time.text = volunteerTime.record?.get(0)?.hours.plus("小时")
            it.findViewById<AppCompatTextView>(R.id.tv_volunteer_feed_activity_address).text =
                volunteerTime.record?.get(0)?.server_group
        }

    }
}