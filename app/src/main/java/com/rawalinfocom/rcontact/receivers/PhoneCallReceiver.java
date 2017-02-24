package com.rawalinfocom.rcontact.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.rawalinfocom.rcontact.calllog.CallLogFragment;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.model.CallLogType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Aniruddh on 22/02/17.
 */

public class PhoneCallReceiver extends BroadcastReceiver{

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing

    public PhoneCallReceiver() {
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        AppConstants.isFromReceiver = true;
        try
        {
            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            }
            else{
                String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                int state = 0;
                if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                    state = TelephonyManager.CALL_STATE_IDLE;
                }
                else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                }
                else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                    state = TelephonyManager.CALL_STATE_RINGING;
                }


                onCallStateChanged(context, state, number);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Derived classes should override these to respond to specific events of interest
    protected void onIncomingCallStarted(Context ctx, String number, Date start){}
    protected void onOutgoingCallStarted(Context ctx, String number, Date start){}
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end){}
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end){}
    protected void onMissedCall(Context ctx, String number, Date start){}

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number) {
        if(lastState == state){
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:

                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss
                    onMissedCall(context, savedNumber, callStartTime);
                    CallLogFragment.callLogTypeReceiver.setNumber(savedNumber);
                    CallLogFragment.callLogTypeReceiver.setType(3);
                    String logDate = new SimpleDateFormat("MMMM dd, hh:mm a").format(callStartTime);
                    CallLogFragment.callLogTypeReceiver.setLogDate(logDate);

                }
                else if(isIncoming){
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                    CallLogFragment.callLogTypeReceiver.setNumber(savedNumber);
                    CallLogFragment.callLogTypeReceiver.setType(1);
                    String logDate = new SimpleDateFormat("MMMM dd, hh:mm a").format(callStartTime);
                    CallLogFragment.callLogTypeReceiver.setLogDate(logDate);

                }
                else{

                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                    CallLogFragment.callLogTypeReceiver.setNumber(savedNumber);
                    CallLogFragment.callLogTypeReceiver.setType(2);
                    String logDate = new SimpleDateFormat("MMMM dd, hh:mm a").format(callStartTime);
                    CallLogFragment.callLogTypeReceiver.setLogDate(logDate);

                }
                break;
        }
        lastState = state;
    }

}