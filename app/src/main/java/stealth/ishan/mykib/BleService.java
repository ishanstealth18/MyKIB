package stealth.ishan.mykib;

import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BleService  extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
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
