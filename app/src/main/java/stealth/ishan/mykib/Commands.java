package stealth.ishan.mykib;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Commands {

    private final static String logTag = Commands.class.getSimpleName();
    List<BluetoothGattCharacteristic> bGattCharacteristicList;
    private BluetoothGatt bGatt;
    private BluetoothGattCharacteristic bWriteChar;
    private BluetoothGattCharacteristic bReadChar;
    private BluetoothGattDescriptor bGattDescriptor;
    private UUID descriptorUUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private final static byte[] LOCK_COMMAND = {(byte)0x0c, 0x00, 0x00, 0x0, 0x02, 0x05, 0x30, 0x00, 0x00};
    private final static byte[] UNLOCK_COMMAND = {(byte)0x0c, 0x00, 0x00, 0x0, 0x02, 0x05, 0x31, 0x00, 0x00};
    private UUID writeCharUUId;
    private UUID readCharUUId;
    private Handler mHandler;
    private BluetoothDevice bleDevice = BleScan.getInstance().bleDevice;

    private Commands()
    {

    }
    //Creating the instance
    private static Commands commandInstance = new Commands();

    //Get instance method
    static Commands getInstance() {
        if(commandInstance == null)
        {
            commandInstance = new Commands();
        }
        return commandInstance;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void listCharacteristics()
    {
        bGattCharacteristicList = BleScan.getInstance().gattCharacteristicList;
        Log.d(logTag, "List size :" +bGattCharacteristicList.size());
        //Listing out characteristics
        for(BluetoothGattCharacteristic gc : bGattCharacteristicList)
        {
            if(gc.getUuid().toString().equals("c13c3555-2811-e1aa-7648-42b080d7ade7"))
            {
                writeCharUUId = gc.getUuid();
                bWriteChar = gc;
                Log.d(logTag, "Char uuid: " +gc.getUuid());
            }
            else
            {
                bReadChar = gc;
                readCharUUId = gc.getUuid();
                Log.d(logTag, "Read Char: " +readCharUUId);
            }
        }
    }


    /**
     * This function will list out the write/read characteristics, send the data to encrypt and write data to characteristics
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void lockCommand() throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        listCharacteristics();
        //Assigning lock command bytes to byte[]
        byte[] commandDataFrame = LOCK_COMMAND;
        int dataFrameLength = commandDataFrame.length;
        Log.d(logTag, "Command data frame length:  " +dataFrameLength);

        int dataSentSize =0;
        int currentDataSize = 0;
        while(dataSentSize < dataFrameLength)
        {
            Log.d(logTag, "Data sent size: " +dataSentSize);
            if(dataFrameLength - dataSentSize > 16)
            {
                currentDataSize = 16;
            }
            else
            {
                currentDataSize = dataFrameLength - dataSentSize;
            }
            Log.d(logTag, "Current Data size: " +currentDataSize);
            byte[] dataChunkToSend = new byte[currentDataSize];
            System.arraycopy(commandDataFrame,0,dataChunkToSend,0,dataFrameLength);
            Log.d(logTag, "Data chunk to be sent before encryption: " +arrayToString(bytesToHex(dataChunkToSend)));
            byte[] encryptedDataSent = BleScan.getInstance().dataToEncrypt(dataChunkToSend);
            Log.d(logTag, "Encrypted data: " +arrayToString(bytesToHex(encryptedDataSent)));
            bWriteChar.setValue(encryptedDataSent);
            boolean dataSentStatus;
            bGatt = BleScan.getInstance().bluetoothGatt;
            if (bGatt != null)
            {
                dataSentStatus = bGatt.writeCharacteristic(bWriteChar);

                if(dataSentStatus == false)
                {
                    Log.d(logTag, "Data not sent!!");
                }
                else
                {
                    Log.d(logTag, "Data sent!!");
                    Log.d(logTag, "Command data sent: " + Arrays.toString(LOCK_COMMAND));
                }
            }
            else
            {
                Log.d(logTag, "bGatt is null");
            }
            dataSentSize = dataSentSize + currentDataSize;
        }


        /**bWriteChar.setValue(resetData);
        bGatt = BleScan.getInstance().bluetoothGatt;
        bGatt.setCharacteristicNotification(bWriteChar,true);
        bGattDescriptor = bWriteChar.getDescriptor(descriptorUUID);

        Log.d(logTag, "Descriptor: " +bGattDescriptor.getUuid());
        bGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bGatt.writeDescriptor(bGattDescriptor);**/
        //bGatt.writeCharacteristic(bWriteChar);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void unlockCommand() throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        listCharacteristics();
        //Assigning lock command bytes to byte[]
        byte[] commandDataFrame = UNLOCK_COMMAND;
        int dataFrameLength = commandDataFrame.length;
        Log.d(logTag, "Command data frame length:  " +dataFrameLength);

        int dataSentSize =0;
        int currentDataSize = 0;
        while(dataSentSize < dataFrameLength)
        {
            Log.d(logTag, "Data sent size: " +dataSentSize);
            if(dataFrameLength - dataSentSize > 16)
            {
                currentDataSize = 16;
            }
            else
            {
                currentDataSize = dataFrameLength - dataSentSize;
            }
            Log.d(logTag, "Current Data size: " +currentDataSize);
            byte[] dataChunkToSend = new byte[currentDataSize];
            System.arraycopy(commandDataFrame,0,dataChunkToSend,0,dataFrameLength);
            Log.d(logTag, "Data chunk to be sent before encryption: " +arrayToString(bytesToHex(dataChunkToSend)));
            byte[] encryptedDataSent = BleScan.getInstance().dataToEncrypt(dataChunkToSend);
            Log.d(logTag, "Encrypted data: " +arrayToString(bytesToHex(encryptedDataSent)));
            bWriteChar.setValue(encryptedDataSent);
            boolean dataSentStatus;
            bGatt = BleScan.getInstance().bluetoothGatt;
            if (bGatt != null)
            {
                dataSentStatus = bGatt.writeCharacteristic(bWriteChar);

                if(dataSentStatus == false)
                {
                    Log.d(logTag, "Data not sent!!");
                }
                else
                {
                    Log.d(logTag, "Data sent!!");
                    Log.d(logTag, "Command data sent: " + Arrays.toString(UNLOCK_COMMAND));
                }
            }
            else
            {
                Log.d(logTag, "bGatt is null");
            }
            dataSentSize = dataSentSize + currentDataSize;
        }


        /**bWriteChar.setValue(resetData);
         bGatt = BleScan.getInstance().bluetoothGatt;
         bGatt.setCharacteristicNotification(bWriteChar,true);
         bGattDescriptor = bWriteChar.getDescriptor(descriptorUUID);

         Log.d(logTag, "Descriptor: " +bGattDescriptor.getUuid());
         bGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
         bGatt.writeDescriptor(bGattDescriptor);**/
        //bGatt.writeCharacteristic(bWriteChar);
    }

    public void insideCalibration()
    {
        Log.d(logTag, "Device name :" +bleDevice.getName() +" " +"Device address: " +bleDevice.getAddress());
        Log.d(logTag, "Current RSSI: " +BleScan.getInstance().bleDeviceRSSI);

    }


    /**
     * This function converts hex string to normal string with " " spaces
     * @param hexString
     * @return
     */
    public String arrayToString(String hexString)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<hexString.length()-1; i=i+2)
        {
            String temp = hexString.substring(i,i+2);
            sb.append(temp);
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * This function will take byte array and convert it to HEX String
     */
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
