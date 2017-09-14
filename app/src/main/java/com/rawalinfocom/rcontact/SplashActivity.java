package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.helper.Utils;

public class SplashActivity extends BaseActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        switch (Utils.getIntegerPreference(SplashActivity.this, AppConstants
                .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY)) {
            case IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY:
                SPLASH_TIME_OUT = 1500;
                break;
            default:
                SPLASH_TIME_OUT = 300;
                break;
        }

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

                /*startActivityIntent(SplashActivity.this, TutorialActivity.class, null);
                finish();*/

                if (Utils.getIntegerPreference(SplashActivity.this, AppConstants
                        .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY) ==
                        IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY) {
                    startActivityIntent(SplashActivity.this, TutorialActivity.class, null);
                    finish();

                } else if (Utils.getIntegerPreference(SplashActivity.this, AppConstants
                        .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY) ==
                        IntegerConstants.LAUNCH_TERMS_CONDITIONS_ACTIVITY) {
                    startActivityIntent(SplashActivity.this, TermsConditionsActivity.class, null);
                    finish();

                } else if (Utils.getIntegerPreference(SplashActivity.this, AppConstants
                        .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY) ==
                        IntegerConstants.LAUNCH_MOBILE_REGISTRATION) {
                    startActivityIntent(SplashActivity.this, MobileNumberRegistrationActivity
                            .class, null);
                    finish();

                } else if (Utils.getIntegerPreference(SplashActivity.this, AppConstants
                        .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY) ==
                        IntegerConstants.LAUNCH_PROFILE_REGISTRATION) {
                    startActivityIntent(SplashActivity.this, ProfileRegistrationActivity.class,
                            null);
                    finish();

                } else if (Utils.getIntegerPreference(SplashActivity.this, AppConstants
                        .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY) ==
                        IntegerConstants.LAUNCH_SET_PASSWORD) {
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.EXTRA_IS_FROM, "splash");
                    startActivityIntent(SplashActivity.this, SetPasswordActivity.class, bundle);
                    finish();

                } else if (Utils.getIntegerPreference(SplashActivity.this, AppConstants
                        .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY) ==
                        IntegerConstants.LAUNCH_ENTER_PASSWORD) {
                    startActivityIntent(SplashActivity.this, EnterPasswordActivity.class, null);
                    finish();

                } else if (Utils.getIntegerPreference(SplashActivity.this, AppConstants
                        .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY) ==
                        IntegerConstants.LAUNCH_RE_LOGIN_PASSWORD) {

                    Intent intent = new Intent(SplashActivity.this, ReLoginEnterPasswordActivity
                            .class);
                    intent.putExtra(AppConstants.PREF_IS_FROM, AppConstants.PREF_FORGOT_PASSWORD);
                    startActivity(intent);
                    finish();

                } else {
                    if (Utils.getBooleanPreference(SplashActivity.this, AppConstants
                            .KEY_IS_RESTORE_DONE, false)) {
                        // Redirect to MainActivity
                        startActivityIntent(SplashActivity.this, MainActivity.class, null);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    } else {
                        // Redirect to RestorationActivity
                        startActivityIntent(SplashActivity.this, RestorationActivity.class, null);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    }
                }
            }
        }, SPLASH_TIME_OUT);
    }
}
