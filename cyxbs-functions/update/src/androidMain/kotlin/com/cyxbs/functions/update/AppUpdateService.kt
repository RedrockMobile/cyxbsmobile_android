package com.cyxbs.functions.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import com.afollestad.materialdialogs.MaterialDialog
import com.cyxbs.functions.update.api.AppUpdateStatus
import com.cyxbs.functions.update.api.IAppUpdateService
import com.cyxbs.functions.update.bean.UpdateInfo
import com.g985892345.provider.api.annotation.ImplProvider
import java.util.concurrent.TimeUnit

/**
 * Create By Hosigus at 2020/5/2
 */
@ImplProvider
internal object AppUpdateService : IAppUpdateService {
    override fun getUpdateStatus(): LiveData<AppUpdateStatus> = AppUpdateModel.status

    override fun checkUpdate() {
        AppUpdateModel.checkUpdate()
    }

    override fun noticeUpdate(activity: FragmentActivity) {
        val info = AppUpdateModel.updateInfo ?: return
        noticeUpdateInternal(activity, info)
    }
    
    private fun noticeUpdateInternal(activity: FragmentActivity, info: UpdateInfo) {
        MaterialDialog(activity).show {
            title(text = "有新版本更新")
            message(text = "最新版本:" + info.versionName + "\n" + info.updateContent + "\n点击点击，现在就更新一发吧~")
            positiveButton(text = "下载最新安装包") {
                val uri = Uri.parse(info.apkUrl)
                /*
                * 22-8-30
                * 因为应用内更新有很多毛病，所以采用浏览器下载
                * */
                activity.startActivity(
                    Intent(Intent.ACTION_VIEW, uri)
                )
            }
            negativeButton(text = "下次吧") {
                dismiss()
            }
            cornerRadius(16F)
        }
    }
    
    override fun tryNoticeUpdate(activity: FragmentActivity, isForce: Boolean) {
        checkUpdate()
        getUpdateStatus().observe(activity) {
            if (it == AppUpdateStatus.DATED) {
                val sp = activity.getSharedPreferences("更新记录", Context.MODE_PRIVATE)
                val nowTime = System.currentTimeMillis()
                if (isForce) {
                    noticeUpdate(activity)
                    sp.edit { putLong("上次提醒更新时间", nowTime) }
                    return@observe
                }
                val lastTime = sp.getLong("上次提醒更新时间", 0L)
                val diff = TimeUnit.HOURS.convert(nowTime - lastTime, TimeUnit.MILLISECONDS)
                if (diff >= 12) {
                    // 如果有更新，则每隔 12 个小时提醒一次更新
                    noticeUpdate(activity)
                    sp.edit { putLong("上次提醒更新时间", nowTime) }
                }
            }
        }
    }
    
    override fun debug(activity: FragmentActivity, updateContent: String) {
        if (BuildConfig.DEBUG) {
            val info = AppUpdateModel.updateInfo
                ?.copy(updateContent = updateContent)
                ?: UpdateInfo(updateContent = updateContent)
            noticeUpdateInternal(activity, info)
        }
    }
}