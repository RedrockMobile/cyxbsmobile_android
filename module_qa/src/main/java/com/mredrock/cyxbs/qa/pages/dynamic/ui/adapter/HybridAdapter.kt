package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.beannew.H5Dynamic
import com.mredrock.cyxbs.qa.beannew.Message
import com.mredrock.cyxbs.qa.beannew.MessageWrapper
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.ui.widget.NineGridView
import com.mredrock.cyxbs.qa.ui.widget.OptionalPopWindow
import com.mredrock.cyxbs.qa.ui.widget.ShareDialog
import com.mredrock.cyxbs.qa.utils.dynamicTimeDescription
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_header.view.*

/**
 * @author: RayleighZ
 * @describe: Based on DynamicAdapter, feat hybrid dynamic
 */
class HybridAdapter(val context: Context?, private val onItemClickEvent: (Dynamic, View) -> Unit) :
        BaseEndlessRvAdapter<Message>(DIFF_CALLBACK) {
    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Message>() {

            //比较二者是否是统一数据类型
            override fun areItemsTheSame(oldItem: Message, newItem: Message) = oldItem.javaClass == newItem.javaClass

            //只需比较两者内容上是否相同
            override fun areContentsTheSame(oldItem: Message, newItem: Message) = oldItem.toString() == newItem.toString()
        }
    }

    var onShareClickListener: ((Dynamic, String) -> Unit)? = null
    var onTopicListener: ((String, View) -> Unit)? = null
    var onPopWindowClickListener: ((Int, String, Dynamic) -> Unit)? = null

    var curSharedItem: View? = null
    var curSharedDynamic: Dynamic? = null
    var curSharedItemPosition: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DynamicViewHolder(parent)

    //封装一层viewType
    override fun getItemViewType(position: Int): Int = getItem(position)?.let {
        if (it is Dynamic) {
            MessageWrapper.NORMAL_DYNAMIC
        } else {
            MessageWrapper.H5_DYNAMIC
        }
    } ?: MessageWrapper.NORMAL_DYNAMIC

    override fun onBindViewHolder(holder: BaseViewHolder<Message>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.apply {
            val data = getItem(position)
            if (data is Dynamic){
                initDynamicItem(this, data, position)
            } else if (data is H5Dynamic){
                initH5Item(this, data, position)
            }
        }
    }

    private fun initDynamicItem(itemView: View, dynamic: Dynamic, position: Int) {
        itemView.apply {
            qa_iv_dynamic_share.setOnSingleClickListener {
                ShareDialog(context).apply {
                    initView(onCancelListener = {
                        dismiss()
                    }, qqShare = {
                        onShareClickListener?.invoke(
                                dynamic,
                                CommentConfig.QQ_FRIEND
                        )
                    }, qqZoneShare = {
                        onShareClickListener?.invoke(dynamic, CommentConfig.QQ_ZONE)
                    }, weChatShare = {
                        CyxbsToast.makeText(
                                context,
                                R.string.qa_share_wechat_text,
                                Toast.LENGTH_SHORT
                        ).show()
                    }, friendShipCircle = {
                        CyxbsToast.makeText(
                                context,
                                R.string.qa_share_wechat_text,
                                Toast.LENGTH_SHORT
                        ).show()
                    }, copyLink = {
                        onShareClickListener?.invoke(
                                dynamic,
                                CommentConfig.COPY_LINK
                        )
                    })
                }.show()
            }
            qa_tv_dynamic_topic.setOnSingleClickListener {
                dynamic.topic.let { it1 -> onTopicListener?.invoke(it1, it) }
            }
            qa_iv_dynamic_more_tips_clicked.setOnSingleClickListener { view ->
                if (dynamic.isSelf == 0) {
                    if (dynamic.isFollowTopic == 0) {
                        OptionalPopWindow.Builder().with(context)
                                .addOptionAndCallback(CommentConfig.IGNORE) {
                                    onPopWindowClickListener?.invoke(position, CommentConfig.IGNORE, dynamic)
                                }.addOptionAndCallback(CommentConfig.REPORT) {
                                    onPopWindowClickListener?.invoke(position, CommentConfig.REPORT, dynamic)
                                }.addOptionAndCallback(CommentConfig.FOLLOW) {
                                    onPopWindowClickListener?.invoke(position, CommentConfig.FOLLOW, dynamic)
                                }.show(view, OptionalPopWindow.AlignMode.RIGHT, 0)
                    } else {
                        OptionalPopWindow.Builder().with(context)
                                .addOptionAndCallback(CommentConfig.IGNORE) {
                                    onPopWindowClickListener?.invoke(position, CommentConfig.IGNORE, dynamic)
                                }.addOptionAndCallback(CommentConfig.REPORT) {
                                    onPopWindowClickListener?.invoke(position, CommentConfig.REPORT, dynamic)
                                }.addOptionAndCallback(CommentConfig.UN_FOLLOW) {
                                    onPopWindowClickListener?.invoke(position, CommentConfig.UN_FOLLOW, dynamic)
                                }.show(view, OptionalPopWindow.AlignMode.RIGHT, 0)
                    }
                } else {
                    OptionalPopWindow.Builder().with(context)
                            .addDynamicData(dynamic)
                            .addOptionAndCallback(CommentConfig.DELETE, R.layout.qa_popupwindow_option_bottom) {
                                onPopWindowClickListener?.invoke(position, CommentConfig.DELETE, dynamic)
                            }.showFromBottom(LayoutInflater.from(context).inflate(
                                    R.layout.qa_fragment_dynamic, null, false
                            ))
                }
            }
        }
    }

    private fun initH5Item(itemView: View, h5Dynamic: H5Dynamic, position: Int){
        //TODO: 补充H5 todo填充逻辑
        itemView.apply {

        }
    }

    override fun onItemClickListener(
            holder: BaseViewHolder<Message>,
            position: Int,
            data: Message
    ) {
        super.onItemClickListener(holder, position, data)

        //判断数据类型，执行不同逻辑
        if (data is Dynamic) {
            if (holder !is DynamicViewHolder) return
            curSharedDynamic = data
            curSharedItem = holder.itemView
            curSharedItemPosition = position
            onItemClickEvent.invoke(data, holder.itemView)
        } else if (data is H5Dynamic) {
            //TODO: 补充H5数据类型点击时的逻辑
        }

    }

    class DynamicViewHolder(parent: ViewGroup) :
            BaseViewHolder<Message>(parent, R.layout.qa_recycler_item_dynamic_header) {
        override fun refresh(data: Message?) {
            data ?: return
            if (data is Dynamic){
                refreshNormalDynamic(data)
            } else if (data is H5Dynamic){
                refreshH5Dynamic(data)
            }
        }

        private fun refreshH5Dynamic(h5Dynamic: H5Dynamic){
            //TODO: 增加刷新H5的逻辑
        }

        private fun refreshNormalDynamic(data: Dynamic){
            itemView.apply {
                qa_iv_dynamic_praise_count_image.registerLikeView(
                        data.postId,
                        CommentConfig.PRAISE_MODEL_DYNAMIC,
                        data.isPraised,
                        data.praiseCount
                )
                qa_iv_dynamic_praise_count_image.setOnSingleClickListener {
                    qa_iv_dynamic_praise_count_image.click()
                }
                qa_iv_dynamic_avatar.setAvatarImageFromUrl(data.avatar)
                qa_tv_dynamic_topic.text = "# " + data.topic
                qa_tv_dynamic_nickname.text = data.nickName
                qa_tv_dynamic_content.setContent(data.content)
                qa_tv_dynamic_comment_count.text = data.commentCount.toString()
                qa_tv_dynamic_publish_at.text =
                        dynamicTimeDescription(System.currentTimeMillis(), data.publishTime * 1000)
                //解决图片错乱的问题
                if (data.pics.isNullOrEmpty())
                    qa_dynamic_nine_grid_view.setRectangleImages(
                            emptyList(),
                            NineGridView.MODE_IMAGE_THREE_SIZE
                    )
                else {
                    data.pics.apply {
                        val tag = qa_dynamic_nine_grid_view.tag
                        if (null == tag || tag == this) {
                            val tagStore = qa_dynamic_nine_grid_view.tag
                            qa_dynamic_nine_grid_view.setImages(
                                    this,
                                    NineGridView.MODE_IMAGE_THREE_SIZE,
                                    NineGridView.ImageMode.MODE_IMAGE_RECTANGLE
                            )
                            qa_dynamic_nine_grid_view.tag = tagStore
                        } else {
                            val tagStore = this
                            qa_dynamic_nine_grid_view.tag = null
                            qa_dynamic_nine_grid_view.setRectangleImages(
                                    emptyList(),
                                    NineGridView.MODE_IMAGE_THREE_SIZE
                            )
                            qa_dynamic_nine_grid_view.setImages(
                                    this,
                                    NineGridView.MODE_IMAGE_THREE_SIZE,
                                    NineGridView.ImageMode.MODE_IMAGE_RECTANGLE
                            )
                            qa_dynamic_nine_grid_view.tag = tagStore
                        }
                    }
                }
                qa_dynamic_nine_grid_view.setOnItemClickListener { _, index ->
                    ViewImageActivity.activityStart(
                            context,
                            data.pics.map { it }.toTypedArray(),
                            index
                    )
                }
            }
        }
    }
}