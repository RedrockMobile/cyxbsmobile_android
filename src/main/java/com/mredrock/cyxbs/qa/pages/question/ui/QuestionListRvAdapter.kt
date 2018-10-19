package com.mredrock.cyxbs.qa.pages.question.ui

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import android.widget.TextView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.event.AskLoginEvent
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.pages.answer.ui.AnswerListActivity
import com.mredrock.cyxbs.qa.utils.setNicknameTv
import com.mredrock.cyxbs.qa.utils.timeDescription
import com.mredrock.cyxbs.qa.utils.toDate
import kotlinx.android.synthetic.main.qa_recycle_item_question.view.*
import org.greenrobot.eventbus.EventBus

/**
 * Created By jay68 on 2018/8/26.
 */
class QuestionListRvAdapter : BaseEndlessRvAdapter<Question>(DIFF_CALLBACK) {
    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Question>() {
            override fun areItemsTheSame(oldItem: Question?, newItem: Question?) = oldItem?.id == newItem?.id

            override fun areContentsTheSame(oldItem: Question?, newItem: Question?) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = QuestionViewHolder(parent)

    override fun onItemClickListener(holder: BaseViewHolder<Question>, position: Int, data: Question) {
        super.onItemClickListener(holder, position, data)
        val context = holder.itemView.context
        if (BaseApp.isLogin) {
            AnswerListActivity.activityStart(context, data)
        } else {
            EventBus.getDefault().post(AskLoginEvent(context.getString(R.string.qa_unlogin_error)))
        }
    }

    class QuestionViewHolder(parent: ViewGroup) : BaseViewHolder<Question>(parent, R.layout.qa_recycle_item_question) {
        override fun refresh(data: Question?) {
            data ?: return
            itemView.apply {
                iv_avatar.setAvatarImageFromUrl(data.photoThumbnailSrc)
                tv_nickname.setNicknameTv(data.nickname, data.isEmotion && !data.isAnonymous, data.isMale)
                setDisappear(tv_disappear_at, data.disappearAt)
                tv_tag.text = context.getString(R.string.qa_question_item_tag, data.tags)
                tv_title.text = data.title
                tv_content.text = data.description
                tv_reward.text = context.getString(R.string.qa_question_item_reward, data.reward)
                //todo 没图片时隐藏查看图片按钮
                setShowPictureButton(tv_show_picture, data.photoThumbnailSrc)
            }
        }

        private fun setDisappear(tv: TextView, rowTime: String) {
            tv.text = context.getString(R.string.qa_question_item_disappear,
                    timeDescription(System.currentTimeMillis(), rowTime.toDate().time))
        }

        private fun setShowPictureButton(tv: TextView, url: String?) {
            if (url.isNullOrBlank()) {
                tv.gone()
            } else {
                tv.visible()
            }
        }
    }
}