package stealth.ishan.mykib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RoutingSessionInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class RssiReceiver extends BroadcastReceiver {
    private static final String logTag = RssiReceiver.class.getSimpleName();

    private int liveRssi = 0;
    private Toast toast;
    private RssiReceiver rssireceiver;
    Context context;

    public Context getContext() {
        if(BLEDeviceDetails.getInstance().getApplicationContext() != null)
        {
            return BLEDeviceDetails.getInstance().getApplicationContext();
        }
        else
        {
            Log.d(logTag, "Application context is null");
            return null;
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(logTag, "Inside broadcast receive method");
        Bundle getRSSIBundle = intent.getExtras();
        if(intent.getAction().equals("RSSIBROADCAST"))
        {
            int insideRssi = getRSSIBundle.getInt("INSIDE_THRESHOLD");
            int outideRssi = getRSSIBundle.getInt("OUTSIDE_THRESHOLD");
            liveRssi = getRSSIBundle.getInt("RSSI_VALUE");
            Log.d(logTag, "Live rssi: " +liveRssi +" "  +"Inside rssi: " +insideRssi +" " +"Outside rssi: " + outideRssi);
            Log.d(logTag, "Rssi received : " +liveRssi);
            if(liveRssi >= insideRssi && liveRssi > outideRssi)
            {
                Log.d(logTag, "Inside Car:  Live rssi: " +liveRssi +" " +"Inside threshold: " +insideRssi);
                BLEDeviceDetails.getInstance().updatePhonePosition("Inside Car");
            }
            else if(liveRssi < insideRssi && liveRssi >= outideRssi)
            {
                Log.d(logTag, "Outside Car: Live rssi: " +liveRssi +" " +"Outside threshold: " +outideRssi);
                BLEDeviceDetails.getInstance().updatePhonePosition("Outside Car");
            }
            else if(liveRssi < outideRssi && liveRssi < insideRssi)
            {
                Log.d(logTag, "Connected: Live rssi: " +liveRssi );
                BLEDeviceDetails.getInstance().updatePhonePosition("Connected");
            }
        }
    }

}
