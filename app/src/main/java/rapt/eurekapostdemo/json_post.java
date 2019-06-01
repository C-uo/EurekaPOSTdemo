package rapt.eurekapostdemo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class json_post extends AppCompatActivity {

    TextView checkNetwork;
    EditText aircraftName;
    TextView separateThread;
    String ChinaURL = "http://:8000/api/v1/aircraft/"; // 大学以外のネット環境用
    String OsakaURL = "";
    String hoge = "";       // 立命館のネット環境で接続用の一時的なIPアドレス
    String tmpURL = "http://" + hoge + ":8000/api/v1/aircraft/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_post);
        checkNetwork = (TextView) findViewById(R.id.checkNetwork);
        aircraftName = findViewById(R.id.aircraftName);
        separateThread = (TextView) findViewById(R.id.separateThread);
        checkNetworkConnection();
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            // show "Connected" & type of network "WIFI or MOBILE"
            checkNetwork.setText(networkInfo.getTypeName() + "に接続しました");
            // change background color to green
            checkNetwork.setBackgroundColor(0xFFFF0000);
        } else {
            // show "Not Connected"
            checkNetwork.setText("接続できません");
            // change background color to red
            checkNetwork.setBackgroundColor(0xFF7CCC26);
        }

        return isConnected;
    }

    private String httpPost(String myUrl) throws IOException, JSONException{
        String result = "";

        URL url = new URL(myUrl);
        // HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");     // UTF-8が大事！！！

        // JSON
        JSONObject jsonObject = buidJsonObject();
        setPostRequestContent(conn, jsonObject);

        conn.connect();
        return conn.getResponseMessage()+"";
    }

    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                try {
                    return httpPost(urls[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "Error!";
                }
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            separateThread.setText(result);
        }
    }

    public void send(View view){
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        if(checkNetworkConnection())
            new HTTPAsyncTask().execute(ChinaURL);
        else
            Toast.makeText(this, "接続していない", Toast.LENGTH_SHORT).show();
    }

    public JSONObject buidJsonObject() throws JSONException{

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("name", aircraftName.getText().toString());
        return jsonObject;
    }

    private void setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException{
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(json_post.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }

}

