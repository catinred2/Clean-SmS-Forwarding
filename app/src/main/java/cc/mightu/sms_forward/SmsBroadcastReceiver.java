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
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(action)) {

            Log.i(Constants.LOG_TAG, "on receive," + intent.getAction());

            String number = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("number", "");
            if (number == "") {
                Log.i(Constants.LOG_TAG, "phone number not set. ignore this one.");
                return;
            }


            SmsMessage[] smsList = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            if (smsList == null || smsList.length <= 0){
                Log.e(Constants.LOG_TAG,"parse Sms Message From Intent Error");
                return;
            }
            String address = smsList[0].getOriginatingAddress();
            StringBuilder message = new StringBuilder();
            message.append("[").append(address).append("]");
            for (SmsMessage smsMessage : smsList) {

                String messageBody = smsMessage.getMessageBody();
                message.append(messageBody);
            }
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> dividedMessages = sms.divideMessage(message.toString());
            sms.sendMultipartTextMessage(number, null, dividedMessages, null, null);

        } else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Intent startIntent = new Intent(context, MainActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent);
        }
    }
}
