package com.example.rajeevnagarwal.project11;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

public class home extends AppCompatActivity {


    Button btn1;
    Button btn2;
    EditText txt1,txt2;
    ProgressDialog pDialog;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

       pref = getApplicationContext().getSharedPreferences("Mypref",0);

        if(pref.getString("username","").length()>0)
        {

            System.out.println("hello");
            new Sign(getApplicationContext()).execute(pref.getString("username",""),pref.getString("password",""));
        }
       else {
            setContentView(R.layout.activity_home);
            btn1 = (Button) findViewById(R.id.btn1);
            btn2 = (Button) findViewById(R.id.btn2);
            txt1 = (EditText)findViewById(R.id.txt1);
            txt2 = (EditText)findViewById(R.id.txt2);
        }

    }

    public void signin(View v)
    {
        String username = txt1.getText().toString();
        String password = txt2.getText().toString();
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username",username);
        editor.putString("password",password);
        editor.commit();
        new Sign(getApplicationContext()).execute(username, password);

    }
    public void register(View v)
    {

        Intent i = new Intent(this,Register.class);
        startActivity(i);
    }

    class Sign extends AsyncTask<String,String,String>
    {
        private Context context;
        public Sign(Context context)
        {
            this.context = context;
        }
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(home.this);
            pDialog.setMessage("Signing you in...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }
        protected String doInBackground(String... args)
        {
            String username = args[0];
            String password = args[1];
            String result="";
            String link = Link.link+"/project11/"+username+"/config.json";
            //link = link+"?username="+username+"&password="+password;
            System.out.println(link);
            try
            {
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                BufferedReader bufferedReader;
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                result = bufferedReader.readLine();
                System.out.println(result);

            }
            catch(ConnectException e)
            {
                result = "Connection Error";
            }
            catch(FileNotFoundException e)
            {
                result = "Wrong Username or Password";
            }
            catch(UnknownHostException e)
            {
                result = "Connection Problem";
            }
            catch(Exception e)
            {
                result = "Error Occurred";
                e.printStackTrace();
            }
            return result;
        }
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            if(result!=null)
            {
                if(!result.contains("{"))
                {
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

                }
                else {
                    System.out.println(result);
                    try {
                        JSONObject json = new JSONObject(result);
                        JSONArray array = json.getJSONArray("rooms");
                        User user = new User(json.getString("name"), json.getString("username"), json.getString("password"));
                        for (int i = 0; i < array.length(); i++) {
                            System.out.println(array.getJSONObject(i).getString("name"));
                            user.addRooms(new Rooms(array.getJSONObject(i).getString("name")));
                            try {
                                for (int j = 0; j < array.getJSONObject(i).getJSONArray("devices").length(); j++) {
                                    user.getRooms().get(i).AddDevice(new Devices(array.getJSONObject(i).getJSONArray("devices").getJSONObject(j).getString("name"), array.getJSONObject(i).getJSONArray("devices").getJSONObject(j).getBoolean("status"), array.getJSONObject(i).getJSONArray("devices").getJSONObject(j).getString("IP_Addr"), array.getJSONObject(i).getJSONArray("devices").getJSONObject(j).getString("Port")));

                                }
                            }catch(JSONException e)
                            {
                                System.out.println(array.getJSONObject(i).get("devices"));
                            }
                        }
                        Intent i = new Intent(context, Homepage.class);
                        i.putExtra("user", user);
                        startActivity(i);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {

                    }
                }

            }
            else
            {

            }
        }




    }


}
