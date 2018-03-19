package com.kumarpalsinh.sas.application.authentication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kumarpalsinh.sas.R;
import com.kumarpalsinh.sas.utils.BaseActivity;
import com.kumarpalsinh.sas.utils.AppController;
import com.kumarpalsinh.sas.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpStepOne extends BaseActivity {

    //General Variables
    private static final String TAG = SignUpStepOne.class.getSimpleName();
    Activity mActivity = SignUpStepOne.this;

    //Toolbar
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    //EditTexts
    @BindView(R.id.edtName)
    EditText edtName;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.edtConfirmPassword)
    EditText edtConfirmPassword;

    //LinearLayout
    @BindView(R.id.linearParentSignUpStepOne)
    LinearLayout linearParentSignUpStepOne;

    //String Data
    String name = "", email = "", password = "", confirmPassword = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step_one);
        ButterKnife.bind(mActivity);

        //Initialize Views
        initializeViews();

        //Initialize Data
        initializeData();

    }


    /**
     * Initialize views
     */
    private void initializeViews() {
        // TODO ... Initialize your views here

        setSupportActionBar(mToolbar);
        mToolbar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.icn_back_black);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });

    }


    /**
     * Initialize data
     */
    private void initializeData() {
        // TODO ... Initialize your data here


    }


    /**
     * Validating data for Sign Up
     */
    public boolean validate() {

        boolean valid = false;

        name = edtName.getText().toString();
        email = edtEmail.getText().toString();
        password = edtPassword.getText().toString();
        confirmPassword = edtConfirmPassword.getText().toString();

        if (name.isEmpty()) {

            CommonUtils.getInstance().displaySnackBar(linearParentSignUpStepOne,
                    getResources().getString(R.string.alert_empty_name));

            edtName.requestFocus();
        } else if (email.isEmpty()) {


            CommonUtils.getInstance().displaySnackBar(linearParentSignUpStepOne,
                    getResources().getString(R.string.alert_empty_email_id));

            edtEmail.requestFocus();
        } else if (!CommonUtils.getInstance().isValidEmail(email)) {

            CommonUtils.getInstance().displaySnackBar(linearParentSignUpStepOne,
                    getResources().getString(R.string.alert_invalid_email_id));

            edtEmail.requestFocus();
        } else if (password.isEmpty()) {


            CommonUtils.getInstance().displaySnackBar(linearParentSignUpStepOne,
                    getResources().getString(R.string.alert_empty_password));

            edtPassword.requestFocus();
        } else if (password.length() < 6 || password.length() > 15) {

            CommonUtils.getInstance().displaySnackBar(linearParentSignUpStepOne,
                    getResources().getString(R.string.alert_invalid_password));

            edtPassword.requestFocus();
        } else if (confirmPassword.isEmpty()) {

            CommonUtils.getInstance().displaySnackBar(linearParentSignUpStepOne,
                    getResources().getString(R.string.alert_empty_confirm_password));
            edtConfirmPassword.requestFocus();
        } else if (!confirmPassword.equals(password)) {

            CommonUtils.getInstance().displaySnackBar(linearParentSignUpStepOne,
                    getResources().getString(R.string.alert_passwords_not_match));

            edtPassword.requestFocus();
        } else {
            valid = true;
        }

        return valid;
    }

    // TODO _______________________( Click Events )_______________________

    @OnClick(R.id.btnNext)
    void onClickNext() {
        if (validate()) {
            AppController.getAppPref().setIsLogin(true);
            CommonUtils.getInstance().startActivity(mActivity, SignUpStepTwo.class);
        }
    }
}