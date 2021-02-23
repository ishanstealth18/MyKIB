package stealth.ishan.mykib;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class BLEDeviceDetails extends AppCompatActivity {

    private static final String logTag = BLEDeviceDetails.class.getSimpleName();
    private TextView bleTitleView;
    private TextView bleDeviceText;
    private TextView bleDeviceStatus;
    private TextView deviceNameTextView;
    private TextView deviceStatusTextView;
    private TextView deviceRssiTextView;
    private TextView bleDeviceRssiVal;
    private TextView serviceUUIDTextView;
    private TextView serviceUUIDValueView;
    private TextView commandCharUUIDTextView;
    private TextView commandCharUUIDValueView;
    private TextView rssiDialogValue;
    private TextView insideRSSIVal;
    private Button resetCommandBtn;
    private Button unlockBtn;
    private Bundle bundle;
    private String getBleDeviceName;
    private int getBleStatus;
    private int getBleDeviceRssi;
    private String getServiceUUID;
    private String getCommandCharUUID;
    private Handler mHandler = new Handler();
    private int bluetoothStateConnection = 0;
    private int bleRSSI = 0;
    private BluetoothGatt bGatt = BleScan.getInstance().bluetoothGatt;
    private BluetoothLeScanner bleScanner;
    private boolean mScan;
    private BluetoothDevice bleDevice;
    private String deviceName;
    private AlertDialog.Builder builder;
    private int insideBLESetValue = 0;
    private int outsideBLESetValue = 0;
    private TextView outsideRSSIValue;
    private TextView devicePosition;
    private TextView devicePositionState;
    private boolean setInsideCalibrationFlag = false;
    private boolean setOutsideCalibrationFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_l_e_device_details);
        bleTitleView = findViewById(R.id.bleDetailsTextView);
        bleDeviceText = findViewById(R.id.deviceNameText);
        bleDeviceStatus = findViewById(R.id.deviceStatus);
        deviceNameTextView = findViewById(R.id.deviceName);
        deviceStatusTextView = findViewById(R.id.deviceStatusText);
        deviceRssiTextView = findViewById(R.id.rssi_text_view);
        bleDeviceRssiVal = findViewById(R.id.rssi_value_text);
        resetCommandBtn = findViewById(R.id.resetButton);
        serviceUUIDTextView = findViewById(R.id.serviceUUIDText);
        serviceUUIDValueView = findViewById(R.id.serviceUUIDValue);
        commandCharUUIDTextView = findViewById(R.id.commandCharText);
        commandCharUUIDValueView = findViewById(R.id.commandCharUUIDValue);
        outsideRSSIValue = findViewById(R.id.outsideCalibrationValue);
        rssiDialogValue = findViewById(R.id.rssiDialogValue);
        devicePosition = findViewById(R.id.devicePostionStatus);
        devicePositionState = findViewById(R.id.devicePositionValue);

        insideRSSIVal = findViewById(R.id.insideRSSIValue);
        unlockBtn = findViewById(R.id.unlockButton);

        Intent getDeviceDetailsIntent = getIntent();
        bundle = getDeviceDetailsIntent.getExtras();
        getDeviceDetails();

        builder = new AlertDialog.Builder(BLEDeviceDetails.this);

    }

    /**
     * This function will set and get the values from another activity
     */
    public void getDeviceDetails() {
        getBleDeviceName = bundle.getString("Device Name");
        getBleStatus = bundle.getInt("Device Status");
        getBleDeviceRssi = bundle.getInt("Device RSSI");
        getServiceUUID = bundle.getString("Service UUID");
        getCommandCharUUID = bundle.getString("Command Char UUID");
        deviceNameTextView.setText(getBleDeviceName);
        deviceStatusTextView.setText(String.valueOf(getBleStatus));
        //bleDeviceRssiVal.setText(String.valueOf(getBleDeviceRssi));
        serviceUUIDValueView.setText(getServiceUUID);
        commandCharUUIDValueView.setText(getCommandCharUUID);
    }

    /**
     * This function will send the Lock command when clicked
     *
     * @param view
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void sendLockCommand(View view) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        Commands.getInstance().lockCommand();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void sendUnlockCommand(View view) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Commands.getInstance().unlockCommand();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setInsideCalibration(View view) {
        setInsideCalibrationFlag = true;
        startBleScan();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setOutsideCalibration(View view) {
        setOutsideCalibrationFlag = true;
        startBleScan();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void disconnectBLEDevice(View view) {

        bGatt.disconnect();
        Log.d(logTag, "Connection status: " + BleScan.getInstance().connectionState);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(logTag, "Connection status after delay: " + BleScan.getInstance().connectionState);
            }
        }, 5000);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startBleScan() {
        bleScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        Log.d(logTag, "mScan value: " + mScan);
        if (!mScan) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScan = false;
                    //bleScanner.stopScan(leScanCallback);
                }
            }, 10000);

            mScan = true;
            bleScanner.startScan(leScanCallback);
        }
    }

    /**
     * This function will give call back with results of scanning
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    // Device scan callback.
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    Log.d(logTag, "Device address: " + result.getDevice().getAddress() + " " + "Device name: " + result.getDevice().getName());
                    deviceName = result.getDevice().getName();
                    if (deviceName == null) {
                        deviceName = "NULL";
                    }
                    //Checking if BLE device is KIB, if yes, update the RSSI value on the UI continuously.
                    if (deviceName.equals("LW  KIB")) {
                        Log.d(logTag, "KIB detected!!");
                        bleDevice = result.getDevice();
                        bleRSSI = result.getRssi();
                        Log.d(logTag, "Rssi: " + bleRSSI);
                        bleDeviceRssiVal.setText(String.valueOf(bleRSSI));
                        //Logic to check if user is trying to set Inside calibration or Outside calibration.
                        if(setInsideCalibrationFlag == true)
                        {
                            insideRSSIVal.setText(String.valueOf(bleRSSI));
                        }
                        else
                        {
                            outsideRSSIValue.setText(String.valueOf(bleRSSI));
                        }
                    }
                }
            };

    /**
     * This function will set the rssi value when user will press Set button on UI.
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setCalibrationValues(View view) {
        //Logic to check if User is setting Inside/Outside calibration
        if(setInsideCalibrationFlag == true)
        {
            insideBLESetValue = bleRSSI;
            Log.d(logTag, "Inside Calibration final value : " +insideBLESetValue);
            setInsideCalibrationFlag = false;
        }
        else
        {
            outsideBLESetValue = bleRSSI;
            Log.d(logTag, "Outside Calibration final value : " +outsideBLESetValue);
            setOutsideCalibrationFlag = false;
        }
        //Once rssi value is set, stop the scan.
        //bleScanner.stopScan(leScanCallback);

        if(bleRSSI <= insideBLESetValue)
        {
            devicePositionState.setText("Inside Car");
        }
        else if(bleRSSI > insideBLESetValue && bleRSSI < outsideBLESetValue)
        {
            devicePositionState.setText("Outside Car");
        }
    }


}