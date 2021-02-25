package stealth.ishan.mykib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RoutingSessionInfo;
import android.os.Bundle;
import android.util.Log;

public class RssiReceiver extends BroadcastReceiver {
    private static final String logTag = RssiReceiver.class.getSimpleName();

    private int liveRssi = 0;

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
            if(liveRssi <= insideRssi)
            {
                Log.d(logTag, "Live rssi: " +liveRssi +" " +"Inside threshold: " +insideRssi);

                BLEDeviceDetails.getInstance().devicePositionState.setText("Inside");
            }
            else if(liveRssi > insideRssi && liveRssi < outideRssi)
            {
                Log.d(logTag, "Live rssi: " +liveRssi +" " +"Outside threshold: " +outideRssi);
                BLEDeviceDetails.getInstance().devicePositionState.setText("Outside Car");
            }
            else
            {
                Log.d(logTag, "Outside range, Live rssi: " +liveRssi );
            }
        }
    }

}
