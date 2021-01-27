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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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
    private BluetoothGattCharacteristic readChar;
    public  final String commandCharacteristic = "c13c3555-2811-e1aa-7648-42b080d7ade7";
    public  final String service = "6917f879-2dda-4d8e-9b46-cbe76616d50b";
    public static final String CONFIG_CHARACTERISTIC_ID = "C13C3555-2811-E1AA-7648-42B080D7ADE8";
    private static byte[] DECRYPTION_AES_Key = {'L','I','G','H','T','W','A','V','E','P','R','O','D','U','C','T'};
    private static byte[] ENCRYPTION_AES_Key =  {0, 0, 0 , 0, 0, 0, 0, 0, 0, 0,'l','w','k','i','b',0x00};
    private static final byte[] ivArray =  {0x20, (byte)0xc7, 0x04, 0x40, (byte)0xac, 0x40, 0x0d, (byte)0xba, (byte)0x84, 0x06, 0x57, 0x00, 0x74, (byte)0xf2, (byte)0xe2, 0x2a};
    private static byte[] randNumber = {(byte)0,(byte)0,(byte)0,(byte)0};
    private static final int CONFIG_CHARACTERISTIC_ID_LENGTH = 16;
    private static final int ENCRYPTION_RAND_BYTE = 0;

    public String bleDeviceName;
    public BluetoothDevice bleDevice;
    public BluetoothGatt bluetoothGatt;
    private Toast toast;
    public UUID serviceUUID = null;
    public UUID charUUID = null;
    public int bleDeviceRSSI = 0;


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

    //Creating an instance so that it can be accessed by other classes/activities w/o creating
    //additional objects
    private static BleScan ourInstance = new BleScan();

    public static ArrayList getBleList()
    {
        return mLeDevices;
    }

    //Get instance method
    static BleScan getInstance() {
        if(ourInstance == null)
        {
            ourInstance = new BleScan();
        }
        return ourInstance;
    }

    /**
     * This function will scan BLE device, it will stop scan after certain period.
     */
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
                    Log.d(logTag, "Device address: " +result.getDevice().getAddress() +" " +"Device name: " +result.getDevice().getName());
                    bleDeviceName = result.getDevice().getName();
                    if(bleDeviceName == null)
                    {
                        bleDeviceName = "NULL";
                    }
                    //Checking if BLE device is KIB, if yes, add it in the list and stop scanning
                    if(bleDeviceName.equals("LW  KIB"))
                    {
                        Log.d(logTag, "KIB detected!!");
                        adapterObj = new LeDeviceListAdapter();
                        bleDevice = result.getDevice();
                        mLeDevices.add(bleDevice);
                        bleDeviceRSSI = result.getRssi();
                        Log.d(logTag, "Device array: " +mLeDevices +" " +"Array count: " +mLeDevices.size());
                        bluetoothLeScanner.stopScan(leScanCallback);
                    }
                }
            };


    /**
     * This function will connect to BLE and call gattcallback.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void connectBleDevice()
    {
        bluetoothGatt = bleDevice.connectGatt(context,false, gattCallback);
    }

    /**
     * This function will connect to Bluetooth Gatt server, discover services and characteristics.
     */
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
            Log.d(logTag, "Total service size: " +gattServiceList.size());
            if(gattServiceList.size() > 0)
            {
                Log.d(logTag, "Gatt services identified: ");
                for(BluetoothGattService gattService : gattServiceList)
                {
                    serviceUUID = gattService.getUuid();
                    Log.d(logTag, "Service UUID: " +serviceUUID);
                    //Logic to check if it detects correct characteristic for writing and reading commands
                    if(serviceUUID.toString().equals(service))
                    {
                        Log.d(logTag, "Correct characteristics detected");
                        gattCharacteristicList = gattService.getCharacteristics();
                        for(BluetoothGattCharacteristic c : gattCharacteristicList)
                        {
                            Log.d(logTag, "Current characteristics: " + c.getUuid());
                            //Checking for a particular characteristics
                            if(c.getUuid().toString().equals("c13c3555-2811-e1aa-7648-42b080d7ade8"))
                            {
                                readChar = c;
                                Log.d(logTag, "readChar: " +readChar.getUuid());
                            }
                        }
                    }
                    Log.d(logTag, "Characteristics for discovered service: " +gattService.getCharacteristics());
                }
                //Listing out characteristics
                if(gattCharacteristicList.size() > 0)
                {
                    Log.d(logTag, "Gatt characteristics identified: ");
                    for(BluetoothGattCharacteristic gatChar : gattCharacteristicList)
                    {
                        charUUID = gatChar.getUuid();
                        Log.d(logTag, "Service Characteristics: " + charUUID);
                    }
                }
                boolean readStatus;
                readStatus = bluetoothGatt.readCharacteristic(readChar);
                Log.d(logTag, "read status: " +readStatus);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d(logTag, "Characteristic changed: " + Arrays.toString(characteristic.getValue()));
        }

        //Reading the data coming from KIB device and decrypting it
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            //Getting the values from characteristics in byte[] and converting to String
            String hexValue = bytesToHex(characteristic.getValue());
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i<hexValue.length()-1; i=i+2)
            {
                String temp = hexValue.substring(i,i+2);
                sb.append(temp);
                sb.append(" ");
            }
            Log.d(logTag, "Characteristic read hex: " + sb.toString());
            Log.d(logTag, "Characteristic read: " + Arrays.toString(characteristic.getValue()));
            //Validate if correct characteristic is there
            if(characteristic.getUuid().toString().toUpperCase().equals(CONFIG_CHARACTERISTIC_ID))
            {
                Log.d(logTag, "Characteristics matching");
                byte[] decryptedInfo = null;
                try {
                    //Check if decryption is needed
                    decryptedInfo = needToDecrypt(characteristic.getValue());
                    String decryptedDataHexString = bytesToHex(decryptedInfo);
                    Log.d(logTag, "Decrypted values original in HEX string: " +arrayToString(decryptedDataHexString));
                    Log.d(logTag, "Decrypted values formatted: " +arrayToString(decryptedDataHexString));
                    Log.d(logTag, "Length of the decoded data: " +decryptedInfo.length);
                    //If length of data is equal to config characteristic length, extract random key
                    if(decryptedInfo.length >= CONFIG_CHARACTERISTIC_ID_LENGTH)
                    {
                        //Function to call random key
                        decodeEncryptionRandom(decryptedInfo);
                    }

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                    e.printStackTrace();
                }

            }

        }
    };

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

    /**
     * This function checks if data which is read is eligible for decryption
     * @param infoToDecode
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] needToDecrypt(byte[] infoToDecode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] decryptedData = null;
        Log.d(logTag, "Size of data to be decoded: " +infoToDecode.length);
        if(infoToDecode.length < CONFIG_CHARACTERISTIC_ID_LENGTH)
        {
            Log.d(logTag, "Invalid information size!!");
        }
        else
        {
            Log.d(logTag, "Decryption needed!!");
            //Function to decrypt data
            decryptedData = decryptData(infoToDecode);
        }
        return decryptedData;
    }

    /**
     * This function will decrypt the data using Key, Data and IV, returns decrypted data in bytes[]
     * @param dataToDecrypt
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] decryptData(byte[] dataToDecrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] finalDecryptedData = null;
        Log.d(logTag, "Start decryption.....");
        String dataToDecryptHexString = bytesToHex(dataToDecrypt);
        String keyHexString = bytesToHex(DECRYPTION_AES_Key);
        String ivHexString = bytesToHex(ivArray);
        if(dataToDecrypt.length == 16)
        {
            IvParameterSpec iv = new IvParameterSpec(ivArray);
            SecretKeySpec secretKey = new SecretKeySpec(DECRYPTION_AES_Key,"AES");
            Log.d(logTag, "Decrypted Data: " +arrayToString(dataToDecryptHexString));
            Log.d(logTag, "Decrypted Key: " +arrayToString(keyHexString));
            Log.d(logTag, "Decrypted IV: " +arrayToString(ivHexString));
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE,secretKey,iv);
            finalDecryptedData =cipher.doFinal(dataToDecrypt);
        }
        return finalDecryptedData;
    }

    /**
     * This function will extract random key which will be used by KIB module to decrypt
     * @param data
     * @return
     */
    private byte[] decodeEncryptionRandom(byte[] data) {
        for(int cnt = 0; cnt < randNumber.length;cnt++ )
            randNumber[cnt] = data[ENCRYPTION_RAND_BYTE+cnt];
        Log.d(logTag, String.format("[decodeEncryptionRandom]: 0X%02X 0X%02X 0X%02X 0X%02X",
                randNumber[0], randNumber[1],randNumber[2],randNumber[3]));

        return randNumber;
    }

    /**
     * This function will format the data to be decrypted
     * @param dataToEncrypt
     * @return
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     */
    public byte[] dataToEncrypt(byte[] dataToEncrypt) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Log.d(logTag, "Size of data to encrypt: " +dataToEncrypt.length);
        byte[] formattedData = {(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
                (byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0};

        for(int i = 0; i<dataToEncrypt.length; i++)
        {
            formattedData[i] = dataToEncrypt[i];
        }
        Log.d(logTag, String.format("[decodeEncryptionRandom]: 0X%02X 0X%02X 0X%02X 0X%02X",
                randNumber[0], randNumber[1],randNumber[2],randNumber[3]));
        //Copy the random key which was extracted earlier to Encryption AES key
        System.arraycopy(randNumber,0,ENCRYPTION_AES_Key,0,randNumber.length);
        Log.d(logTag, "Encryption AES Key: " + arrayToString(bytesToHex(ENCRYPTION_AES_Key)));

        Log.d(logTag, "BLE device address: " +bleDevice.getAddress());
        String[] bleDeviceAddress = bleDevice.getAddress().split(":");
        Log.d(logTag, "BLE address in array form: " + Arrays.toString(bleDeviceAddress));

        //Add BLE device address to Encryption AES key
        for(int i = 0; i<bleDeviceAddress.length; i++)
        {
            ENCRYPTION_AES_Key[randNumber.length+i] =(byte)Integer.parseInt(bleDeviceAddress[i], 16);
        }
        Log.d(logTag, "Encryption AES Key after adding device address from random number length: "
                + arrayToString(bytesToHex(ENCRYPTION_AES_Key)));
        //Send the data to encrypt
        byte[] encryptedData = encryptData(formattedData);
        Log.d(logTag, "Encrypted Data final: " +arrayToString(bytesToHex(encryptedData)));
        return encryptedData;
    }

    /**
     * This function will encrypt the data using Key, IV.
     * @param dataToEncrypt
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] encryptData(byte[] dataToEncrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        IvParameterSpec iv = new IvParameterSpec(ivArray);
        Log.d(logTag, String.format("[encrypt] random: 0X%02X 0X%02X 0X%02X 0X%02X", randNumber[0], randNumber[1],randNumber[2],randNumber[3]));
        String encryptionHexString = bytesToHex(ENCRYPTION_AES_Key);
        String ivEncryptHexString = bytesToHex(ivArray);
        String dataBeforeEncryption = bytesToHex(dataToEncrypt);

        Log.d(logTag, "Encrypted Key: " +arrayToString(encryptionHexString));
        Log.d(logTag, "Encrypted IV: " +arrayToString(ivEncryptHexString));
        Log.d(logTag, "Data before encryption : " +arrayToString(dataBeforeEncryption));
        SecretKeySpec skeySpec = new SecretKeySpec(ENCRYPTION_AES_Key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        return cipher.doFinal(dataToEncrypt);
    }


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
