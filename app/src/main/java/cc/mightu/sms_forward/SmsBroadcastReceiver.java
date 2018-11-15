package cc.mightu.sms_forward;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "ForwardSMSService";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(action)) {

            Log.i("sms", "on receive," + intent.getAction());

            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {

                String messageBody = smsMessage.getMessageBody();
                String emailFrom = smsMessage.getEmailFrom();
                String address = smsMessage.getOriginatingAddress();
                Log.i(LOG_TAG, "body: " + messageBody);
                Log.i(LOG_TAG, "address: " + address);

                String message = "[" + address + "] " + messageBody;

                String number = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("number", "");
                if (number == "") {
                    Log.i("sms", "phone number not set. ignore this one.");
                    return;
                }
                Log.i(LOG_TAG, "sending to " + number);

                Log.i(LOG_TAG, "message send:" + message);
                SmsManager sms = SmsManager.getDefault();
                ArrayList<String> dividedMessages = sms.divideMessage(message);
                sms.sendMultipartTextMessage(number, null, dividedMessages, null, null);
            }

        } else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Intent startIntent = new Intent(context, MainActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent);
        }
    }
}
