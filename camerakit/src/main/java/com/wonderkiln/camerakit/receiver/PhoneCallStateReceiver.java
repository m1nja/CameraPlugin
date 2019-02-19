package com.wonderkiln.camerakit.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneCallStateReceiver extends BroadcastReceiver {

    public static final String CALL_STATE_DISCONNECT = "call_state_disconnect";
    public static final String CALL_STATE_ANSWER = "call_state_answer";
    public static final String CALL_STATE_RING = "call_state_ring";
    @Override
    public void onReceive(final Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            //如果是去电（拨出）
        }else{
            //查了下android文档，貌似没有专门用于接收来电的action,所以，非去电即来电
            TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
            //设置一个监听器
            tm.listen(new PhoneStateListener(){
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    super.onCallStateChanged(state, incomingNumber);
                    switch(state){
                        case TelephonyManager.CALL_STATE_IDLE:
                            context.sendBroadcast(new Intent(CALL_STATE_DISCONNECT));
                            break;
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            context.sendBroadcast(new Intent(CALL_STATE_ANSWER));
                            break;
                        case TelephonyManager.CALL_STATE_RINGING:
                            context.sendBroadcast(new Intent(CALL_STATE_RING));
                            break;
                    }
                }
            }, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
}
