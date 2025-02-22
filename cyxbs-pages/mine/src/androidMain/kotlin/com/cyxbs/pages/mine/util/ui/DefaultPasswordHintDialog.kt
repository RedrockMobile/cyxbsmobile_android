package com.cyxbs.pages.mine.util.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.widget.Button
import com.cyxbs.pages.mine.R

/**
 *@Date 2020-11-04
 *@Time 19:10
 *@author SpreadWater
 *@description 一个提示默认密码的dialog
 */
class DefaultPasswordHintDialog(context: Context, them: Int) : Dialog(context, them) {
    companion object {
        fun show(context: Context?, activity: Activity) {
            if (context == null) return
            val defaultPasswordHintDialog = DefaultPasswordHintDialog(context, R.style.transparent_dialog)
            defaultPasswordHintDialog.setContentView(R.layout.mine_dialog_default_password_hint)
            val relogin = defaultPasswordHintDialog.findViewById<Button>(R.id.mine_security_bt_relogin)
            relogin.setOnClickListener {
                defaultPasswordHintDialog.hide()
            }
            //修改：将原本的只在按钮监听事件中finish activity变更为在cancel的监听事件中finish activity
            defaultPasswordHintDialog.setOnCancelListener {
                activity.finish()
            }
            defaultPasswordHintDialog.show()
        }
    }
}