package stealth.ishan.mykib;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ListAdapter;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private static final String logTag = MainActivity.class.getSimpleName();
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private Toast toast;
    private Context context;
    private int toastDuration = Toast.LENGTH_SHORT;
    private Button scanBtn;
    public ListView bleDeviceListView;
    private TextView bleDeviceText;
    ArrayList<BluetoothDevice> bleList = new ArrayList<>();
    private Handler mHandler = new Handler();
    private ArrayAdapter<BluetoothDevice> a;
    private TextView bleDeviceTextView;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanBtn = (Button) findViewById(R.id.scan_ble_button);
        bleDeviceListView = (ListView) findViewById(R.id.ble_device_list_view);
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bleDeviceListView.setOnClickListener();
    }


    public void checkBluetoothState()
    {
        context = getApplicationContext();
        if(bluetoothAdapter == null || (!bluetoothAdapter.isEnabled()))
        {
            Log.d(logTag,"Turning bluetooth on....");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,1);
            toast = Toast.makeText(context, "Turning Bluetooth ON", toastDuration);
            toast.show();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void scanBLEDevices(View view) throws InterruptedException {
        Log.d(logTag, "Scan button pressed!!");
        checkBluetoothState();
        if(bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BleScan.getInstance().scanLeDevice();
            }
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bleList = BleScan.getBleList();
                Log.d(logTag, "Array size: " +bleList.size() +" " +"Device: " +bleList.get(0));
                a = new ArrayAdapter<BluetoothDevice>(MainActivity.this, android.R.layout.simple_list_item_1, bleList);
                bleDeviceListView.setAdapter(a);

            }
        },10000);

    }
}