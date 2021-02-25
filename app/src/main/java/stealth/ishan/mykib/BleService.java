package stealth.ishan.mykib;

import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class BleService  extends Service {

    private static final String logTag = BleService.class.getSimpleName();
    BLEDeviceDetails bleObj = new BLEDeviceDetails();
    private Handler handler;
    RssiReceiver rssiObj = new RssiReceiver();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(logTag, "Starting ble background services..");
        handler = new Handler();
        bleObj.startBleScan();
        rssiObj.onReceive(BLEDeviceDetails.getInstance().getApplicationContext(),intent);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
