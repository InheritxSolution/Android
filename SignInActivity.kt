package com.appysalons.android.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.appysalons.android.R
import com.appysalons.android.application.MyApplication
import com.appysalons.android.baseclasses.BaseActivity
import com.appysalons.android.utils.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import android.app.ActivityManager
import android.content.Context
import android.app.Activity


class SignInActivity : BaseActivity(), OnApiResponseListener {

    val TAG = SignInActivity::class.java.simpleName

    private var isVisiblePassword: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        init()
        onClick()
    }

    private fun init() {
        edtPassword.setText("")
        isVisiblePassword = false
        edtPassword.inputType = 129

        edtPassword.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(p0: View?, p1: Boolean) {
                edtEmail.setBackgroundResource(R.drawable.edittext_bg)
            }
        }
        edtEmail.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(p0: View?, p1: Boolean) {
                edtPassword.setBackgroundResource(R.drawable.edittext_bg)
            }
        }

        edtPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                edtPassword.setBackgroundResource(R.drawable.edittext_bg)

                if (text.isNotEmpty()) {
                    showHidePasswordVisibleIcon(true)
                } else {
                    showHidePasswordVisibleIcon(false)
                }


            }

        })


        val bundle = intent.extras
        if (bundle.containsKey("user_auth_token")) {
            val user_auth_token = bundle.getString("user_auth_token")
            val email = bundle.getString("email")
            val user_id = bundle.getString("user_id")

            ApiController(this, this, true).callVerifyEmailAuth(user_auth_token)

        }
    }

    private fun onClick() {
        imgBackArrow.setOnClickListener {

            CommanUtils.hideSoftKeyboard(this)

            val mngr = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            val taskList = mngr.getRunningTasks(10)

            if (taskList[0].numActivities == 1 && taskList[0].topActivity.className == this.javaClass.name) {
                Log.i(TAG, "This is last activity in the stack")
                val mainIntent = Intent(this@SignInActivity, WelcomeActivity::class.java)
                startActivity(mainIntent)
                finish()
            } else {
                finish()
            }
        }

        txtForgotPassword.setOnClickListener {
            val mainIntent = Intent(this@SignInActivity, ForgotPasswordActivity::class.java)
            mainIntent.putExtra("email", edtEmail.text.toString().trim())
            startActivityForResult(mainIntent, 10)
        }

        btnSignIn.setOnClickListener {
            if (validateData()) {
                ApiController(this, this, true).callLogin(
                        edtEmail.text.toString().trim(),
                        edtPassword.text.toString(), null)
            }
        }

        imgPasswordVisibleInvisible.setOnClickListener {
            if (isVisiblePassword) {
                isVisiblePassword = false
                edtPassword.inputType = 129
                imgPasswordVisibleInvisible.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_password_invisible))
                edtPassword.setSelection(edtPassword.text!!.length)
            } else {
                isVisiblePassword = true
                edtPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                imgPasswordVisibleInvisible.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_password_visible))
                edtPassword.setSelection(edtPassword.text!!.length)
            }
        }

    }

    private fun validateData(): Boolean {

        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        if (!isEmailValid(email, true)) {
            edtEmail.setBackgroundResource(R.drawable.edittext_red)
            edtEmail.requestFocus()

            edtPassword.setBackgroundResource(R.drawable.edittext_bg)
            return false
        } else if (password.isEmpty()) {

            ApiController(this@SignInActivity, this@SignInActivity, true).callCheckEmailExist(
                    edtEmail.text.toString().trim())
            edtEmail.setBackgroundResource(R.drawable.edittext_red)
            edtEmail.requestFocus()

            edtPassword.setBackgroundResource(R.drawable.edittext_bg)
            return false
        } else if (password.length < 6) {
            MessageDialog(this, getString(R.string.title_password_error),
                    getString(R.string.message_password_incomplete),
                    getString(R.string.color_red_light)).show()
            edtPassword.setBackgroundResource(R.drawable.edittext_red)
            edtPassword.requestFocus()

            edtEmail.setBackgroundResource(R.drawable.edittext_bg)

            return false
        } else {
            return true
        }

    }

    private fun isEmailValid(email: String, displayMessage: Boolean): Boolean {
        if (email.isEmpty()) {
            if (displayMessage) {
                MessageDialog(this, getString(R.string.title_somethings_missing),
                        getString(R.string.message_email_require),
                        getString(R.string.color_red_light)).show()
            }
            return false
        } else if (!CommanUtils.isValidEmail(email)) {
            if (displayMessage) {
                MessageDialog(this, getString(R.string.title_format_error),
                        getString(R.string.message_email_incorrect),
                        getString(R.string.color_red_light)).show()
            }
            return false
        } else {
            return true
        }
    }

    private fun showHidePasswordVisibleIcon(isVisible: Boolean) {
        if (isVisible) {
            imgPasswordVisibleInvisible.visibility = View.VISIBLE
        } else {
            imgPasswordVisibleInvisible.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === 10) {
            if (resultCode === Activity.RESULT_OK) {

                if (data != null) {
                    val email = data.getStringExtra("email")
                    edtEmail.setText(email)
                }
            }
            if (resultCode === Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    override fun onSuccess(mAny: Any) {
        Log.e(TAG, "onSuccess : ")

        edtEmail.setBackgroundResource(R.drawable.edittext_bg)
        edtPassword.setBackgroundResource(R.drawable.edittext_bg)

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

                    val mainIntent = Intent(this@SignInActivity, MainActivity::class.java)
                    mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(mainIntent)
                    finish()

                } else if (login.status == "2") {
                    if (content.isNotEmpty() && title.isNotEmpty() && colorCode.isNotEmpty()) {
                        MessageDialog(this, title, content, colorCode, MessageDialog.OnDialogClick { finish() }).show()
                    }
                } else if (login.status == "3") {
                    val mainIntent = Intent(this@SignInActivity, PrivacyPolicyActivity::class.java)
                    mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    mainIntent.putExtra("user_id", login.data[0].id)
                    mainIntent.putExtra("access_token", login.data[0].token)
                    mainIntent.putExtra("email", login.data[0].email)
                    mainIntent.putExtra("redirect_to_dashbord", "0")
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

            is CheckEmailExist -> {
                val checkEmailExist = mAny as CheckEmailExist

                val content = checkEmailExist.message.message_content
                val title = checkEmailExist.message.message_title
                val colorCode = checkEmailExist.message.color_code

                if (checkEmailExist.status == "1") {
                    MessageDialog(this, getString(R.string.title_somethings_missing),
                            getString(R.string.message_password_require),
                            getString(R.string.color_red_light)).show()
                    edtPassword.setBackgroundResource(R.drawable.edittext_red)
                    edtPassword.requestFocus()
                } else if (checkEmailExist.status == "2") {
                    if (content.isNotEmpty() && title.isNotEmpty() && colorCode.isNotEmpty()) {
                        MessageDialog(this, title, content, colorCode, MessageDialog.OnDialogClick { finish() }).show()
                    }
                } else if (checkEmailExist.status == "0") {
                    if (content.isNotEmpty() && title.isNotEmpty() && colorCode.isNotEmpty()) {
                        MessageDialog(this, title, content, colorCode).show()
                    }

                    edtEmail.setBackgroundResource(R.drawable.edittext_red)
                    edtEmail.requestFocus()
                } else {
                    if (content.isNotEmpty() && title.isNotEmpty() && colorCode.isNotEmpty()) {
                        MessageDialog(this, title, content, colorCode).show()
                    }
                }


            }

            is VerifyEmailAuth -> {
                val verifyEmailAuth = mAny as VerifyEmailAuth

                val content = verifyEmailAuth.message.message_content
                val title = verifyEmailAuth.message.message_title
                val colorCode = verifyEmailAuth.message.color_code

                if (verifyEmailAuth.status == "1") {
                    if (content.isNotEmpty() && title.isNotEmpty() && colorCode.isNotEmpty()) {
                        MessageDialog(this, title, content, colorCode).show()
                    }
                    val bundle = intent.extras
                    if (bundle.containsKey("email")) {
                        edtEmail.setText(bundle.getString("email"))
                    }

                } else if (verifyEmailAuth.status == "0") {
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