package stealth.ishan.mykib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
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
import android.widget.Toast;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static android.bluetooth.BluetoothClass.*;


public class BleScan {

    private BluetoothLeScanner bluetoothLeScanner;
    private boolean mScanning;
    private LeDeviceListAdapter adapterObj;
    private Handler handler = new Handler();
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private static ArrayList<BluetoothDevice> mLeDevices;
    private List<BluetoothGattService> gattServiceList;
    List<BluetoothGattCharacteristic> gattCharacteristicList;
    public String bleDeviceName;
    private BluetoothDevice bleDevice;
    private BluetoothGatt bluetoothGatt;
    private Toast toast;
    private UUID serviceUUID = null;
    private UUID charUUID = null;


    public int connectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED = 2;
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";


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


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void connectBleDevice()
    {
        bluetoothGatt = bleDevice.connectGatt(context,false, gattCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                connectionState = STATE_CONNECTED;
                //broadcastUpdate(intentAction);
                Log.d(logTag, "Connected to GATT server.");
                Log.d(logTag, "Attempting to start service discovery:" +
                        bluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                connectionState = STATE_DISCONNECTED;
                Log.d(logTag, "Disconnected from GATT server.");
                //broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                Log.d(logTag, "onServicesDiscovered received: " + status);
                Log.d(logTag, "Services discovered: " +gatt.getServices());
                gattServiceList = gatt.getServices();
            } else {
                Log.d(logTag, "onServicesDiscovered received: " + status);
            }

            if(gattServiceList.size() > 0)
            {
                Log.d(logTag, "Gatt services identified: ");
                for(BluetoothGattService gattService : gattServiceList)
                {
                    serviceUUID = gattService.getUuid();
                    Log.d(logTag, "Service UUID: " +serviceUUID);
                    gattCharacteristicList = gattService.getCharacteristics();
                    //Log.d(logTag, "Characteristics: " +gattCharacteristicList);
                }

                if(gattCharacteristicList.size() > 0)
                {
                    Log.d(logTag, "Gatt characteristics identified: ");
                    for(BluetoothGattCharacteristic gatChar : gattCharacteristicList)
                    {
                        charUUID = gatChar.getUuid();
                        Log.d(logTag, "Service Characteristics: " + charUUID);
                    }
                }
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
