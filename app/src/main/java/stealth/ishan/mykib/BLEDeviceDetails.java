package stealth.ishan.mykib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
        Intent getDeviceDetailsIntent = getIntent();
        bundle = getDeviceDetailsIntent.getExtras();


        getDeviceDetails();

    }

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

    public void sendResetCommand(View view) {
    }
}