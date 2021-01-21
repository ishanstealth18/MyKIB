package stealth.ishan.mykib;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ListAdapter;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
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
    private Intent intent;
    private AlertDialog.Builder builder;
    private Bundle bundle;
    private String bleDeviceName;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanBtn = (Button) findViewById(R.id.scan_ble_button);
        bleDeviceListView = (ListView) findViewById(R.id.ble_device_list_view);
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        builder = new AlertDialog.Builder(MainActivity.this);
        bleDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                builder.setMessage("Do you want to connect this device?").setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        toast = Toast.makeText(MainActivity.this, "Connecting....",Toast.LENGTH_SHORT);
                        toast.show();
                        BleScan.getInstance().connectBleDevice();
                        //Inserting delay so that BLE connection is complete
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                intent = new Intent(MainActivity.this, BLEDeviceDetails.class);
                                startActivity(intent);
                                bundle = new Bundle();
                                bundle.putString("Device Name", BleScan.getInstance().bleDeviceName);
                                bundle.putInt("Device Status", BleScan.getInstance().connectionState);
                                bundle.putInt("Device RSSI", BleScan.getInstance().bleDeviceRSSI);
                                bundle.putString("Service UUID" , BleScan.getInstance().service);
                                bundle.putString("Command Char UUID" , BleScan.getInstance().commandCharacteristic);
                                Intent sendDeviceIntent = new Intent(MainActivity.this, BLEDeviceDetails.class);
                                sendDeviceIntent.putExtras(bundle);
                                startActivity(sendDeviceIntent);
                            }
                        },5000);


                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alert = builder.create();
                alert.setTitle("BLE Device: ");
                alert.show();
            }
        });

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