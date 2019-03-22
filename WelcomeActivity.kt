package com.appysalons.android.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.appysalons.android.R
import com.appysalons.android.application.MyApplication
import com.appysalons.android.baseclasses.BaseActivity
import kotlinx.android.synthetic.main.activity_welcome.*
import com.appysalons.android.facebook.FacebookHelper
import com.appysalons.android.facebook.FacebookResponse
import com.appysalons.android.facebook.FacebookUser
import com.appysalons.android.utils.*
import com.facebook.FacebookException
import org.json.JSONObject


class WelcomeActivity : BaseActivity(), FacebookResponse, OnApiResponseListener {

    val TAG = WelcomeActivity::class.java.simpleName

    private lateinit var mFbHelper: FacebookHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        init()
        showButtonsWithAnimations()
        onClicks()
    }

    private fun showButtonsWithAnimations() {

        CommanUtils.showViewWithAnimation(this, btnSignInByEmail, AnimationConst.SHOW_FROM_CENTER)
        CommanUtils.showViewWithAnimation(this, btnSignInByFacebook, AnimationConst.SHOW_FROM_CENTER)

    }

    private fun init() {


        if (!CommanUtils.isNetworkConnected(this)) {
            MessageDialog(this, getString(R.string.title_internet_connection),
                    getString(R.string.message_internet_connection),
                    getString(R.string.color_gray_dark)).show()
        }

        mFbHelper = FacebookHelper(this,
                "id,name,email,gender,birthday,picture,cover", this)
    }

    private fun onClicks() {
        btnSignInByFacebook.setOnClickListener {
            if (CommanUtils.isNetworkConnected(this)) {
                mFbHelper.performSignIn(this)
            } else {
                MessageDialog(this, getString(R.string.title_internet_connection),
                        getString(R.string.message_internet_connection),
                        getString(R.string.color_gray_dark)).show()
            }
        }

        btnSignInByEmail.setOnClickListener {
            val mainIntent = Intent(this@WelcomeActivity, SignInActivity::class.java)
            startActivity(mainIntent)
        }

        btnCreateAccount.setOnClickListener {
            val mainIntent = Intent(this@WelcomeActivity, CreateAccountActivity::class.java)
            startActivity(mainIntent)
        }
    }

    override fun onFbSignInFail(e: String) {
        Log.d("TAG", "onFbSignInFail: $e")
    }

    override fun onFbSignInSuccess() {
        Log.d("TAG", "onFbSignInSuccess: ")
    }

    override fun onFbProfileReceived(facebookUser: FacebookUser?) {

        var facebookData: JSONObject = JSONObject()
        facebookData.put("facebook_id", facebookUser.facebookID.toString())
        facebookData.put("email", facebookUser.email.toString())
        facebookData.put("name", facebookUser.name.toString())

        Log.i(TAG, "onFbProfileReceived: facebookData: " + facebookData.toString())
        ApiController(this, this, true).callLogin("", "", facebookData)
    }

    override fun onFBSignOut() {
        Log.d("TAG", "onFBSignOut: ")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        mFbHelper.onActivityResult(requestCode, resultCode, data);
    }

    override fun onSuccess(mAny: Any) {

        when (mAny) {
            is Login -> {
                val login = mAny as Login

                val content = login.message.message_content
                val title = login.message.message_title
                val colorCode = login.message.color_code

                if (login.status == "1") {

                    val userId = login.data[0].id
                    val userName = login.data[0].name
                    val userEmail = login.data[0].email
                    val userStatus = login.data[0].status
                    val userToken = login.data[0].token

                    MyApplication.appPrefs.setIsLogin(true)
                    MyApplication.appPrefs.setUserData(userId, userName, userEmail, userStatus, userToken)

                    val mainIntent = Intent(this@WelcomeActivity, MainActivity::class.java)
                    mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(mainIntent)
                    finish()
                } else if (login.status == "3") {
                    val mainIntent = Intent(this@WelcomeActivity, PrivacyPolicyActivity::class.java)
                    mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    mainIntent.putExtra("user_id", login.data[0].id)
                    mainIntent.putExtra("access_token", login.data[0].token)
                    mainIntent.putExtra("redirect_to_dashbord", "1")
                    startActivity(mainIntent)
                    finish()
                } else if (login.status == "4") {
                    if (content.isNotEmpty() && title.isNotEmpty() && colorCode.isNotEmpty()) {
                        MessageDialog(this, title, content, colorCode).show()
                    }
                } else if (login.status == "0") {
                    if (content.isNotEmpty() && title.isNotEmpty() && colorCode.isNotEmpty()) {
                        MessageDialog(this, title, content, colorCode).show()
                    }
                } else {
                    if (content.isNotEmpty() && title.isNotEmpty() && colorCode.isNotEmpty()) {
                        MessageDialog(this, title, content, colorCode).show()
                    }
                }
            }
        }
    }

    override fun onFailure(mThrowable: Throwable) {
        Log.e(TAG, "onFailure : " + mThrowable.message)
    }
}