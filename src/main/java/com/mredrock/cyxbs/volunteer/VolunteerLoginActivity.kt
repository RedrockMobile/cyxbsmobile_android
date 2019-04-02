package com.mredrock.cyxbs.volunteer

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.volunteer.Network.ApiService
import com.mredrock.cyxbs.volunteer.Network.VolunteerRetrofit
import com.mredrock.cyxbs.volunteer.bean.VolunteerLogin
import com.mredrock.cyxbs.volunteer.widget.EncryptPassword
import com.mredrock.cyxbs.volunteer.widget.VolunteerTimeSP
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

@Route(path = DISCOVER_VOLUNTEER)
class VolunteerLoginActivity : BaseActivity() {
    companion object {
        private const val BIND_SUCCESS: Int = 0
        private const val INVALID_ACCOUNT: Int = -2
        private const val WRONG_PASSWORD: Int = 3
    }

    private var uid: String? = null
    private lateinit var account: String
    private lateinit var password: String
    private lateinit var volunteerSP: VolunteerTimeSP
    private lateinit var dialog: ProgressDialog

    override val isFragmentActivity: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        common_toolbar.init("完善信息")

        btn_volunteer_login.setOnClickListener { view: View? ->
            showProgressDialog()
            initUserInfo()
            if (view!!.id == R.id.btn_volunteer_login) login(account, EncryptPassword.encrypt(password))
        }

        useSoftKeyboard()
        initUserInfo()
    }

    private fun initUserInfo() {
        account = volunteer_account.text.toString()
        password = volunteer_password.text.toString()

        volunteerSP = VolunteerTimeSP(this)
        if (BaseApp.user != null) {
            uid = BaseApp.user!!.stuNum!!
        } else {
            val regEx = "[^0-9]"
            val p = Pattern.compile(regEx)
            val m = p.matcher(account)
            uid = m.replaceAll("").trim()
            LogUtils.d(TAG, uid!!)
        }

        if ((!(volunteerSP.volunteerUid == "404"
                        || volunteerSP.volunteerUid == "null"
                        || volunteerSP.volunteerUid == ""))) {

            val intent = Intent(this, VolunteerRecordActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun useSoftKeyboard() {
        volunteer_password.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_GO) {
                showProgressDialog()
                initUserInfo()
                login(account, EncryptPassword.encrypt(password))
                handled = true

                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (inputMethodManager.isActive) {
                    inputMethodManager.hideSoftInputFromWindow(this@VolunteerLoginActivity.currentFocus.windowToken, 0)
                }
            }
            handled
        }
    }

    private fun login(account: String, encodingPassword: String) {
        if (uid == null) {

        }
        ApiGenerator.getApiService(VolunteerRetrofit.volunteerRetrofit, ApiService::class.java)
                .volunteerLogin("Basic enNjeTpyZWRyb2Nrenk=", account, encodingPassword, uid!!)
                .setSchedulers()
                .safeSubscribeBy { volunteerLogin: VolunteerLogin ->
                    when ((volunteerLogin.code)!!.toInt()) {
                        BIND_SUCCESS -> {
                            volunteerSP.bindVolunteerInfo(account, password, uid!!)
                            val intent = Intent(this@VolunteerLoginActivity, VolunteerRecordActivity::class.java)
                            startActivity(intent)
                            dialog.dismiss()
                            finish()
                        }

                        INVALID_ACCOUNT -> showUnsuccessDialog("亲，输入的账号不存在哦")

                        WRONG_PASSWORD -> showUnsuccessDialog("亲，输入的账号或密码有误哦")
                    }
                }
    }

    private fun showProgressDialog() {
        dialog = ProgressDialog.show(this, "登录中...", "请稍候")
        dialog.setCancelable(false)
    }

    private fun showUnsuccessDialog(text: String) {
        runOnUiThread {
            dialog.dismiss()
            MaterialDialog.Builder(this@VolunteerLoginActivity)
                    .title("登录失败")
                    .content(text)
                    .positiveText("我知道啦")
                    .onPositive{_,_ ->
                        volunteer_password.setText("")
                    }.show()
        }
    }
}
