package minipos.miniproject.com.miniposadndroid.WSTask;

import android.content.Context;
import android.os.AsyncTask;

import minipos.miniproject.com.miniposadndroid.StorageHelper.StorageManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WSTask extends AsyncTask<String,Void,String> {
    private StorageManager sm;
    private Context context;
//    ProgressDialog progressDialog;
    public WSTask(Context context){
        this.context = context;
        this.sm = new StorageManager(this.context);
    }

//    @Override
//    protected void onPreExecute() {
//        progressDialog = new ProgressDialog(this.context);
//        progressDialog.setTitle("สถานะ");
//        progressDialog.setMessage("กำลังโหลด...");
//        progressDialog.show();
//    }


    @Override
    protected String doInBackground(String... params) {
        OkHttpClient client = new OkHttpClient();
        if(params[1].equalsIgnoreCase("GET")){
            String[] serverConfig = sm.readServerConfig();
            String serverPath = "http://"+serverConfig[0]+":"+serverConfig[1]+"/services";
            String newUrl = serverPath.concat(params[0]);

            Request.Builder builder = new Request.Builder();
            builder.url(newUrl);
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(params[1].equalsIgnoreCase("POST")){
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            String[] serverConfig = sm.readServerConfig();
            String serverPath = "http://"+serverConfig[0]+":"+serverConfig[1]+"/services";
            String newUrl = serverPath.concat(params[0]);
            RequestBody body = RequestBody.create(JSON, params[2]);
            Request.Builder builder = new Request.Builder();
            builder.post(body);
            builder.url(newUrl);
            Request request = builder.build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(params[1].equalsIgnoreCase("TEST")){
            String serverPath = params[0].concat("/services/testConnector");
            String newUrl = "http://".concat(serverPath);
            Request.Builder builder = new Request.Builder();
            builder.url(newUrl);
            Request request = builder.build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

//    protected void onPostExecute(String result)  {
//        progressDialog.dismiss();
//    }
}
