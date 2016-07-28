package com.example.rajeevnagarwal.project11;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DevPage extends AppCompatActivity {

    User user = null;
    int pos;
    GridView gridView;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_page);
        Intent intent = getIntent();
        user = (User) intent.getExtras().get("user");
        pos = intent.getExtras().getInt("position");
        final ArrayList<Devices> devlist = user.getRooms().get(pos).getDev();
        gridView = (GridView) findViewById(R.id.gridView2);
        gridView.setAdapter(new DevAdapter(this, devlist));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                System.out.println(position);
                if (position < devlist.size()) {
                    Intent intent = new Intent(getApplicationContext(), Stats.class);
                    intent.putExtra("devlist", devlist);
                    intent.putExtra("pos", position);

                    startActivity(intent);
                } else if (position == devlist.size()) {
                    LayoutInflater li = LayoutInflater.from(DevPage.this);
                    View promptview = li.inflate(R.layout.plusdev, null);
                    AlertDialog.Builder form = new AlertDialog.Builder(DevPage.this);
                    form.setTitle("Add device details");
                    form.setView(promptview);
                    final EditText txt = (EditText) promptview.findViewById(R.id.dname);
                    final EditText txt1 = (EditText) promptview.findViewById(R.id.ip);
                    final EditText txt2 = (EditText) promptview.findViewById(R.id.port);
                    form.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    System.out.println(txt.getText());

                                    Devices dev = new Devices(txt.getText().toString(), false, txt1.getText().toString(), txt2.getText().toString());
                                    user.getRooms().get(pos).AddDevice((dev));
                                    new AddDevices(getApplicationContext()).execute(user);


                                    Intent i = new Intent(getApplicationContext(), DevPage.class);
                                    i.putExtra("user", user);
                                    i.putExtra("position", pos);

                                    startActivity(i);
                                    finish();
                                }

                            }
                    );
                    form.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    finish();
                                }

                            }
                    );

                    AlertDialog dialog = form.create();
                    dialog.show();


                }


            }
        });


    }

    class AddDevices extends AsyncTask<User, String, String> {
        private Context context;

        public AddDevices(Context context) {
            this.context = context;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DevPage.this);
            pDialog.setMessage("Adding Devices");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(User... obj) {
            JSONObject json = new JSONObject();
            OutputStream os = null;
            HttpURLConnection conn = null;
            String res = null;
            try {
                json.put("room", obj[0].getRooms().get(pos).getName());
                json.put("name", obj[0].getRooms().get(pos).getDev().get(obj[0].getRooms().get(pos).getDev().size()-1).getName());
                json.put("status", obj[0].getRooms().get(pos).getDev().get(obj[0].getRooms().get(pos).getDev().size()-1).getStatus());
                json.put("IP_Addr", obj[0].getRooms().get(pos).getDev().get(obj[0].getRooms().get(pos).getDev().size()-1).getIP_Addr());
                json.put("Port", obj[0].getRooms().get(pos).getDev().get(obj[0].getRooms().get(pos).getDev().size() - 1).getPort());
                res = null;


                try {
                    URL url = new URL(Link.link+"/project11/" + obj[0].getUsername() + "/" + "AddDevices.php");
                    String data = json.toString();
                    System.out.println(data);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setFixedLengthStreamingMode(data.getBytes().length);
                    conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                    conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                    conn.connect();
                    os = new BufferedOutputStream(conn.getOutputStream());
                    os.write(data.getBytes());
                    os.flush();
                    BufferedReader bufferedReader;
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    res = bufferedReader.readLine();
                    System.out.println(res);
                    return res;

                } catch (Exception e) {

                    e.printStackTrace();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                try {
                    os.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                conn.disconnect();

            }
            return res;


        }

        protected void onPostExecute(String result) {
            System.out.println(result);
           // pDialog.dismiss();
           /* if(result.equals("Done"))
                Toast.makeText(context,"You are our registered user now",Toast.LENGTH_SHORT).show();
            else if(result.equals("Already exists"))
            {
                Toast.makeText(context,"Sorry username already exists. Please try again",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(context, "Error in registration", Toast.LENGTH_LONG).show();
            }*/
        }

    }
    public void onBackPressed() {
        Intent intent = new Intent(this,Homepage.class);
        intent.putExtra("user",user);
        startActivity(intent);
        // do nothing.
    }
}
