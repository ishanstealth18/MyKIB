package stealth.ishan.mykib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
    private Bundle bundle;
    private String getBleDeviceName;
    private int getBleStatus;
    private int getBleDeviceRssi;
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
        Intent getDeviceDetailsIntent = getIntent();
        bundle = getDeviceDetailsIntent.getExtras();


        getDeviceDetails();

    }

    public void getDeviceDetails()
    {
        getBleDeviceName = bundle.getString("Device Name");
        getBleStatus = bundle.getInt("Device Status");
        getBleDeviceRssi = bundle.getInt("Device RSSI");
        deviceNameTextView.setText(getBleDeviceName);
        deviceStatusTextView.setText(String.valueOf(getBleStatus));
        bleDeviceRssiVal.setText(String.valueOf(getBleDeviceRssi));
    }
}