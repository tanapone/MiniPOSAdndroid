package minipos.miniproject.com.miniposadndroid.Printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import minipos.miniproject.com.miniposadndroid.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

/**
 * Created by NongToey Laptop on 2/22/2018.
 */

public class PrinterHelper extends AppCompatActivity {
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<String> deviceList_bonded=new ArrayList<String>();//bonded list
    ListView bluetoothListView;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private OutputStream outputStream;
    private InputStream inputStream;
    private byte[] readBuffer;
    private int readBufferPosition;
    private volatile boolean stopWorker;
    private Thread workerThread;
    private View dialogView;
    private LayoutInflater inflater;
    public PrinterHelper(Context context){
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public boolean connectedBluetooth(){
        boolean result = false;
        if(bluetoothDevice != null){
            result =true;
        }
        return result;
    }

    public void findBuletooth(){
    try{
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            Toast.makeText(context,"ไม่พบอุปกรณ์ บลูธูทใดๆ", Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled()){
            Toast.makeText(context,"กรุณาเปิดบลูธูท", Toast.LENGTH_SHORT).show();
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBluetooth);
        }

        final Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if(pairedDevices.size()>0){
            deviceList_bonded.clear();
            for(BluetoothDevice device : pairedDevices){
                deviceList_bonded.add(device.getName()+'\n'+device.getAddress());
            }

            inflater= LayoutInflater.from(context);
            dialogView =inflater.inflate(R.layout.bluetooth_select_dialog, null);
            bluetoothListView = (ListView) dialogView.findViewById(R.id.bluetoothListView);
            ArrayAdapter<String> bluetoothListViewAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_expandable_list_item_1,deviceList_bonded);
            bluetoothListView.setAdapter(bluetoothListViewAdapter);
            final AlertDialog dialog = new AlertDialog.Builder(context).setTitle("กรุณาเลือกอุปกรณ์ บลูธูท").setView(dialogView).create();
            dialog.show();
            bluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(bluetoothAdapter!=null&&bluetoothAdapter.isDiscovering()){
                        bluetoothAdapter.cancelDiscovery();
                    }
                    String deviceName = deviceList_bonded.get(i).substring(0,deviceList_bonded.get(i).length()-18);
                    String deviceAddress = deviceList_bonded.get(i).substring(deviceList_bonded.get(i).length()-18,deviceList_bonded.get(i).length());
                    Toast.makeText(context,"You selected : "+deviceName, Toast.LENGTH_SHORT).show();
                    // Toast.makeText(getApplicationContext(),"You selected : "+deviceAddress,Toast.LENGTH_SHORT).show();
                    for(BluetoothDevice device : pairedDevices){
                        if(device.getName().equals(deviceName)){
                            bluetoothDevice = device;
                            Log.e("bluetooth","connected");
                            try {
                                openBT();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                    dialog.cancel();
                }
            });

        }else{
            Toast.makeText(context,"ไม่พบอุปกรณ์ บลูธูทใดๆ", Toast.LENGTH_SHORT).show();
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            beginListenForData();
            Toast.makeText(context,"เชื่อมต่อ เครื่องปริ้นแล้ว", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
* after opening a connection to bluetooth printer device,
* we have to listen and check if a data were sent to be printed.
*/
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = inputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                inputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Toast.makeText(context,data, Toast.LENGTH_SHORT).show();
                                                // myLabel.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] getDateTime() {
        final Calendar c = Calendar.getInstance();
        String dateTime [] = new String[2];
        dateTime[0] = c.get(Calendar.DAY_OF_MONTH) +"/"+ c.get(Calendar.MONTH) +"/"+ c.get(Calendar.YEAR);
        dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE)+"."+ c.get(Calendar.SECOND);
        return dateTime;
    }

    public String getDate(){
            String time = getDateTime()[0]+" "+getDateTime()[1];
            return time;
    }


    public Bitmap drawText(String text, int textWidth, int color, int size, String position) {


        // Get text dimensions
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.parseColor("#000000"));
        textPaint.setTextSize(size);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Kanit-SemiBold.ttf");
        textPaint.setTypeface(face);
        StaticLayout mTextLayout = null;
        if(position.equals("left")) {
            mTextLayout = new StaticLayout(text, textPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }else if(position.equals("center")){
            mTextLayout = new StaticLayout(text, textPaint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        }else{
            mTextLayout = new StaticLayout(text, textPaint, textWidth, Layout.Alignment.ALIGN_OPPOSITE, 1.0f, 0.0f, false);
        }
        // Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap(textWidth, mTextLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        // Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        c.drawPaint(paint);

        // Draw text
        c.save();
        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }


    public void printText(String text, int size, String position) {
        try {
            int width=385;
            int colorWhite = Color.WHITE;
            Bitmap bmp = drawText(text,width,colorWhite,size,position);
            byte[] command = Utils.decodeBitmap(bmp);
            printText(command);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    void printText(byte[] msg) {
        try {
            // Print normal text
            outputStream.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCenter(){
        try{
            outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void resetPrint() {
        try{
            outputStream.write(PrinterCommands.ESC_FONT_COLOR_DEFAULT);
            outputStream.write(PrinterCommands.FS_FONT_ALIGN);
            outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
            outputStream.write(PrinterCommands.ESC_CANCEL_BOLD);
            outputStream.write(PrinterCommands.LF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printUnicode(){
        try {
            outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
            printText(Utils.UNICODE_TEXT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //print new line
    public void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
