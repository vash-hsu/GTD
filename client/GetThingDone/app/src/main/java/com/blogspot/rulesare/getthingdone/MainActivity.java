package com.blogspot.rulesare.getthingdone;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private Spinner my_spinner;
    private Context my_context;
    ArrayAdapter<String> my_adapter;

    private Button button2put;
    private Button button2post;
    private Button button2delete;
    private Button button2reset;

    private EditText editRid;
    private EditText editText;
    private Switch switchOnOff;

    String[] arrayName;
    String[] arrayID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d("DM:", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayName = new String[1];
        arrayName[0] = String.valueOf("... new ...");
        initialize_gui();
    }

    private void initialize_gui()
    {
        Log.d("DM:", "initialize_gui");
        editRid = (EditText) findViewById(R.id.edittext_rid);
        editRid.setEnabled(false);
        editText = (EditText) findViewById(R.id.editText);
        switchOnOff = (Switch) findViewById(R.id.switch_onoff);
        switchOnOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { actWhileTurnOnOff(switchOnOff.isChecked());
             }} );
        button2post = (Button) findViewById(R.id.button2post);
        button2put = (Button) findViewById(R.id.button2put);
        button2delete = (Button) findViewById(R.id.button2delete);
        button2reset = (Button) findViewById(R.id.button2reset);
        button2post.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { actWhileClicking(button2post); } });
        button2put.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { actWhileClicking(button2put); } });
        button2delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { actWhileClicking(button2delete); } });
        button2reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { actWhileClicking(button2reset); } });

        _initialize_spinner();
        draw_gui();
    }

    private void clean_gui()
    {
        Log.d("DM:", "clean_gui");
        editRid.setText("");
        editText.setText("");
        my_spinner.setSelection(0);
        button2post.setVisibility(View.VISIBLE);
        button2put.setVisibility(View.INVISIBLE);
        button2delete.setVisibility(View.INVISIBLE);
        button2reset.setVisibility(View.VISIBLE);
        switchOnOff.setChecked(false);
    }

    private void _initialize_spinner()
    {
        Log.d("DM:", "_initialize_spinner");
        my_spinner = (Spinner) findViewById(R.id.spinner);
        my_context = this.getApplicationContext();
        my_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayName);
        my_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        my_spinner.setAdapter(my_adapter);
        my_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                flushSpinnerAtClick(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                doWhileSpinnerNotSelected();
            }
        });
    }

    private void draw_gui()
    {
        Log.d("DM:", "draw_gui");
        clean_gui();
        draw_spinner();
    }

    public void callback_draw_spinner(String[] keys, String[] names)
    {
        Log.d("callback_draw_spinner", "");
        if (keys == null) // no list
        {
            arrayID = new String[1];
            arrayID[0] = String.valueOf("... new ...");
            arrayName = arrayID;
            my_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayID);
            my_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            my_spinner.setAdapter(my_adapter);
        }
        else
        {
            arrayID = new String[names.length+1];
            arrayName = new String[keys.length+1];
            arrayID[0] = String.valueOf("... new ...");
            arrayName[0] = String.valueOf("... new ...");
            System.arraycopy(names, 0, arrayName, 1, names.length);
            System.arraycopy(keys, 0, arrayID, 1, keys.length);
            my_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayName);
            my_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            my_spinner.setAdapter(my_adapter);
        }
    }

    //  [networking] --> MyHttpHelper() --> callback_draw_spinner()
    private void draw_spinner() {
        Log.d("draw_spinner:", "..." + Debugger.getLocation());
        EditText fqdn = (EditText) findViewById(R.id.edittext_fqdn);
        String string_fqdn = fqdn.getText().toString();
        MyRestAPI my_api = new MyRestAPI(string_fqdn, my_context);
        if (my_api.isOnline()) {
            new MyHttpHelper().execute( my_api.getRestURL(), "get", "");
        } else {
            Toast.makeText(my_context,
                    "Houston, we have a networking problem",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void flushGuiAfter(String action) {
        Log.d("DM:", "flushGuiAfter " + action);
        switch (action) {
            case "get":
                button2post.setVisibility(View.INVISIBLE);
                button2put.setVisibility(View.VISIBLE);
                button2delete.setVisibility(View.VISIBLE);
                break;
            case "put":
            case "post":
            case "delete":
            case "reset":
                draw_gui();
                break;
            default:
                button2post.setVisibility(View.INVISIBLE);
                button2put.setVisibility(View.INVISIBLE);
                button2delete.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void actWhileClicking(Button button2click) {
        Log.d("actWhileClicking", button2click.getText().toString());
        String strText = editText.getText().toString();
        String strRid = editRid.getText().toString();
        boolean isComplete = switchOnOff.isChecked();
        String textMessage = "";
        int iID = button2click.getId();
        if (iID == R.id.button2delete){
            textMessage = admitRestByHttp(strRid, "", "delete");
            popToast(textMessage);
            flushGuiAfter("delete");
        } else if (iID == R.id.button2reset) {
            flushGuiAfter("reset");
        }
        else if (iID == R.id.button2put) {
            String format = "{'key':'%s', 'name':'%s', 'isComplete':%s}";
            String strJson = String.format(format, strRid, strText, isComplete?"true":"false");
            textMessage = admitRestByHttp(strRid, strJson, "put");
            popToast(textMessage);
            flushGuiAfter("put");
        }
        else if (iID == R.id.button2post) {
            String format = "{'name':'%s', 'isComplete':%s}";
            String strJson = String.format(format, strText, isComplete?"true":"false");
            textMessage = admitRestByHttp(strRid, strJson, "post");
            popToast(textMessage);
            flushGuiAfter("put");
        }
    }

    private void actWhileTurnOnOff(boolean onOff) {
        if (onOff){
            popToast("On");
        } else {
            popToast("Off");
        }
    }

    private void loadTodoList(String name, String key, boolean isComplete){
        Log.d("loadTodoList", name + " " + key + " " + String.valueOf(isComplete));
        editText.setText(name);
        editRid.setText(key);
        switchOnOff.setChecked(isComplete);
    }

    private void popToast(String message) {
        Toast.makeText(my_context, message, Toast.LENGTH_SHORT).show();
    }

    private void flushSpinnerAtClick(int position) {
        Log.d("flushSpinnerAtClick", "clicking line # " + position);
        if (position > 0) {
            String resource_id = arrayID[position];
            popToast("retrieving " + resource_id);
            editRid.setText(resource_id);
            EditText fqdn = (EditText) findViewById(R.id.edittext_fqdn);
            String string_fqdn = fqdn.getText().toString();
            MyRestAPI my_api = new MyRestAPI(string_fqdn, my_context);
            if (my_api.isOnline()) {
                try{
                    new MyHttpHelper().execute(my_api.getRestURL(resource_id), "get",
                            "", resource_id);
                } catch (Exception ex){
                    Log.w("flushSpinnerAtClick", "Exception " + ex.getMessage());
                }
            }
            flushGuiAfter("get");
        } else {
            clean_gui();
        }
    }

    // trigger async http request; return roger that immediately for user friendly
    private String admitRestByHttp(String resource_id, String input_json, String action) {
        Log.d("admitRestByHttp", action + " " + resource_id);
        String data4return = "";
        EditText fqdn = (EditText) findViewById(R.id.edittext_fqdn);
        String strFqdn = fqdn.getText().toString();
        MyRestAPI my_api = new MyRestAPI(strFqdn, my_context);
        if (my_api.isOnline()) {
            String format = "to %s %s";
            data4return = String.format(format, action, resource_id);
            switch (action) {
                case "delete":
                    new MyHttpHelper().execute(my_api.getRestURL(resource_id),
                            action, "", resource_id);
                    break;
                case "put":
                    new MyHttpHelper().execute(my_api.getRestURL(resource_id),
                            action, input_json, resource_id);
                    break;
                case "post":
                    new MyHttpHelper().execute(my_api.getRestURL(),
                            action, input_json, resource_id);
                    break;
                default:
                    Log.e("DM: wrapper_for_rest_", "fail to integrate " + action);
                    break;
            }
        }
        return data4return;
    }

    private void doWhileSpinnerNotSelected() {
        Log.d("SpinnerNotSelected", "doWhileSpinnerNotSelected .. " + Debugger.getLocation());
    }

    private class Message{
        private int http_status;
        private String raw_message;
        private Message(int status){
            http_status = status;
            raw_message = "";
        }
        private Message(int status, String buffer){
            http_status = status;
            raw_message = buffer;
        }
        private int get_status(){
            return http_status;
        }
        private String get_message(){
            return raw_message;
        }
    }

    // https://developer.android.com/training/basics/network-ops/connecting.html#connection
    class MyHttpHelper extends AsyncTask<String, Void, Message> {
        String string_url = "";
        String string_method = "";
        String string_json = "";
        String string_key = "";
        @Override
        protected Message doInBackground(String... urls)  // --> returning value was stored in result of onPostExecute
        {
            Message message;
            if (urls.length > 3){
                string_key = urls[3];
            }
            if (urls.length > 2){
                string_json = urls[2];
            }
            string_url = urls[0];
            string_method = urls[1];
            try {
                message = goHTTP(string_url, string_method, string_json);
            } catch (Exception e) {
                Log.w("doInBackground", e.getMessage());
                message = new Message(-1);
            }
            return message;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Message result) {
            Log.d("onPostExecute", String.valueOf(result.get_status()));
            Log.d("onPostExecute", result.get_message());
            while (true) {
                if (result.get_status() < 200 || result.get_status() >= 400) {
                    break;
                }
                switch (string_method.toLowerCase()) {
                    case "delete":
                    case "put":
                        if (result.get_status() == 204) {
                            popToast(string_method + " successfully");
                        } else {
                            popToast("Oops! " + string_method + " has "
                                    + String.valueOf(result.get_status()) + " back");
                        }
                        break;
                    case "post":
                        if (result.get_status() == 201) {
                            popToast(string_method + " successfully");
                        } else {
                            popToast("Oops! " + string_method + " has "
                                    + String.valueOf(result.get_status()) + " back");
                        }
                        break;
                    case "get":
                        if (result.get_status() == 200) {
                            popToast(string_method + " successfully");
                            cookJSON4Get(result.get_message(), string_key);
                        } else {
                            popToast("Oops! " + string_method + " has "
                                    + String.valueOf(result.get_status()) + " back");
                        }
                        break;
                    case "patch": // todo
                    default:
                        break;
                }
                break;
            }
        }

        // http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
        private String convertStreamToString(java.io.InputStream is) {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }

        private void cookJSON4Get(String strJSON, String strKey){
            try {
                Log.d("cookJSON4Get", "strJSON = " + strJSON);
                if (strKey!=null){
                    Log.d("cookJSON4Get", "strKey = " + strKey);
                } else {
                    Log.d("cookJSON4Get", "strKey = null");
                }

                if (strKey != null && strKey.length() > 0) { // get specific one
                    JSONObject json_root = new JSONObject(strJSON);
                    String name = json_root.getString("name");
                    String key = json_root.getString("key");
                    boolean isComplete = json_root.getBoolean("isComplete");
                    loadTodoList(name, key, isComplete);
                    //popToast("success to retrieve task: " + name);
                } else { // get all
                    JSONArray json_root = new JSONArray(strJSON);
                    String[] keys = new String[json_root.length()];
                    String[] names = new String[json_root.length()];
                    for (int i = 0; i < json_root.length(); i++) {
                        JSONObject child = json_root.getJSONObject(i);
                        String key = child.getString("key");
                        String name = child.getString("name");
                        keys[i] = key;
                        names[i] = name;
                    }
                    callback_draw_spinner(keys, names);
                }
            } catch (Exception err){
                Log.w("cookJSON4Get", err.getMessage());
                popToast("fail to retrieve task: " + strKey + "\n" + err.getMessage());
                callback_draw_spinner(null, null);
            }
        }

        private Message goHTTP(String strUrl, String strMethod, String str2Send) throws IOException
        {
            Log.d("goHTTP", strMethod + " " + strUrl + "\n" + str2Send + "..." + Debugger.getLocation());
            Message message = new Message(-1);
            InputStream is = null;
            try {
                URL url = new URL(strUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod(strMethod.toUpperCase());
                if (strMethod.equals("put") || strMethod.equals("post")) {
                    conn.setDoOutput(true);
                    conn.setRequestProperty("content-type", "application/json; charset=utf-8");
                    OutputStream os = conn.getOutputStream();
                    os.write(str2Send.getBytes());
                    os.flush();
                    os.close();
                } else { // get or delete
                    conn.setDoInput(true);
                }
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("goHTTP", strMethod + " " + strUrl + " HTTP code=[" + response + "]");
                if (response >= 400) {
                    Log.w("goHTTP", "stop processing http body because [" + response + "]");
                    message = new Message(response);
                } else { // response body
                    is = conn.getErrorStream();
                    if (is == null) {
                        is = conn.getInputStream();
                    }
                    String contentAsString = convertStreamToString(is);
                    Log.d("goHTTP", "HTTP body: " + contentAsString);
                    message = new Message(response, contentAsString);
                }
            } finally {
                if (is != null) {
                    try { is.close(); }
                    catch (IOException e) {
                        Log.w("goHTTP", "fail to close input stream, " + e.getMessage());
                    }
                }
            }
            return message;
        }
    }
}

class Debugger{

    public static String getLocation() {
        StackTraceElement current = Thread.currentThread().getStackTrace()[3];
        String strFilename = current.getFileName();
        int linenumber = current.getLineNumber();
        String format = "%s(%d)";
        return String.format(Locale.US, format, strFilename, linenumber);
    }
}


class MyRestAPI {
    private String strFQDN;
    private String strAPI = "/api/todo";
    private Context my_context;

    protected MyRestAPI(String fqdn, Context context) {
        strFQDN = fqdn;
        my_context = context;
    }

    protected MyRestAPI(String fqdn, Context context, String api) {
        strFQDN = fqdn;
        my_context = context;
        strAPI = api;
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                my_context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    protected String getRestURL() {
        return getRestURL("");
    }

    protected String getRestURL(String string_id) {
        String string_returned;
        String format;
        if (string_id.length() > 0) {
            format = "http://%s%s/%s";
            string_returned = String.format(format, strFQDN, strAPI, string_id);
        } else {
            format = "http://%s%s";
            string_returned = String.format(format, strFQDN, strAPI);
        }
        return string_returned;
    }
}