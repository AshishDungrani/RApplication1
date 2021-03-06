package com.rawalinfocom.rcontact;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Country;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OtpVerificationActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.includeToolbar)
    LinearLayout includeToolbar;
    ImageView imageActionBack;
    RippleView rippleActionBack;
    Toolbar toolbarOtpVerification;
    TextView textToolbarTitle;
    @BindView(R.id.text_verify_number)
    TextView textVerifyNumber;
    @BindView(R.id.text_enter_otp)
    TextView textEnterOtp;
    @BindView(R.id.input_otp)
    EditText inputOtp;
    @BindView(R.id.button_submit)
    Button buttonSubmit;
    @BindView(R.id.ripple_submit)
    RippleView rippleSubmit;
    @BindView(R.id.button_resend)
    Button buttonResend;
    @BindView(R.id.ripple_resend)
    RippleView rippleResend;
    @BindView(R.id.relative_root_otp_verification)
    RelativeLayout relativeRootOtpVerification;
    @BindView(R.id.text_resend_call_msg)
    TextView textResendCallMsg;
    @BindView(R.id.button_call_me)
    Button buttonCallMe;
    @BindView(R.id.ripple_call_me)
    RippleView rippleCallMe;
    @BindView(R.id.linear_resend_call_me)
    LinearLayout linearResendCallMe;

    private String[] requiredPermissions = {Manifest.permission.READ_SMS, Manifest
            .permission.RECEIVE_SMS};

    String mobileNumber, selectedCountryCode;
    Country selectedCountry;
    private String isFrom = "";
    private CountDownTimer countDownTimer;

    Intent otpServiceIntent;
    BroadcastReceiver receiver;
    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        ButterKnife.bind(this);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(getString(R.string.str_rawal_otp))) {
                    final String message = intent.getStringExtra("message");
                    if (StringUtils.length(message) == AppConstants.OTP_LENGTH)
                        inputOtp.setText(message);
                    otpConfirmed(message);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.str_rawal_otp));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            isFrom = bundle.getString(AppConstants.EXTRA_IS_FROM, "");
        }

        if (isFrom.equalsIgnoreCase(AppConstants.EXTRA_IS_FROM_FORGOT_PASSWORD) ||
                isFrom.equalsIgnoreCase(AppConstants.EXTRA_IS_FROM_VERIFICATION)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkPermissionToExecute(requiredPermissions, AppConstants.READ_SMS);
            }
        }

        if (isFrom.equalsIgnoreCase(AppConstants.EXTRA_IS_FROM_VERIFICATION)) {
            mobileNumber = bundle.getString(AppConstants.EXTRA_MOBILE_NUMBER, "");
            selectedCountryCode = bundle.getString(AppConstants.EXTRA_OBJECT_COUNTRY_CODE, "");
        } else {
            mobileNumber = Utils.getStringPreference(OtpVerificationActivity.this, AppConstants
                    .PREF_REGS_MOBILE_NUMBER, "");
            selectedCountry = (Country) Utils.getObjectPreference(OtpVerificationActivity.this,
                    AppConstants.PREF_SELECTED_COUNTRY_OBJECT, Country.class);
        }

        if (selectedCountry == null) {
            selectedCountry = new Country();
        }

        init();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionToExecute(String[] permissions, int requestCode) {
        boolean READ_SMS = ContextCompat.checkSelfPermission(OtpVerificationActivity
                .this, permissions[0]) !=
                PackageManager.PERMISSION_GRANTED;
        boolean RECEIVE_SMS = ContextCompat.checkSelfPermission(OtpVerificationActivity
                .this, permissions[1]) !=
                PackageManager.PERMISSION_GRANTED;
        if (READ_SMS || RECEIVE_SMS) {
            requestPermissions(permissions, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                finish();
                break;

            case R.id.ripple_submit:

                if (inputOtp.getText().toString().equals("")) {
                    Utils.showErrorSnackBar(OtpVerificationActivity.this,
                            relativeRootOtpVerification, getString(R.string.msg_otp_error));
                } else {
                    otpConfirmed(inputOtp.getText().toString());
                }
                break;

            case R.id.ripple_resend:
                inputOtp.setText("");
                sendOtp();
                break;

            case R.id.ripple_call_me:
                break;
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            //<editor-fold desc="REQ_SEND_OTP">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_CHECK_NUMBER)) {
                WsResponseObject otpDetailResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (otpDetailResponse != null && StringUtils.equalsIgnoreCase(otpDetailResponse
                        .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                    Utils.showSuccessSnackBar(OtpVerificationActivity.this,
                            relativeRootOtpVerification, otpDetailResponse.getMessage());
//                    if (isFrom.equalsIgnoreCase(AppConstants.EXTRA_IS_FROM_VERIFICATION)) {
//                        linearResendCallMe.setVisibility(View.GONE);
//                        resendTimer();
//                    }
                } else {
                    if (otpDetailResponse != null) {
                        Log.e("error response", otpDetailResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootOtpVerification,
                                otpDetailResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootOtpVerification, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_OTP_CONFIRMED">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_OTP_CONFIRMED)) {
                WsResponseObject confirmOtpResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (confirmOtpResponse != null && StringUtils.equalsIgnoreCase(confirmOtpResponse
                        .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    UserProfile userProfile = confirmOtpResponse.getUserProfile();

                    Utils.setObjectPreference(OtpVerificationActivity.this,
                            AppConstants.PREF_REGS_USER_OBJECT, userProfile);

                    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

                    if (isFrom.equals(AppConstants.EXTRA_IS_FROM_FORGOT_PASSWORD)) {

                        // set launch screen as OtpVerificationActivity
                        Utils.setIntegerPreference(OtpVerificationActivity.this,
                                AppConstants.PREF_LAUNCH_SCREEN_INT, IntegerConstants
                                        .LAUNCH_MOBILE_REGISTRATION);

                        // Redirect to SetPassWordActivity
                        Bundle bundle = new Bundle();
                        bundle.putString(AppConstants.EXTRA_IS_FROM, AppConstants
                                .EXTRA_IS_FROM_FORGOT_PASSWORD);
                        Intent intent = new Intent(this, SetPasswordActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    } else if (isFrom.equalsIgnoreCase(AppConstants.EXTRA_IS_FROM_VERIFICATION)) {
                        if (countDownTimer != null)
                            countDownTimer.cancel();
                        finish();
                    } else {

                        // set launch screen as OtpVerificationActivity
                        Utils.setIntegerPreference(OtpVerificationActivity.this,
                                AppConstants.PREF_LAUNCH_SCREEN_INT, IntegerConstants
                                        .LAUNCH_PROFILE_REGISTRATION);

                        // Redirect to ProfileRegistrationActivity
                        Intent intent = new Intent(this, ProfileRegistrationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    }

                } else {
                    if (confirmOtpResponse != null) {
                        Log.e("error response", confirmOtpResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootOtpVerification,
                                confirmOtpResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootOtpVerification, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

        } else {
//            AppUtils.hideProgressDialog();
            Utils.showErrorSnackBar(this, relativeRootOtpVerification, "" + error
                    .getLocalizedMessage());
        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        imageActionBack = ButterKnife.findById(includeToolbar, R.id.image_action_back);
        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        toolbarOtpVerification = ButterKnife.findById(includeToolbar, R.id.toolbar);
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);

        textToolbarTitle.setText(getString(R.string.title_verification));

        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));
        textVerifyNumber.setTypeface(Utils.typefaceBold(this));
        textEnterOtp.setTypeface(Utils.typefaceRegular(this));
        inputOtp.setTypeface(Utils.typefaceRegular(this));
        textResendCallMsg.setTypeface(Utils.typefaceRegular(this));
        buttonSubmit.setTypeface(Utils.typefaceRegular(this));
        buttonResend.setTypeface(Utils.typefaceRegular(this));
        buttonCallMe.setTypeface(Utils.typefaceRegular(this));

        Utils.setRoundedCornerBackground(buttonSubmit, ContextCompat.getColor
                (OtpVerificationActivity.this, R.color.colorAccent), 5, 0, ContextCompat
                .getColor(OtpVerificationActivity.this, R.color.colorAccent));

        Utils.setRoundedCornerBackground(buttonResend, ContextCompat.getColor
                (OtpVerificationActivity.this, R.color.vividRed), 5, 0, ContextCompat
                .getColor(OtpVerificationActivity.this, R.color.vividRed));

        Utils.setRoundedCornerBackground(buttonCallMe, ContextCompat.getColor
                (OtpVerificationActivity.this, R.color.vividRed), 5, 0, ContextCompat
                .getColor(OtpVerificationActivity.this, R.color.vividRed));

        rippleActionBack.setOnRippleCompleteListener(this);
        rippleResend.setOnRippleCompleteListener(this);
        rippleCallMe.setOnRippleCompleteListener(this);
        rippleSubmit.setOnRippleCompleteListener(this);

        buttonSubmit.setEnabled(false);
        inputOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().length() > 5) {
                    buttonSubmit.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        linearResendCallMe.setVisibility(View.GONE);
//        resendTimer();
    }

    //</editor-fold>

//    private void resendTimer() {
//
//        textResendCallMsg.setVisibility(View.VISIBLE);
//
//        countDownTimer = new CountDownTimer(30000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                textResendCallMsg.setText(String.format(Locale.getDefault(),
//                        "Resend OTP or Call Me after 00:%d sec", millisUntilFinished / 1000));
//            }
//
//            public void onFinish() {
//                textResendCallMsg.setVisibility(View.GONE);
//                linearResendCallMe.setVisibility(View.VISIBLE);
//            }
//        }.start();
//    }

    //<editor-fold desc="Web Service Call">

    private void sendOtp() {

        WsRequestObject otpObject = new WsRequestObject();

        if (isFrom.equalsIgnoreCase(AppConstants.EXTRA_IS_FROM_VERIFICATION))
            otpObject.setCountryCode(selectedCountryCode);
        else
            otpObject.setCountryCode(selectedCountry.getCountryCodeNumber());
        otpObject.setMobileNumber(mobileNumber.replace("+91", ""));
//        otpObject.setDeviceId(getDeviceId());
        if (isFrom.equals(AppConstants.EXTRA_IS_FROM_FORGOT_PASSWORD))
            otpObject.setForgotPassword(1);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), otpObject,
                    null, WsResponseObject.class, WsConstants.REQ_CHECK_NUMBER, getString(R.string
                    .msg_please_wait), false)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT +
                            WsConstants.REQ_CHECK_NUMBER);
            Utils.showSuccessSnackBar(OtpVerificationActivity.this,
                    relativeRootOtpVerification, getString(R.string.msg_success_otp_request));
        } else {
            Utils.showErrorSnackBar(this, relativeRootOtpVerification, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void otpConfirmed(String otp) {

        WsRequestObject otpObject = new WsRequestObject();
        otpObject.setOtp(otp);
//        otpObject.setDeviceId(getDeviceId());
        otpObject.setMobileNumber(mobileNumber.replace("+", ""));
        if (isFrom.equals(AppConstants.EXTRA_IS_FROM_FORGOT_PASSWORD))
            otpObject.setForgotPassword(1);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), otpObject,
                    null, WsResponseObject.class, WsConstants.REQ_OTP_CONFIRMED, getString(R
                    .string.msg_please_wait), false)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT +
                            WsConstants.REQ_OTP_CONFIRMED);
        } else {
            Utils.showErrorSnackBar(this, relativeRootOtpVerification, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    //</editor-fold>
}

