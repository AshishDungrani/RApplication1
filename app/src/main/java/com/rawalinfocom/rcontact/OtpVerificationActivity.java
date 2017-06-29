package com.rawalinfocom.rcontact;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.rawalinfocom.rcontact.asynctasks.AsyncGetDeviceToken;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableOtpLogDetails;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Country;
import com.rawalinfocom.rcontact.model.OtpLog;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;
import com.rawalinfocom.rcontact.services.OtpTimerService;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rawalinfocom.rcontact.helper.Utils.PLAY_SERVICES_RESOLUTION_REQUEST;

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

    String mobileNumber;
    Country selectedCountry;
    private String isFrom = "";

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

//        if (Utils.getStringPreference(this, AppConstants.PREF_DEVICE_TOKEN_ID, "").equals(""))
//            new AsyncGetDeviceToken(this).execute();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        /*mobileNumber = bundle.getString(AppConstants.EXTRA_MOBILE_NUMBER);
        selectedCountry = (Country) bundle.getSerializable(AppConstants.EXTRA_OBJECT_COUNTRY);*/

        mobileNumber = Utils.getStringPreference(OtpVerificationActivity.this, AppConstants
                .PREF_REGS_MOBILE_NUMBER, "");
        selectedCountry = (Country) Utils.getObjectPreference(OtpVerificationActivity.this,
                AppConstants.PREF_SELECTED_COUNTRY_OBJECT, Country.class);

        if (selectedCountry == null) {
            selectedCountry = new Country();
        }

        init();

        if (bundle != null) {
            isFrom = bundle.getString(AppConstants.EXTRA_IS_FROM, "");
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_action_back:

                finish();

//                if (isFrom.equals("mobile")) {
//                } else if (isFrom.equals("forgot_pass")) {
//                }

                break;

            case R.id.ripple_submit:

                if (inputOtp.getText().toString().equals("")) {
                    Utils.showErrorSnackBar(OtpVerificationActivity.this,
                            relativeRootOtpVerification, getString(R.string.msg_otp_error));
                } else {
                    otpConfirmed(inputOtp.getText().toString());
//                verifyOtp(inputOtp.getText().toString());
                }
                break;

            case R.id.ripple_resend:
//                stopService(new Intent(OtpVerificationActivity.this, OtpTimerService.class));
                inputOtp.setText("");
                sendOtp();
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
                } else {
                    if (otpDetailResponse != null) {
                        Log.e("error response", otpDetailResponse.getMessage());
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

                    Utils.setObjectPreference(OtpVerificationActivity.this, AppConstants
                            .PREF_REGS_USER_OBJECT, userProfile);
                    Utils.setStringPreference(OtpVerificationActivity.this, AppConstants
                            .PREF_USER_PM_ID, userProfile.getPmId());
//                    Utils.setStringPreference(OtpVerificationActivity.this, AppConstants
//                            .PREF_ACCESS_TOKEN, getDeviceTokenId() + "_" + userProfile.getPmId());

                    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

                    if (isFrom.equals("forgot_pass")) {

                        // set launch screen as OtpVerificationActivity
                        Utils.setIntegerPreference(OtpVerificationActivity.this,
                                AppConstants.PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_MOBILE_REGISTRATION);

                        // Redirect to SetPassWordActivity
                        Bundle bundle = new Bundle();
                        bundle.putString(AppConstants.EXTRA_IS_FROM, "forgot_pass");
                        Intent intent = new Intent(this, SetPasswordActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();

//                        Intent intent = new Intent(this, SetPasswordActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.enter, R.anim.exit);

                    } else {

                        // set launch screen as OtpVerificationActivity
                        Utils.setIntegerPreference(OtpVerificationActivity.this,
                                AppConstants.PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_PROFILE_REGISTRATION);

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

        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        textVerifyNumber.setTypeface(Utils.typefaceRegular(this));
        textEnterOtp.setTypeface(Utils.typefaceRegular(this));
        inputOtp.setTypeface(Utils.typefaceRegular(this));
        buttonSubmit.setTypeface(Utils.typefaceSemiBold(this));
        buttonResend.setTypeface(Utils.typefaceSemiBold(this));

        rippleActionBack.setOnRippleCompleteListener(this);
        rippleResend.setOnRippleCompleteListener(this);
        rippleSubmit.setOnRippleCompleteListener(this);
    }

//    private void startOtpService() {
//        otpServiceIntent = new Intent(this, OtpTimerService.class);
//        otpServiceIntent.putExtra(AppConstants.EXTRA_MOBILE_NUMBER, mobileNumber);
//        otpServiceIntent.putExtra(AppConstants.EXTRA_OTP_SERVICE_END_TIME, (long) (AppConstants
//                .OTP_VALIDITY_DURATION * 60 * 1000));
//        otpServiceIntent.putExtra(AppConstants.EXTRA_CALL_MSP_SERVER, true);
//        startService(otpServiceIntent);
//    }

//    private void verifyOtp(String message) {
//        if (StringUtils.length(message) == AppConstants.OTP_LENGTH) {
//            TableOtpLogDetails tableOtpLogDetails = new TableOtpLogDetails(databaseHandler);
//            OtpLog otpLog = tableOtpLogDetails.getLastOtpDetails();
//            if (otpLog.getOldValidityFlag().equalsIgnoreCase("1")) {
//                if (otpLog.getOldOtp().equals(message)) {
//                    stopService(new Intent(OtpVerificationActivity.this, OtpTimerService.class));
//                    otpConfirmed(otpLog);
//                } else {
//                    Utils.hideSoftKeyboard(this, inputOtp);
//                    Utils.showErrorSnackBar(OtpVerificationActivity.this,
//                            relativeRootOtpVerification, getString(R.string
//                                    .error_otp_verification_fail));
//                }
//            } else {
//                Utils.showErrorSnackBar(OtpVerificationActivity.this,
//                        relativeRootOtpVerification, getString(R.string.error_otp_expired));
//            }
//        } else {
//            Utils.showErrorSnackBar(OtpVerificationActivity.this,
//                    relativeRootOtpVerification, getString(R.string
//                            .error_otp_length_incorrect, AppConstants.OTP_LENGTH));
//        }
//    }

    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("Reg key Error", "This device is not supported.");
                // finish();
            }
            return false;
        }

        return true;
    }

    private boolean isOtpServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer
                .MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void sendOtp() {

//        Utils.showProgressDialog(OtpVerificationActivity.this, getString(R.string.msg_please_wait), false);

        WsRequestObject otpObject = new WsRequestObject();
        otpObject.setCountryCode(selectedCountry.getCountryCodeNumber());
        otpObject.setMobileNumber(mobileNumber);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), otpObject,
                    null, WsResponseObject.class, WsConstants.REQ_CHECK_NUMBER, getString(R.string
                    .msg_please_wait), false)
                    .execute(WsConstants.WS_ROOT + WsConstants.REQ_CHECK_NUMBER);
            Utils.showSuccessSnackBar(OtpVerificationActivity.this,
                    relativeRootOtpVerification, getString(R.string.msg_success_otp_request));
        } else {
            Utils.showErrorSnackBar(this, relativeRootOtpVerification, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void otpConfirmed(String otp) {

        WsRequestObject otpObject = new WsRequestObject();
//        otpObject.setPmId(Integer.parseInt(otpLog.getRcProfileMasterPmId()));
//        otpObject.setStatus(AppConstants.OTP_CONFIRMED_STATUS);
//        otpObject.setLdOtpDeliveredTimeFromCloudToDevice(otpLog.getOldMspDeliveryTime());
//        otpObject.setOtpGenerationTime(otpLog.getOldGeneratedAt());
        otpObject.setOtp(otp);
        otpObject.setMobileNumber(mobileNumber);
//        otpObject.setAccessToken(getDeviceTokenId() + "_" + otpLog.getRcProfileMasterPmId());

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), otpObject,
                    null, WsResponseObject.class, WsConstants.REQ_OTP_CONFIRMED, getString(R
                    .string.msg_please_wait), false).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_OTP_CONFIRMED);
        } else {
            Utils.showErrorSnackBar(this, relativeRootOtpVerification, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    //</editor-fold>
}

