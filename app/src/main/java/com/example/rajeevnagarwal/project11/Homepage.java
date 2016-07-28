package com.example.rajeevnagarwal.project11;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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

public class Homepage extends AppCompatActivity {

    GridView gridView;
    User user=null;

    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Intent intent = getIntent();
        user = (User)intent.getExtras().get("user");
        final ArrayList<Rooms> rm = user.getRooms();
        gridView = (GridView)findViewById(R.id.gridView1);
        gridView.setAdapter(new CustomAdapter(this, rm));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        ((TextView) v.findViewById(R.id.roomname))
                                .getText(), Toast.LENGTH_SHORT).show();
                if (position < rm.size()) {
                    Intent i = new Intent(getApplicationContext(), DevPage.class);
                    i.putExtra("user", user);
                    i.putExtra("position", position);
                    startActivity(i);
                } else if (position == rm.size()) {
                    LayoutInflater li = LayoutInflater.from(Homepage.this);
                    View promptview = li.inflate(R.layout.plusroom, null);
                    AlertDialog.Builder form = new AlertDialog.Builder(Homepage.this);
                    form.setTitle("Add room details");
                    form.setView(promptview);

                    final EditText txt = (EditText) promptview.findViewById(R.id.name);
                    form.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    System.out.println(txt.getText());
                                    Rooms rm = new Rooms(txt.getText().toString());
                                    user.addRooms(rm);
                                    new AddRooms(getApplicationContext()).execute(user);


                                    Intent i = new Intent(getApplicationContext(), Homepage.class);
                                    i.putExtra("user", user);

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
    class AddRooms extends AsyncTask<User,String,String>
    {
        private Context context;
        public AddRooms(Context context)
        {
            this.context=context;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Homepage.this);
            pDialog.setMessage("Adding Room");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(User... obj)
        {
            JSONObject json = new JSONObject();
            OutputStream os = null;
            HttpURLConnection conn = null;
            String res=null;
            try {
                json.put("name", obj[0].getRooms().get(obj[0].getRooms().size()-1).getName());
                JSONArray array = new JSONArray();
                json.put("devices",array);
                res=null;


                try {
                    URL url = new URL(Link.link+"/project11/"+obj[0].getUsername()+"/"+"AddRooms.php");
                    String data = json.toString();
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

                }
                catch(Exception e)
                {

                    e.printStackTrace();
                }


            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try {
                    os.close();

                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                conn.disconnect();

            }
            return res;


        }
        protected void onPostExecute(String result)
        {
            System.out.println(result);
            //pDialog.dismiss();
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
    @Override
    public void onBackPressed() {
        // do nothing.
    }
}
