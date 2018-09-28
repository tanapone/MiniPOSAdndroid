package minipos.miniproject.com.miniposadndroid.StorageHelper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class StorageManager {
    Context context;
    private String FILENAME = "server-config.txt";
    public StorageManager(Context context){
        this.context = context;
    }

    public boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(
                Environment.getExternalStorageState());
    }

    public boolean isExternalStorageReadable() {
        return  isExternalStorageWritable() ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(
                        Environment.getExternalStorageState());
    }

    public void writeServerConfig(String serverIP,String serverPort){
        if(isExternalStorageWritable() && checkPermisson(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            try{
                File textFile = new File(Environment.getExternalStorageDirectory(),FILENAME);
                FileOutputStream fos = new FileOutputStream(textFile);
                String serverConfig = serverIP+","+serverPort;
                fos.write(serverConfig.getBytes());
                fos.close();
                System.out.println("------------------------------------");
                System.out.println("Write Server Config Success");
                System.out.println("------------------------------------");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkPermisson(String permission){
        int check = ContextCompat.checkSelfPermission(context,permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    public boolean checkFileExits(){
        File file = new File(FILENAME);
        return file.exists();
    }

    public String[] readServerConfig(){
        String[] serverPath = {};
        try{
            if(isExternalStorageReadable()){
                File textFile = new File(Environment.getExternalStorageDirectory(),FILENAME);
                FileInputStream fis = new FileInputStream(textFile);
                if(fis!=null){
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader buff = new BufferedReader(isr);

                    String line = null;
                    while ((line = buff.readLine()) != null){
                        serverPath = line.split(",");
                    }
                    fis.close();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverPath;
    }


}
