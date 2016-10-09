package com.blogspot.rulesare.getthingdone_mydb;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner my_spinner;
    private Context my_context;
    ArrayAdapter<String> my_adapter;

    private Button button2update;
    private Button button2create;
    private Button button2delete;
    private Button button2reset;

    private EditText editRid;
    private EditText editText;
    private Switch switchOnOff;
    private ToDoItemDAO itemDAO;

    String[] arrayName;
    String[] arrayID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d("DM:", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        my_context = this.getApplicationContext();
        itemDAO = new ToDoItemDAO(getApplicationContext());
        arrayName = new String[1];
        arrayName[0] = String.valueOf("... new ...");
        setupGUI();
    }

    private void setupGUI()
    {
        Log.d("DM:", "setupGUI");
        editRid = (EditText) findViewById(R.id.edittext_rid);
        editRid.setEnabled(false);
        editText = (EditText) findViewById(R.id.editText);
        switchOnOff = (Switch) findViewById(R.id.switch_onoff);
        switchOnOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { actWhileTurnOnOff(switchOnOff.isChecked());
            }} );
        button2create = (Button) findViewById(R.id.button2create);
        button2update = (Button) findViewById(R.id.button2update);
        button2delete = (Button) findViewById(R.id.button2delete);
        button2reset = (Button) findViewById(R.id.button2reset);
        button2create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { actWhileClicking(button2create); } });
        button2update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { actWhileClicking(button2update); } });
        button2delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { actWhileClicking(button2delete); } });
        button2reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { actWhileClicking(button2reset); } });

        setupSpinner();
        drawGUIduetoSpinner();
    }

    private void resetGUI()
    {
        Log.d("DM:", "resetGUI");
        editRid.setText("");
        editText.setText("");
        my_spinner.setSelection(0);
        button2create.setVisibility(View.VISIBLE);
        button2update.setVisibility(View.INVISIBLE);
        button2delete.setVisibility(View.INVISIBLE);
        button2reset.setVisibility(View.VISIBLE);
        switchOnOff.setChecked(false);
    }

    private void setupSpinner()
    {
        Log.d("DM:", "setupSpinner");
        my_spinner = (Spinner) findViewById(R.id.spinner);
        my_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayName);
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

    private void drawGUIduetoSpinner()
    {
        Log.d("DM:", "drawGUIduetoSpinner");
        resetGUI();
        regSpinDrawInBg();
    }

    public void actOnSpinner(String[] keys, String[] names)
    {
        if (keys.length == 0) // no list
        {
            arrayID = new String[1];
            arrayID[0] = String.valueOf("... new ...");
            arrayName = arrayID;
            my_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayID);
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
            my_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayName);
            //my_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayID);
            my_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            my_spinner.setAdapter(my_adapter);
        }
    }

    private void regSpinDrawInBg() {
        Log.d("regSpinDrawInBg:", "..." + Debugger.getLocation());
        ToDoItem item = new ToDoItem();
        item.setAction("getall");
        new DBMinBackground().execute(item);
    }

    private void flushGuiAfter(String action) {
        Log.d("flushGuiAfter", action);
        switch (action) {
            case "get":
                button2create.setVisibility(View.INVISIBLE);
                button2update.setVisibility(View.VISIBLE);
                button2delete.setVisibility(View.VISIBLE);
                break;
            case "update":
            case "create":
            case "delete":
            case "reset":
                drawGUIduetoSpinner();
                break;
            default:
                button2create.setVisibility(View.INVISIBLE);
                button2update.setVisibility(View.INVISIBLE);
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
        if (iID == R.id.button2delete) {
            ToDoItem item = new ToDoItem(Long.parseLong(strRid));
            item.setAction("delete");
            textMessage = admitDBonAction(item);
            popToast(textMessage);
            flushGuiAfter("delete");
        } else if (iID == R.id.button2reset) {
            flushGuiAfter("reset");
        }
        else if (iID == R.id.button2update) {
            ToDoItem item = new ToDoItem(Long.parseLong(strRid), strText, isComplete);
            item.setAction("update");
            textMessage = admitDBonAction(item);
            popToast(textMessage);
            flushGuiAfter("update");
        }
        else if (iID == R.id.button2create) {
            ToDoItem item = new ToDoItem(0, strText, isComplete);
            item.setAction("create");
            textMessage = admitDBonAction(item);
            popToast(textMessage);
            flushGuiAfter("create");
        }
    }

    private void actWhileTurnOnOff(boolean onOff) {
        if (onOff){
            popToast("Congratulation! Let's call it a day.");
        } else {
            popToast("The journey towards the end is getting tougher!");
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
            ToDoItem target = new ToDoItem();
            target.setId(Long.parseLong(resource_id));
            target.setAction("get");
            new DBMinBackground().execute(target);
            flushGuiAfter("get");
        } else {
            resetGUI();
        }
    }

    // trigger async DB operation
    private String admitDBonAction(ToDoItem item) {
        Log.d("admitDBonAction", item.getAction() + " " + item.getId());
        String data4return;
        String format = "to %s %s";
        data4return = String.format(format, item.getAction(), item.getId());
        switch (item.getAction()) {
            case "delete":
            case "update":
            case "create":
            case "get":
            case "getall":
                new DBMinBackground().execute(item);
                data4return = "processing " + item.getAction();
                break;
            default:
                Log.e("admitDBonAction", "fail to integrate " + item.getAction() +
                        "..." + Debugger.getLocation());
                break;
        }
        return data4return;
    }

    private void doWhileSpinnerNotSelected() {
        Log.d("SpinnerNotSelected", "doWhileSpinnerNotSelected .. " + Debugger.getLocation());
    }

    class DBMinBackground extends AsyncTask<ToDoItem, Void, ToDoItem[]> {
        ToDoItem item;
        ToDoItem[] items2return;
        @Override
        protected ToDoItem[] doInBackground(ToDoItem... items)
        {
            Log.d("doInBackground", Debugger.getLocation());
            if (items.length == 1) {
                item = items[0];
            } else {
                item = new ToDoItem();
            }

            if (item.getAction().equals("getall")) {
                List<ToDoItem> result = itemDAO.getAll();
                Log.d("doInBackground", "result.size() = " + result.size());
                items2return = new ToDoItem[result.size()];
                for (int i=0; i<result.size(); i++) {
                    items2return[i] = new ToDoItem(
                            result.get(i).getId(), result.get(i).getName(), false);
                    items2return[i].setAction("getall");
                }
                return items2return;
            } else {
                switch(item.getAction()){
                    case "get":
                        item = itemDAO.get(item.getId());
                        item.setAction("gotten");
                        break;
                    case "update":
                        if (itemDAO.update(item)) {
                            item.setAction("updated");
                        }
                        break;
                    case "create":
                        item = itemDAO.insert(item);
                        item.setAction("created");
                        break;
                    case "delete":
                        if (itemDAO.delete(item.getId())) {
                            item.setAction("deleted");
                        }
                        break;
                    default:
                        break;
                }
            }
            items2return = new ToDoItem[1];
            items2return[0] = item;
            return items2return;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ToDoItem[] items) {
            if (items.length == 1 && ! items[0].getAction().equals("getall")) {
                switch (items[0].getAction()) {
                    case "updated":
                    case "deleted":
                    case "created":
                        popToast(items[0].getAction() + " successfully");
                        break;
                    case "update":
                    case "delete":
                        popToast("failed to " + items[0].getAction());
                        break;
                    case "gotten":
                        actWhileItemGot(items[0]);
                        break;
                    default:
                        popToast("undefined " + items[0].getAction() + " "
                                + String.valueOf(items[0].getId()));
                        break;
                }
            } else { // getall
                List<ToDoItem> list = new ArrayList<>();
                for (ToDoItem i:items) {
                    if (item.getAction().equals("getall")) {
                        list.add(i);
                    }
                }
                actWhileItemListGot(list);
            }
        }

        private void actWhileItemListGot(List<ToDoItem> list) {
            Integer length = list.size();
            Log.d("actWhileItemListGot", String.valueOf(length));
            String[] keys = new String[length];
            String[] names = new String[length];
            for (int i=0; i<length; i++) {
                ToDoItem target = list.get(i);
                keys[i] = String.valueOf(target.getId());
                Log.d("actWhileItemListGot", "key[" + i + "] = "+ keys[i]);
                String candidate = target.getName();
                if (candidate.length() > 20) {
                    candidate = candidate.substring(0, 17) + "...";
                }
                names[i] =  String.format(Locale.US, "[%d] %s", target.getId(), candidate);
                Log.d("actWhileItemListGot", "names[" + i + "] = "+ names[i]);
            }
            actOnSpinner(keys, names);
        }

        private void actWhileItemGot(ToDoItem item){
            Log.d("actWhileItemGot", item.getId() + " " +item.getName());
            loadTodoList(item.getName(), String.valueOf(item.getId()), item.getIsComplete());
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
