package com.cyxbs.pages.ufield.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cyxbs.components.base.ui.BaseViewModel
import com.cyxbs.components.utils.extensions.setSchedulers
import com.cyxbs.components.utils.network.mapOrThrowApiException
import com.cyxbs.pages.ufield.bean.DetailMsg
import com.cyxbs.pages.ufield.network.RankService

class MessageViewModel : BaseViewModel() {
    private val _watchMsg = MutableLiveData<List<DetailMsg>>()
    private val _joinMsg = MutableLiveData<List<DetailMsg>>()
    private val _publishMsg = MutableLiveData<List<DetailMsg>>()
    private val _checkMsg = MutableLiveData<List<DetailMsg>>()

    val watchMsg: LiveData<List<DetailMsg>> get() = _watchMsg
    val joinMsg: LiveData<List<DetailMsg>> get() = _joinMsg
    val publishMsg: LiveData<List<DetailMsg>> get() = _publishMsg
    val checkMsg: LiveData<List<DetailMsg>> get() = _checkMsg

    fun getAllMsg() {
        RankService.INSTANCE.getAllMsg()
            .setSchedulers()
            .mapOrThrowApiException()
            .doOnError {
                toast("服务君似乎打盹了呢~")
            }.safeSubscribeBy {
                _watchMsg.postValue(it.watchMsg)
                _joinMsg.postValue(it.joinMsg)
                _publishMsg.postValue(it.publishMsg)
                _checkMsg.postValue(it.checkMsg)
            }
    }
}