package com.example.rajeevnagarwal.project11;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class Register extends AppCompatActivity {

    ProgressDialog pDialog;
    Rooms rm1 = null,rm2 = null;
    Button submit,cancel;
    EditText name,username,password,cnpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        submit = (Button)findViewById(R.id.submit);
        cancel = (Button)findViewById(R.id.cancel);
        name = (EditText)findViewById(R.id.name);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        cnpassword = (EditText)findViewById(R.id.confirmpassword);

        User obj = new User("Rajeev","rajeev14084","sdgs");
        Rooms rm1 = new Rooms("Dining Room");
        Rooms rm2 = new Rooms("Bed Room");
        Rooms rm3 = new Rooms("Hall");
        Devices dev1 = new Devices("fan",false,"192.168.0.1","80");
        Devices dev2 = new Devices("bulb",true,"192.168.0.1","80");
        Devices dev3 = new Devices("Camera",false,"192.168.0.1","80");
        Devices dev4 = new Devices("TV",true,"192.168.0.1","80");
        rm1.AddDevice(dev1);
        rm2.AddDevice(dev2);
        rm3.AddDevice(dev3);
        rm3.AddDevice(dev4);
        obj.addRooms(rm1);
        obj.addRooms(rm2);
        obj.addRooms(rm3);
        new RegisterUser(getApplicationContext()).execute(obj);
        System.out.println("Ok bye");


    }
    public void cancel(View v)
    {
        System.out.println("hello");
    }
    public void submit(View v)
    {
        System.out.println("hello");
    }


    class RegisterUser extends AsyncTask<User,User,String>
    {
        private Context context;
        public RegisterUser(Context context)
        {
            this.context=context;
        }
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage("Registering");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        protected String doInBackground(User... obj)
        {
            JSONObject json = new JSONObject();
            JSONArray array = new JSONArray();
            OutputStream os = null;
            HttpURLConnection conn = null;
            String res=null;
            try {
                json.put("name", obj[0].getName());
                json.put("username",obj[0].getUsername());
                json.put("password",obj[0].getPassword());

                for(int i=0;i<obj[0].getRooms().size();i++)
                {
                    JSONObject jroom = new JSONObject();
                    jroom.put("name",obj[0].getRooms().get(i).getName());
                    JSONArray jdev = new JSONArray();
                    for(int j=0;j<obj[0].getRooms().get(i).getDev().size();j++)
                    {
                        JSONObject x = new JSONObject();
                        x.put("name",obj[0].getRooms().get(i).getDev().get(j).getName());
                        x.put("status",obj[0].getRooms().get(i).getDev().get(j).getStatus());
                        x.put("IP_Addr",obj[0].getRooms().get(i).getDev().get(j).getIP_Addr());
                        x.put("Port",obj[0].getRooms().get(i).getDev().get(j).getPort());
                        jdev.put(x);

                    }
                    jroom.put("devices",jdev);
                    array.put(jroom);
                }
                json.put("rooms",array);
                res=null;


                try {
                    URL url = new URL(Link.link+"/project11/RegisterUser.php");
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
                catch(UnknownHostException e)
                {
                    res = "Internal Server Problem";
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
            pDialog.dismiss();
            if(result.equals("Done"))
                Toast.makeText(context,"You are our registered user now",Toast.LENGTH_SHORT).show();
            else if(result.equals("Already exists"))
            {
                Toast.makeText(context,"Sorry username already exists. Please try again",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            }
        }




    }

}

