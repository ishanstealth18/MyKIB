package stealth.ishan.mykib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class BLEDeviceDetails extends AppCompatActivity {

    private static final String logTag = BLEDeviceDetails.class.getSimpleName();
    private TextView deviceNameTextView;
    private TextView deviceStatusTextView;
    private Bundle bundle;
    private String getBleDeviceName;
    private int getBleStatus;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_l_e_device_details);
        deviceNameTextView = findViewById(R.id.deviceName);
        deviceStatusTextView = findViewById(R.id.deviceStatusText);
        Intent getDeviceDetailsIntent = getIntent();
        bundle = getDeviceDetailsIntent.getExtras();


        getDeviceDetails();

    }

    public void getDeviceDetails()
    {

        getBleDeviceName = bundle.getString("Device Name");
        getBleStatus = bundle.getInt("Device Status");

        deviceNameTextView.setText(getBleDeviceName);
        deviceStatusTextView.setText(String.valueOf(getBleStatus));
    }
}