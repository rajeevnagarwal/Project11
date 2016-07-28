package com.example.rajeevnagarwal.project11;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.rajeevnagarwal.project11.Devices;
import com.example.rajeevnagarwal.project11.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rajeev Nagarwal on 7/18/2016.
 */
public class DevAdapter extends BaseAdapter {
    String IP,Port,Param;
    private Context context;
    int pos;
    private int selectedYear=0,selectedMonth=0,selectedDayOfMonth=0;
    private ArrayList<Devices> devlist;
    public DevAdapter(Context context,ArrayList<Devices> devlist)
    {
        this.context = context;
        this.devlist = devlist;
    }
    @Override
    public int getCount() {
        return devlist.size()+1;
    }
    public View getView(final int position,View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView = null;
        TextView textView=null;
        TextView txt=null;
        if(convertView == null) {

            gridView = inflater.inflate(R.layout.devitem, null);


        }

            else
        {
            gridView = convertView;
        }
        txt = (TextView)gridView.findViewById(R.id.txtreminder);
        textView = (TextView) gridView.findViewById(R.id.devname);
        final Switch sw = (Switch) gridView.findViewById(R.id.switch1);
        if (position < devlist.size()) {
            textView.setText(devlist.get(position).getName());
            if (devlist.get(position).getStatus() == false) {
                sw.setChecked(false);
                sw.setText("OFF");

            } else {
                sw.setChecked(true);
                sw.setText("ON");
            }
            pos = position;
            txt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptview = li.inflate(R.layout.datatime, null);
                    AlertDialog.Builder form = new AlertDialog.Builder(context);
                    form.setTitle("Choose Date and Time");
                    form.setView(promptview);
                    final DatePicker dt = (DatePicker) promptview.findViewById(R.id.dp_datepicker);
                    final TimePicker tm = (TimePicker)promptview.findViewById(R.id.tp_timepicker);

                    dt.init(
                            dt.getYear(), dt.getMonth(), dt.getDayOfMonth(),
                            new DatePicker.OnDateChangedListener() {
                                @Override
                                public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                    selectedYear = year;
                                    selectedMonth = monthOfYear;
                                    selectedDayOfMonth = dayOfMonth;
                                }//end onDateChangedListener

                            });

                    form.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Intent intent = new Intent(context, AlarmReciever.class);
                                    System.out.println("Plz" + devlist.get(position).getName());
                                    System.out.println(selectedDayOfMonth + ":" + selectedMonth + ":" + selectedYear);
                                    System.out.println(tm.getCurrentHour() + ":" + tm.getCurrentMinute());
                                    intent.putExtra("devlist",devlist);
                                    intent.putExtra("pos",position);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                            context,0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

                                    Calendar cal = Calendar.getInstance();
                                    cal.set(Calendar.YEAR,selectedYear);
                                    cal.set(Calendar.MONTH, selectedMonth);
                                    cal.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth);
                                    cal.set(Calendar.HOUR_OF_DAY,tm.getCurrentHour());
                                    cal.set(Calendar.MINUTE, tm.getCurrentMinute());
                                    cal.set(Calendar.SECOND, 0);
                                    long millis = cal.getTimeInMillis();
                                    System.out.println(millis);
                                    System.out.println(""+cal.getTime().getDate()+cal.getTime().getMonth()+cal.getTime().getYear()+cal.getTime().getHours()+cal.getTime().getMinutes()+cal.getTime().getSeconds());
                                    if(cal.compareTo(Calendar.getInstance())<=0)
                                    {
                                        Toast.makeText(context,
                                                "Invalid Date/Time",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                        alarmManager.set(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
                                        Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT);
                                    }




                                }

                            }
                    );
                    form.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {

                                }

                            }
                    );

                    AlertDialog dialog = form.create();
                    dialog.show();


                }
            });
            sw.getThumbDrawable().setColorFilter(sw.isChecked() ? Color.GREEN : Color.RED, PorterDuff.Mode.MULTIPLY);
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b == true) {

                        devlist.get(position).getLogin().add(new Date());
                        sw.setText("ON");
                        IP = devlist.get(position).getIP_Addr();
                        Port = devlist.get(position).getPort();
                        Param = "1";
                        System.out.println(IP+Port+Param);
                        new HttpRequestAsyncTask(context, Param, IP, Port, "pin"
                        ).execute();


                    } else {

                        devlist.get(position).getLogout().add(new Date());
                        sw.setText("OFF");
                        IP = devlist.get(position).getIP_Addr();
                        Port = devlist.get(position).getPort();
                        Param = "0";
                        System.out.println(IP + Port + Param);
                        new HttpRequestAsyncTask(context, Param, IP, Port, "pin"
                        ).execute();
                    }
                    sw.getThumbDrawable().setColorFilter(b ? Color.GREEN : Color.RED, PorterDuff.Mode.MULTIPLY);


                }
            });
        }
        else if(position==devlist.size())
        {
            textView.setText("Add more devices");
            textView.setTextColor(Color.BLUE);
            txt.setVisibility(View.INVISIBLE);


            sw.setVisibility(View.INVISIBLE);

        }
        gridView.setBackground(context.getDrawable(R.drawable.devback));

        return gridView;
    }


    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    public String sendRequest(String parameterValue, String ipAddress, String portNumber, String parameterName) {
        String serverResponse = "ERROR";

        try {

            HttpClient httpclient = new DefaultHttpClient(); // create an HTTP client
            // define the URL e.g. http://myIpaddress:myport/?pin=13 (to toggle pin 13 for example)
            //URI website = new URI("http://"+ipAddress+":"+portNumber+"/?"+parameterName+"="+parameterValue);
            System.out.println("hack"+ipAddress);
            System.out.println(portNumber);
            System.out.println(parameterValue);

            URI website = new URI("http://"+ipAddress+"/PIN_"+parameterValue);
            //URI website = new URI("http://"+ipAddress+"/PIN_08_1"); //?"+parameterName+"="+parameterValue);

            HttpGet getRequest = new HttpGet(); // create an HTTP GET object
            getRequest.setURI(website); // set the URL of the GET request
            HttpResponse response = httpclient.execute(getRequest); // execute the request
            // get the ip address server's reply
            InputStream content = null;
            content = response.getEntity().getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    content
            ));
            serverResponse = in.readLine();
            // Close the connection
            content.close();
        } catch (ClientProtocolException e) {
            // HTTP error
            serverResponse = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            // IO error
            serverResponse = e.getMessage();
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // URL syntax error
            serverResponse = e.getMessage();
            e.printStackTrace();
        }
        // return the server's reply/response text
        return serverResponse;
    }
    private class HttpRequestAsyncTask extends AsyncTask<Void, Void, Void> {

        // declare variables needed
        private String requestReply,ipAddress, portNumber;
        private Context context;
        private AlertDialog alertDialog;
        private String parameter;
        private String parameterValue;

        /**
         * Description: The asyncTask class constructor. Assigns the values used in its other methods.
         * @param context the application context, needed to create the dialog
         * @param parameterValue the pin number to toggle
         * @param ipAddress the ip address to send the request to
         * @param portNumber the port number of the ip address
         */
        public HttpRequestAsyncTask(Context context, String parameterValue, String ipAddress, String portNumber, String parameter)
        {
            this.context = context;

            alertDialog = new AlertDialog.Builder(this.context)
                    .setTitle("HTTP Response From IP Address:")
                    .setCancelable(true)
                    .create();


            this.ipAddress = ipAddress;
            this.parameterValue = parameterValue;
            this.portNumber = portNumber;
            this.parameter = parameter;
        }

        /**
         * Name: doInBackground
         * Description: Sends the request to the ip address
         * @param voids
         * @return
         */
        @Override
        protected Void doInBackground(Void... voids) {
            alertDialog.setMessage("Data sent, waiting for reply from server...");
            if(!alertDialog.isShowing())
            {
                //alertDialog.show();
            }
            requestReply = sendRequest(parameterValue,ipAddress,portNumber, parameter);
            return null;
        }

        /**
         * Name: onPostExecute
         * Description: This function is executed after the HTTP request returns from the ip address.
         * The function sets the dialog's message with the reply text from the server and display the dialog
         * if it's not displayed already (in case it was closed by accident);
         * @param aVoid void parameter
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            alertDialog.setMessage(requestReply);
            if(!alertDialog.isShowing())
            {
               // alertDialog.show(); // show dialog
            }
        }

        /**
         * Name: onPreExecute
         * Description: This function is executed before the HTTP request is sent to ip address.
         * The function will set the dialog's message and display the dialog.
         */
        @Override
        protected void onPreExecute() {
            alertDialog.setMessage("Sending data to server, please wait...");
            if(!alertDialog.isShowing())
            {
                 //alertDialog.show();
            }
        }

    }




}
