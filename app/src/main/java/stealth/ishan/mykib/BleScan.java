package stealth.ishan.mykib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;



public class BleScan extends AppCompatActivity {

    private BluetoothLeScanner bluetoothLeScanner;
    private boolean mScanning;
    private LeDeviceListAdapter adapterObj;
    private Handler handler = new Handler();
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private static ArrayList<BluetoothDevice> mLeDevices;
    private String bleDeviceName;
    private BluetoothDevice bleDevice;


    private static final String logTag = BleScan.class.getSimpleName();
    Context context;

    private BleScan()
    {

    }

    private static BleScan ourInstance = new BleScan();

    public static ArrayList getBleList()
    {
        return mLeDevices;
    }

    static BleScan getInstance() {
        if(ourInstance == null)
        {
            ourInstance = new BleScan();
        }
        return ourInstance;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scanLeDevice()
    {
        bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        Log.d(logTag, "mScan value: " +mScanning);
        if(!mScanning)
        {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
        }
        else
        {
            Log.d(logTag, "Stop scan");
            mScanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    // Device scan callback.
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    Log.d(logTag, "Device address: " +result.getDevice().getAddress() +" " +"Device name: " +result.getDevice().getName());
                    bleDeviceName = result.getDevice().getName();
                    if(bleDeviceName == null)
                    {
                        bleDeviceName = "NULL";
                    }
                    if(bleDeviceName.equals("LW  KIB"))
                    {
                        Log.d(logTag, "KIB detected!!");
                        adapterObj = new LeDeviceListAdapter();
                        bleDevice = result.getDevice();
                        mLeDevices.add(bleDevice);
                        Log.d(logTag, "Device array: " +mLeDevices +" " +"Array count: " +mLeDevices.size());
                        bluetoothLeScanner.stopScan(leScanCallback);
                    }
                }
            };


    // Adapter for holding devices found through scanning.
    public class LeDeviceListAdapter extends BaseAdapter {

        private LayoutInflater mInflator;
        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
        }
        public void addDevice(BluetoothDevice device) {
            Log.d(logTag, "addDevice: " +device);
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }
        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }
        public void clear() {
            mLeDevices.clear();
        }
        @Override
        public int getCount() {
            return mLeDevices.size();
        }
        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }
        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
            {
                Log.d(logTag, "Convert view is null!!");
                convertView = mInflator.inflate(R.layout.activity_main,parent,false);
            }
            Log.d(logTag, "Inside getView function");

           // ( (TextView)convertView.findViewById(R.id.bleDeviceNameText)).setText((Integer) getItem(position));
            return convertView;
        }



    }


}
