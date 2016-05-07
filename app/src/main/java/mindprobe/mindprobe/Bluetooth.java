package mindprobe.mindprobe;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.UUID;

import com.google.gson.JsonElement;
import org.json.JSONException;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Bluetooth{

    Context context;
    Activity act;
    BluetoothDevice bdDevice;
    static BluetoothAdapter bluetoothAdapter;
    ArrayList<BluetoothDevice> arrayListBluetoothDevices;
    public static BluetoothGatt gatt;
    public static BluetoothGattCharacteristic tx;
    public static BluetoothGattCharacteristic rx;
    public static UUID UART_UUID;
    public static UUID TX_UUID;
    public static UUID RX_UUID;
    public static UUID CLIENT_UUID; // UUID for the BTLE client characteristic which is necessary for notifications.
    private static long SCAN_PERIOD;
    public static boolean mScanning;
    private Handler mHandler;
    private Handler handlertest;
    public static SocketIO socket;
    public static String token;

    public Bluetooth(Context c, Activity a) {
        this.act = a;
        this.context=c;
        this.mHandler = new Handler();
        this.handlertest = new Handler();
        this.SCAN_PERIOD = 120000;

        this.UART_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
        this.TX_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
        this.RX_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
        this.CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

        bluetoothAdapter = MainActivity.bluetoothManager.getAdapter();
        arrayListBluetoothDevices = new ArrayList<BluetoothDevice>();
    }

    public final Runnable scanning = new Runnable() {
        public void run() {
            //writeLine("STOP Scanning");
            mScanning = false;
            bluetoothAdapter.stopLeScan(mLeScanCallback);
            MainActivity.start.setClickable(true);
        }
    };

    public void onBluetooth() {
        if(!bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.enable();
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(scanning , SCAN_PERIOD);

            mScanning = true;
            //writeLine("START Scanning");
            bluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            //writeLine("STOP Scanning");
            bluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(arrayListBluetoothDevices.size()<1) // this checks if the size of bluetooth device is 0,then add the
                            {                                           // device to the arraylist.
                                arrayListBluetoothDevices.add(device);

                                if (device.getAddress().equals("EF:17:4B:0F:93:29")){ //Francisco
                                    bdDevice = device;
                                    connect_to_device();
                                }
                            }
                            else
                            {
                                boolean flag = true;    // flag to indicate that particular device is already in the arlist or not
                                for(int i = 0; i<arrayListBluetoothDevices.size();i++)
                                {
                                    if(device.getAddress().equals(arrayListBluetoothDevices.get(i).getAddress()))
                                    {
                                        flag = false;
                                    }
                                }
                                if(flag)
                                {
                                    arrayListBluetoothDevices.add(device);
                                    if (device.getAddress().equals("EF:17:4B:0F:93:29")){ //Francisco
                                        bdDevice = device;
                                        connect_to_device();
                                    }
                                }
                            }
                        }
                    });
                }
            };

    public void connect_to_device() {

        writeLine("Connected to "+ bdDevice.getName());

        if (mScanning) {
            mHandler.removeCallbacks(scanning);
            scanLeDevice(false);
        }
        gatt = bdDevice.connectGatt(context, false, callback);

        if (gatt == null ) {
            writeLine("No gatt!");
        }
    }

    public void test(){
        handlertest.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.server_connection == true) {
                    String temp = String.valueOf(67).concat("a");
                    temp = temp.concat(String.valueOf(312));
                    temp = temp.concat("a");
                    temp = temp.concat(String.valueOf(50));
                    socket.emit("glove message", temp);
                }
            }
        }, 2000);
    }

    public void test2(){
        if (MainActivity.server_connection == false) {

            try {
                socket = new SocketIO(MainActivity.host);
            } catch (MalformedURLException e) {
                writeLine("Error creating socket!");
            }

            socket.connect(new IOCallback() {

                @Override
                public void onMessage(String data, IOAcknowledge ack) {
                    writeLine("Server said: " + data);
                    if (data == "connected") {
                        int id = 1;
                        socket.emit("glove connect event", String.valueOf(id));
                    } else if (data.startsWith("token")) {
                        token = data.substring(6);
                    }
                }

                @Override
                public void onMessage(JsonElement json, IOAcknowledge ack) {
                    writeLine("Server said:" + json.toString());
                }

                @Override
                public void on(String event, IOAcknowledge ack, JsonElement... args) {
                    writeLine("Server triggered event '" + event + "'");
                }

                @Override
                public void onError(SocketIOException socketIOException) {
                    writeLine("Server Offline");
                    socketIOException.printStackTrace();
                    socket = null;
                    MainActivity.server_connection = false;
                    MainActivity.start.setClickable(true);
                    test2();
                }

                @Override
                public void onDisconnect() {
                    writeLine("Connection terminated.");
                    socket = null;
                    MainActivity.server_connection = false;
                    MainActivity.start.setClickable(true);
                    test2();
                }

                @Override
                public void onConnect() {
                    writeLine("Connection established");
                    if (MainActivity.count_connections<1){
                        MainActivity.server_connection = true;
                    }

                    ++MainActivity.count_connections;
                    writeLine(String.valueOf(MainActivity.count_connections));
                }
            });
        }
        test();
    }

    public void writeLine(final CharSequence text) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private BluetoothGattCallback callback = new BluetoothGattCallback() {
        // Called whenever the device connection state changes, i.e. from disconnected to connected.
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                gatt.discoverServices();
                if (!gatt.discoverServices()) {
                    writeLine("Failed to start discovering services!");
                    if (gatt == null) {
                        return;
                    }
                    gatt .close();
                    gatt  = null;
                    //start_communication();
                }
            }
            else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                writeLine("Disconnected!");
                if (gatt == null) {
                    return;
                }
                gatt.close();
                gatt = null;
                tx = null;
                rx = null;
                MainActivity.start_acquisition = false;
                //start_communication();
            }
            else {
                writeLine("Connection state changed.  New state: " + newState);
            }
        }

        // Called when services have been discovered on the remote device.
        // It seems to be necessary to wait for this discovery to occur before
        // manipulating any services or characteristics.
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                MainActivity.start_acquisition = true;
                writeLine("Service discovery completed!");
            }
            else {
                writeLine("Service discovery failed with status: " + status);
            }
            // Save reference to each characteristic.
            tx = gatt.getService(UART_UUID).getCharacteristic(TX_UUID);
            rx = gatt.getService(UART_UUID).getCharacteristic(RX_UUID);

            // Setup notifications on RX characteristic changes (i.e. data received).
            // First call setCharacteristicNotification to enable notification.
            if (!gatt.setCharacteristicNotification(rx, true)) {
                writeLine("Couldn't set notifications for RX characteristic!");
            }
            // Next update the RX characteristic's client descriptor to enable notifications.
            if (rx.getDescriptor(CLIENT_UUID) != null) {
                BluetoothGattDescriptor desc = rx.getDescriptor(CLIENT_UUID);
                desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                if (!gatt.writeDescriptor(desc)) {
                    //writeLine("Couldn't write RX client descriptor value!");
                }
            }
            else {
                writeLine("Couldn't get RX client descriptor!");
            }
        }

        // Called when a remote characteristic changes (like the RX characteristic).
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] value_array = characteristic.getValue();

            MainActivity.yvalues_gsr_temp = (float) (value_array[1] & 0x3F) + ((value_array[2] & 0x0F)<<6);
            MainActivity.yvalues_gsr_temp = (MainActivity.yvalues_gsr_temp*3.9)/1023;
            MainActivity.yvalues_gsr_temp = (MainActivity.yvalues_gsr_temp-0.623)/100000;
            MainActivity.yvalues_gsr_temp = (MainActivity.yvalues_gsr_temp/0.623)*1000000000;

            MainActivity.yvalues_hr[MainActivity.pos] = (float) (value_array[0] & 0xFF);
            MainActivity.hr_value = value_array[0] & 0xFF;
            MainActivity.yvalues_gsr[MainActivity.pos] =  (float) MainActivity.yvalues_gsr_temp;
            MainActivity.gsr_value = (int) MainActivity.yvalues_gsr_temp;
            MainActivity.xvalues[MainActivity.pos] = (float) (((MainActivity.cycles*60) + (MainActivity.pos+1))*0.5);

            if (MainActivity.server_connection == true) {
                String temp = String.valueOf(MainActivity.hr_value).concat("a");
                temp = temp.concat(String.valueOf(MainActivity.gsr_value));
                temp = temp.concat("a");
                temp = temp.concat(String.valueOf(MainActivity.global_score));
                socket.emit("glove message",temp);
            }

            MainActivity.pos++;
            if(MainActivity.pos == 60){
                MainActivity.pos = 0;
                MainActivity.cycles++;
            }
        }
    };

}
