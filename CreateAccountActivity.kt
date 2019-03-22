package com.appysalons.android.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.appysalons.android.R
import com.appysalons.android.baseclasses.BaseActivity
import com.appysalons.android.utils.*
import kotlinx.android.synthetic.main.activity_create_account.*
import java.util.logging.Handler

class CreateAccountActivity : BaseActivity(), OnApiResponseListener {

    val TAG = CreateAccountActivity::class.java.simpleName

    private var isVisiblePassword: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        init()
        onClick()
    }

    private fun init() {
        edtPassword.setText("")
        isVisiblePassword = false
        edtPassword.inputType = 129

        edtPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                edtPassword.setBackgroundResource(R.drawable.edittext_bg)
                if (text.isNotEmpty()) {
                    showHidePasswordVisibleIcon(true)

                    if (CommanUtils.checkPasswordValidation(text.toString())) {
                        showHidePasswordCorrectIcon(true)
                    } else {
                        showHidePasswordCorrectIcon(false)
                    }

                } else {
                    showHidePasswordVisibleIcon(false)
                }


            }

        })


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

    private fun onClick() {
        imgBackArrow.setOnClickListener {
            CommanUtils.hideSoftKeyboard(this)
            finish()
        }

        btnCreate.setOnClickListener {
            if (validateData()) {
                ApiController(this, this, true).callSignUp(
                        edtEmail.text.toString().trim(),
                        edtFullName.text.toString().trim(),
                        edtPassword.text.toString())
            }
        }

        txtPasswordRequirement.setOnClickListener {
            MessageDialog(this, getString(R.string.title_password_requirement),
                    getString(R.string.message_password_requirement),
                    getString(R.string.color_red_light)).show()
        }

    }

    private fun validateData(): Boolean {

        val email = edtEmail.text.toString().trim()
        val fullName = edtFullName.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        if (fullName.isEmpty()) {
            MessageDialog(this, getString(R.string.title_somethings_missing),
                    getString(R.string.message_name_require),
                    getString(R.string.color_red_light)).show()
            edtFullName.setBackgroundResource(R.drawable.edittext_red)
            edtFullName.requestFocus()

            edtEmail.setBackgroundResource(R.drawable.edittext_bg)
            edtPassword.setBackgroundResource(R.drawable.edittext_bg)
            return false
        } else if (!isEmailValid(email, true)) {
            edtEmail.setBackgroundResource(R.drawable.edittext_red)
            edtEmail.requestFocus()

            edtFullName.setBackgroundResource(R.drawable.edittext_bg)
            edtPassword.setBackgroundResource(R.drawable.edittext_bg)
            return false
        } else if (password.isEmpty()) {

            ApiController(this@CreateAccountActivity, this@CreateAccountActivity, true).callCheckEmailExist(
                    edtEmail.text.toString().trim())
            edtPassword.setBackgroundResource(R.drawable.edittext_red)
            edtPassword.requestFocus()

            edtFullName.setBackgroundResource(R.drawable.edittext_bg)
            edtEmail.setBackgroundResource(R.drawable.edittext_bg)

            return false
        } else if (password.length < 6 || !CommanUtils.checkPasswordValidation(password)) {
            MessageDialog(this, getString(R.string.title_password_error),
                    getString(R.string.message_password_incomplete),
                    getString(R.string.color_red_light)).show()
            edtPassword.setBackgroundResource(R.drawable.edittext_red)
            edtPassword.requestFocus()

            edtFullName.setBackgroundResource(R.drawable.edittext_bg)
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

    private fun showHidePasswordCorrectIcon(isVisible: Boolean) {
        if (isVisible) {
            imgPasswordCorrect.visibility = View.VISIBLE
            txtPasswordRequirement.visibility = View.GONE
        } else {
            imgPasswordCorrect.visibility = View.GONE
            txtPasswordRequirement.visibility = View.VISIBLE
        }
    }

    override fun onSuccess(mAny: Any) {
        Log.e(TAG, "onSuccess : ")

        edtFullName.setBackgroundResource(R.drawable.edittext_bg)
        edtEmail.setBackgroundResource(R.drawable.edittext_bg)
        edtPassword.setBackgroundResource(R.drawable.edittext_bg)

        when (mAny) {
            is Signup -> {
                val signup = mAny as Signup

                val content = signup.message.message_content
                val title = signup.message.message_title
                val colorCode = signup.message.color_code

                if (signup.status == "1") {
                    val mainIntent = Intent(this@CreateAccountActivity, PrivacyPolicyActivity::class.java)
                    mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    mainIntent.putExtra("user_id", signup.data[0].id)
                    mainIntent.putExtra("access_token", signup.data[0].token)
                    mainIntent.putExtra("email", signup.data[0].email)
                    mainIntent.putExtra("redirect_to_dashbord", "0")
                    startActivity(mainIntent)
                    finish()

                } else if (signup.status == "2") {
                    if (content.isNotEmpty() && title.isNotEmpty() && colorCode.isNotEmpty()) {
                        MessageDialog(this, title, content, colorCode).show()
                    }

                    android.os.Handler().postDelayed({
                        val mainIntent = Intent(this@CreateAccountActivity, WelcomeActivity::class.java)
                        mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(mainIntent)
                        finish()
                    }, 1500)

                } else if (signup.status == "0") {
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
                    MessageDialog(this, getString(R.string.title_email_registered),
                            getString(R.string.message_email_registered),
                            getString(R.string.color_green),object: MessageDialog.OnDialogClick{
                        override fun onClick() {
                            val mainIntent = Intent(this@CreateAccountActivity, SignInActivity::class.java)
                            mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(mainIntent)
                            finish()
                        }

                    }).show()
                } else if (checkEmailExist.status == "2") {
                    ApiController(this, this, true).callSignUp(
                            edtEmail.text.toString().trim(),
                            edtFullName.text.toString().trim(), "")
                } else if (checkEmailExist.status == "0") {
                    MessageDialog(this, getString(R.string.title_somethings_missing),
                            getString(R.string.message_password_require),
                            getString(R.string.color_red_light)).show()
                    edtPassword.setBackgroundResource(R.drawable.edittext_red)
                    edtPassword.requestFocus()
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