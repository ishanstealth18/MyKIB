package stealth.ishan.mykib;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
    private Button resetCommandBtn;
    private Button unlockBtn;
    private Bundle bundle;
    private String getBleDeviceName;
    private int getBleStatus;
    private int getBleDeviceRssi;
    private String getServiceUUID;
    private String getCommandCharUUID;
    private Handler mHandler;

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
        unlockBtn = findViewById(R.id.unlockButton);

        Intent getDeviceDetailsIntent = getIntent();
        bundle = getDeviceDetailsIntent.getExtras();
        getDeviceDetails();

    }

    /**
     * This function will set and get the values from another activity
     */
    public void getDeviceDetails()
    {
        getBleDeviceName = bundle.getString("Device Name");
        getBleStatus = bundle.getInt("Device Status");
        getBleDeviceRssi = bundle.getInt("Device RSSI");
        getServiceUUID = bundle.getString("Service UUID");
        getCommandCharUUID = bundle.getString("Command Char UUID");
        deviceNameTextView.setText(getBleDeviceName);
        deviceStatusTextView.setText(String.valueOf(getBleStatus));
        bleDeviceRssiVal.setText(String.valueOf(getBleDeviceRssi));
        serviceUUIDValueView.setText(getServiceUUID);
        commandCharUUIDValueView.setText(getCommandCharUUID);
    }

    /**
     * This function will send the Lock command when clicked
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

    public void setInsideCalibration(View view) {
    }

    public void setOutsideCalibration(View view) {
    }
}