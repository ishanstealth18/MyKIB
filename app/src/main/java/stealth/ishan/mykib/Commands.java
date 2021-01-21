package stealth.ishan.mykib;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Commands {

    private final static String logTag = Commands.class.getSimpleName();
    List<BluetoothGattCharacteristic> bGattCharacteristicList;
    private BluetoothGatt bGatt;
    private BluetoothGattCharacteristic bWriteChar;
    private UUID writeCharUUId;

    private Commands()
    {

    }

    private static Commands commandInstance = new Commands();


    static Commands getInstance() {
        if(commandInstance == null)
        {
            commandInstance = new Commands();
        }
        return commandInstance;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void resetCommand()
    {
        bGattCharacteristicList = BleScan.getInstance().gattCharacteristicList;
        Log.d(logTag, "List size :" +bGattCharacteristicList.size());
        for(BluetoothGattCharacteristic gc : bGattCharacteristicList)
        {
            if(gc.getUuid().toString().equals("c13c3555-2811-e1aa-7648-42b080d7ade7"))
            {
                writeCharUUId = gc.getUuid();
                bWriteChar = gc;

                Log.d(logTag, "Char uuid: " +gc.getUuid());
            }
            //Log.d(logTag, "Char uuid: " +gc.getUuid());
        }
        byte[] resetData = {0x00};
        bWriteChar.setValue(resetData);
        bGatt = BleScan.getInstance().bluetoothGatt;
        bGatt.setCharacteristicNotification(bWriteChar,true);
        bGatt.writeCharacteristic(bWriteChar);
    }

}
