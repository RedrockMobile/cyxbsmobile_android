package com.mredrock.cyxbs.qa.pages.dynamic.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.beannew.Comment
import com.mredrock.cyxbs.qa.beannew.CommentReleaseResult
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.network.NetworkState

/**
 * @Author: zhangzhe
 * @Date: 2020/11/27 23:52
 */

open class DynamicDetailViewModel : BaseViewModel() {

    val commentList = MutableLiveData<List<Comment>>()

    val loadStatus = MutableLiveData<Int>()

    val replyInfo = MutableLiveData<Pair<String, String>>()

    var position = 0

    val commentReleaseResult = MutableLiveData<CommentReleaseResult>()


    fun refreshCommentList(postId: String, rv: RecyclerView, commentId: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getComment(postId)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnSubscribe {
                    loadStatus.postValue(NetworkState.LOADING)
                }
                .doOnError {
                    loadStatus.value = NetworkState.FAILED
                }
                .safeSubscribeBy { list ->
                    loadStatus.value = NetworkState.SUCCESSFUL
                    commentList.postValue(list.reversed())
                    position = findCommentByCommentId(list.reversed(), commentId)


                }
    }
    // 回复后，滑动到刚刚回复的comment下
    private fun findCommentByCommentId(dataList: List<Comment>, commentId: String): Int {

        if (commentId == ""){
            return 0
        }
        for (i in dataList.indices) {
            // 如果是直接回复
            if (dataList[i].commentId == commentId){
                return i
            }
            // 如果回复是回复别人，则滚动到被回复的comment地方
            if (dataList[i].replyList != null && dataList[i].replyList.isNotEmpty()) {
                for (j in dataList[i].replyList.indices) {
                    if (dataList[i].replyList[j].commentId == commentId) {
                        return i
                    }
                }
            }
        }
        return 0
    }

    fun releaseComment(postId: String, content: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .releaseComment(content, postId, replyInfo.value?.second?: "")
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnSubscribe {

                }
                .doOnError {

                }
                .safeSubscribeBy {
                    commentReleaseResult.postValue(it)
                }
    }


    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(DynamicDetailViewModel::class.java)) {
                return DynamicDetailViewModel() as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }


}