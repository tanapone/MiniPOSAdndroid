package minipos.miniproject.com.miniposadndroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;

import minipos.miniproject.com.miniposadndroid.R;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import minipos.miniproject.com.miniposadndroid.Models.MessageModel;
import minipos.miniproject.com.miniposadndroid.Models.UserModel;
import minipos.miniproject.com.miniposadndroid.StorageHelper.StorageManager;
import minipos.miniproject.com.miniposadndroid.WSTask.WSTask;
import okhttp3.MediaType;

import static xdroid.toaster.Toaster.toast;

public class LoginActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private StorageManager sm = new StorageManager(LoginActivity.this);
    private EditText usernameEditText;
    private EditText passwordEditText;
    private ImageButton WSConfigImageBtn;
    private Button loginBtn;
    private UserModel user = new UserModel();
    private MessageModel message = new MessageModel();
    private Pattern pattern;
    private Matcher matcher;
    private String Port_PATTERN = "^[1-9]{1}[0-9]{3}$";
    private long backPressedTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("MiniPOS");
        setContentView(R.layout.activity_login);
        pattern = Pattern.compile(Port_PATTERN);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        WSConfigImageBtn = findViewById(R.id.WSConfigImageBtn);
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameEditText.getText().toString().trim().equalsIgnoreCase("") || passwordEditText.getText().toString().trim().equalsIgnoreCase("")) {
                    usernameEditText.setError("กรุณากรอกชื่อบัญชี");
                    passwordEditText.setError("กรุณากรอกรหัสผ่าน");
                } else {
                    user.getUser().setUsername(usernameEditText.getText().toString());
                    user.getUser().setPassword(passwordEditText.getText().toString());
                    try{
                        final LoginController loginController = new LoginController(getApplicationContext());
                        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setMessage("กำลังโหลด...");
                        progressDialog.show();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                String responseData = null;
                                try {
                                    responseData = loginController.verifyLogin(new Gson().toJson(user.getUser()));
                                    MessageModel messageModel = new MessageModel(responseData);
                                    if(responseData!=null){
                                        if(messageModel.getMessage().getMessageText()!=null){
                                            if(messageModel.getMessage().getMessageText().equalsIgnoreCase("Wrong username or password.")){
                                                toast("ชื่อผู้ใช้หรือรหัสผ่านผิด");
                                            }
                                        }else{
                                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                            intent.putExtra("userJsonObject",responseData);
                                            startActivity(intent);
                                        }
                                    }else{
                                        Toast.makeText(LoginActivity.this,"ไม่สามารถเชื่อมต่อระบบได้",Toast.LENGTH_SHORT).show();
                                    }
                                    progressDialog.dismiss();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        },1000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }

        });

        WSConfigImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WSConnectorController().configWS();
            }
        });

    }

    public boolean validatePort(final String serverPort){
        matcher = pattern.matcher(serverPort);
        return matcher.matches();
    }

   public class WSConnectorController{
       public void configWS(){
           AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
           View mView = getLayoutInflater().inflate(R.layout.wsconfig_dialog, null);
           final EditText serverIP_EditText = (EditText) mView.findViewById(R.id.serverIP_EditText);
           final EditText serverPort_EditText = (EditText) mView.findViewById(R.id.serverPort_EditText);
           final Button setupBtn = (Button) mView.findViewById(R.id.setupBtn);
           String[] serverPath = {};
           System.out.println("check file : "+sm.checkFileExits());
           if(sm.readServerConfig().length>0) {
              serverPath = sm.readServerConfig();
           }
           System.out.println(serverPath.length);
           if(serverPath.length>0) {
               serverIP_EditText.setText(serverPath[0].toString());
               serverPort_EditText.setText(serverPath[1].toString());
           }
           mBuilder.setView(mView);
           final AlertDialog dialog = mBuilder.create();
           dialog.setContentView(R.layout.wsconfig_dialog);
           dialog.setTitle("ตั้งค่าการเชื่อมต่อระบบ");

           setupBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   final String serverIP = serverIP_EditText.getText().toString().trim();
                   final String serverPort = serverPort_EditText.getText().toString().trim();
                   if(serverIP.trim().equalsIgnoreCase("")||serverPort.trim().equalsIgnoreCase("")){
                       serverIP_EditText.setError("กรุณากรอก Server IP");
                       serverPort_EditText.setError("กรุณากรอก Server Port");
                   }else if(validatePort(serverPort)==false){
                       serverPort_EditText.setError("รูปแบบ Port ผิด");
                   }else{
                       final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                       progressDialog.setMessage("กำลังโหลด...");
                       progressDialog.setCanceledOnTouchOutside(false);
                       progressDialog.show();
                       final Handler handler = new Handler();
                       handler.postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               try {
                                   String serverPath = serverIP+":"+serverPort;
                                   String responseData = new WSTask(getApplicationContext()).execute(serverPath,"TEST").get();
                                   MessageModel messageModel = new MessageModel(responseData);
                                   System.out.println("responseData : "+responseData);
                                   if(responseData!=null) {
                                       if (messageModel.getMessage().getMessageText().equalsIgnoreCase("Connected")) {
                                           sm.writeServerConfig(serverIP, serverPort);
                                           toast("ตั้งค่าสำเร็จ");
                                           progressDialog.dismiss();
                                           dialog.dismiss();
                                       } else {
                                           toast("ไม่สามารถเชื่อมต่อระบบได้");
                                           progressDialog.dismiss();
                                       }
                                   }else{
                                       toast("ไม่สามารถติดต่อกับเซิฟเวอร์ดังกล่าวได้");
                                       progressDialog.dismiss();
                                   }

                               } catch (InterruptedException e) {
                                   e.printStackTrace();
                               } catch (ExecutionException e) {
                                   e.printStackTrace();
                               }

                           }
                       },1000);
                   }
               }
           });
           dialog.show();
       }
    }

    public class LoginController{
        private Context context;

        public LoginController(Context context){
            this.context = context;
        }

        public String verifyLogin(String jsonUserObject) throws ExecutionException, InterruptedException {
            String responseData = null;
            responseData = new WSTask(context).execute("/login/mobile","POST",jsonUserObject).get();
            return responseData;
        }
    }

    @Override
    public void onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()){
            finishAffinity();
            return;
        }else{
            Toast.makeText(LoginActivity.this,"กดปุ่มกลับ 2 ครั้งเพื่อ ออกจากแอพ",Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
    static boolean IsNullOrEmpty(String[] myStringArray)
    {
        return myStringArray == null || myStringArray.length < 1;
    }
}

