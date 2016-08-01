package com.example.rajeevnagarwal.project11;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Stats extends AppCompatActivity {

    Spinner lst;
    FrameLayout frame = null;
    ArrayList<Devices> devlist;
    User user;
    int pos;
    int posroom;
    ArrayList<String> list = new ArrayList<String>(); // for storing daily timestamps
    ArrayList<String> listm = new ArrayList<String>(); //for storing monthly timestamps
    ArrayList<String> listw = new ArrayList<String>(); // for storing weekly timestamps

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        user = (User) intent.getExtras().get("user");
        posroom = intent.getExtras().getInt("posroom");
        devlist = (ArrayList<Devices>) intent.getExtras().get("devlist");
        setContentView(R.layout.activity_stats);
        lst = (Spinner) findViewById(R.id.listView);
        pos = intent.getExtras().getInt("pos");
        String[] values = {"Weekly Usage", "Monthly Usage", "Daily Usage"};
        frame = (FrameLayout) findViewById(R.id.frame);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
        lst.setAdapter(adapter);
        lst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(lst.getSelectedItem());
                if (lst.getSelectedItem().equals("Weekly Usage")) {
                    Date dt = new Date();
                    new getWeeklyTimeStamp(getApplicationContext()).execute(dt);

                } else if (lst.getSelectedItem().equals("Monthly Usage")) {
                    Date dt = new Date();
                    new getMonthlyTimeStamp(getApplicationContext()).execute(dt);


                } else if (lst.getSelectedItem().equals("Daily Usage")) {
                    Date dt = new Date();
                    new getDailyStamp(getApplicationContext()).execute(dt);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void creategraph(Context context, String param) {

        if (param.equals("Monthly Usage")) {
            frame.removeAllViews();
            BarChart chart = new BarChart(getApplicationContext());
            ArrayList<String> labels = new ArrayList<String>();
            chart.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
            ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
            HashMap<String,Float> login = new HashMap<String,Float>();
            int position = 0;
            Float diff = new Float(0);
            for(int i=0;i<listw.size();i++)
                System.out.println(listw.get(i));
            for(int i=0;i<listw.size();i++)
            {
                if(listw.get(i).contains("For"))
                {
                    String sen[] = listw.get(i).split(" ");
                    position = Integer.parseInt(sen[1]);
                    diff = 0f;
                }
                else if(!(listw.get(i).contains("For")))
                {

                    String[] sen = listw.get(i).split(" ");
                    if(sen[1].equals("0"))
                    {
                        if(!(listw.get(i-1).contains("For")))
                        {
                            if(listw.get(i-1).split(" ")[1].equals("1"))
                            {
                                diff = (Float.parseFloat(listw.get(i).split(" ")[0])-Float.parseFloat(listw.get(i-1).split(" ")[0]));
                                System.out.println(diff);
                                diff = (diff/3600);
                                System.out.println(diff);
                                if(!login.containsKey(""+position))
                                {

                                    login.put(""+position,diff);
                                }
                                else
                                {
                                    System.out.println(position+" "+login.get(""+position));
                                    login.put(""+position,login.get("" + position)+diff);
                                }
                            }
                        }
                    }
                    else if(sen[1].equals("1"))
                    {

                    }
                }
            }
            int i=0;
            for (Map.Entry<String, Float> entry : login.entrySet()) {
                if (login.containsKey(entry.getKey())) {

                        Date dt = new Date(Long.parseLong(entry.getKey()) * 1000);
                        labels.add(""+dt.getDate());
                        entries.add(new BarEntry(entry.getValue(), i));
                        i++;

                }
            }
            BarDataSet dataset = new BarDataSet(entries, "# of Hours");
            BarData data = new BarData(labels, dataset);
            chart.setData(data);
            chart.setDescription("Your weekly usage of this device");
            chart.animateXY(2000, 2000);
            dataset.setColors(ColorTemplate.COLORFUL_COLORS);
            frame.addView(chart);


        } else if (param.equals("Weekly Usage")) {
            frame.removeAllViews();
            ArrayList<Entry> entries = new ArrayList<Entry>();
            PieChart p = new PieChart(context);
            PieDataSet dataset = new PieDataSet(entries, "No. of hours");
            dataset.setColors(ColorTemplate.COLORFUL_COLORS);
            ArrayList<String> labels = new ArrayList<String>();
            HashMap<String,Float> login = new HashMap<String,Float>();
            int position = 0;
            Float diff = new Float(0);
            for(int i=0;i<listm.size();i++)
                System.out.println(listm.get(i));
            for(int i=0;i<listm.size();i++)
            {
                if(listm.get(i).contains("For"))
                {
                    String sen[] = listm.get(i).split(" ");
                    position = Integer.parseInt(sen[1]);
                    diff = 0f;
                }
                else if(!(listm.get(i).contains("For")))
                {

                    String[] sen = listm.get(i).split(" ");
                    if(sen[1].equals("0"))
                    {
                        if(!(listm.get(i-1).contains("For")))
                        {
                            if(listm.get(i-1).split(" ")[1].equals("1"))
                            {
                                diff = (Float.parseFloat(listm.get(i).split(" ")[0])-Float.parseFloat(listm.get(i-1).split(" ")[0]));
                                System.out.println(diff);
                                diff = (diff/3600);
                                System.out.println(diff);
                                if(!login.containsKey(""+position))
                                {

                                    login.put(""+position,diff);
                                }
                                else
                                {
                                    System.out.println(position+" "+login.get(""+position));
                                    login.put(""+position,login.get("" + position)+diff);
                                }
                            }
                        }
                    }
                    else if(sen[1].equals("1"))
                    {

                    }
                }
            }
            int i=0;
            for (Map.Entry<String, Float> entry : login.entrySet()) {
                if (login.containsKey(entry.getKey())) {
                    if(entry.getValue()!=0.0) {
                        Date dt = new Date(Long.parseLong(entry.getKey()) * 1000);
                        labels.add(dtod(dt.getDay()));
                        entries.add(new Entry(entry.getValue(), i));
                        i++;
                    }
                }

            }
            PieData data = new PieData(labels, dataset);
            p.setData(data);
            p.getLegend().setPosition(Legend.LegendPosition.LEFT_OF_CHART);
            p.animateXY(2000, 2000);

            frame.addView(p);

        } else if (param.equals("Daily Usage")) {
            frame.removeAllViews();

            ArrayList<Entry> entries = new ArrayList<Entry>();
            ArrayList<String> lables = new ArrayList<String>();
            LineChart lineChart = new LineChart(context);
            for(int i=0;i<list.size();i++)
                System.out.println(list.get(i));
            for(int i=0;i<list.size();i++)
            {

                String[] sen = list.get(i).split(" ");
                if(sen[1].equals("1"))
                {
                    lables.add(sen[0]);
                    entries.add(new Entry(5.0f, i));
                }
                else if(sen[1].equals("0"))
                {
                    lables.add(sen[0]);
                    entries.add(new Entry(0.0f, i));
                }
            }
            LineDataSet dataset = new LineDataSet(entries, "Daily Usage");
            LineData data = new LineData(lables, dataset);
            dataset.setColors(ColorTemplate.COLORFUL_COLORS);
            dataset.setDrawFilled(true);
            lineChart.setData(data);
            lineChart.animateXY(2000,2000);
            frame.addView(lineChart);
        }
    }
    public String dtod(int d) {
        switch (d) {
            case 1:
                return "Mon";
            case 2:
                return "Tue";
            case 3:
                return "Wed";
            case 4:
                return "Thurs";
            case 5:
                return "Fri";
            case 6:
                return "Sat";
            case 7:
                return "Sun";
            default:
                return "noday";

        }
    }

    public String dtom(int d) {
        switch (d + 1) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return "nomonth";
        }
    }
    class getWeeklyTimeStamp extends AsyncTask<Date,String,String>
    {
        private Context context;

        public getWeeklyTimeStamp(Context context) {
            this.context = context;
        }
        protected void onPreExecute() {
            super.onPreExecute();


        }
        protected String doInBackground(Date... args) {
            listm.clear();
            String username = user.getUsername();
            String room = user.getRooms().get(posroom).getName();
            if (room.contains(" ")) {
                System.out.println("hello");
                String[] sen = room.split(" ");
                sen[0] = sen[0] +"%20";
                room = sen[0]+sen[1];
            }
            System.out.println(room);
            String device = devlist.get(pos).getName();
            String year = "" + (args[0].getYear() + 1900);
            System.out.println(year);
            String month = dtom(args[0].getMonth());
            String date = "" + args[0].getDate();
            String week = dtod(args[0].getDay());
            String result = "";
                String link = new Link().getLink() + "/project11/" + username + "/Rooms/" + room + "/devices/" + device + "/getWeekly.php";
                try {
                    URL url = new URL(link);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    BufferedReader bufferedReader;
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    result = bufferedReader.readLine();
                    String[] array = result.split(" ");
                    for(int i=array.length-1;i>=0;i--)
                    {
                        listm.add("For "+array[i]);
                        link = new Link().getLink() + "/project11/" + username + "/Rooms/" + room + "/devices/" + device + "/"+array[i]+".txt";
                        url = new URL(link);
                        con = (HttpURLConnection)url.openConnection();
                        bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        while((result=bufferedReader.readLine())!=null)
                        {
                            listm.add(result);
                        }
                    }

                } catch (MalformedURLException e) {

                } catch (ConnectException e) {
                    result = "Connection Error";
                } catch (FileNotFoundException e) {
                  result = "File not Found";
                } catch (Exception e) {
                    result = "Error Occurred";
                    e.printStackTrace();
                }
            return result;
        }
        protected void onPostExecute(String result) {
            creategraph(context,"Weekly Usage");

        }


    }

    class getDailyStamp extends AsyncTask<Date, String, String> {
        private Context context;

        public getDailyStamp(Context context) {
            this.context = context;
        }

        protected void onPreExecute() {
            super.onPreExecute();


        }

        protected String doInBackground(Date... args) {
            list.clear();
            String username = user.getUsername();
            String room = user.getRooms().get(posroom).getName();
            if (room.contains(" ")) {
                String[] sen = room.split(" ");
                sen[0] = sen[0] +"%20";
                room = sen[0]+sen[1];
            }
            System.out.println(room);
            String device = devlist.get(pos).getName();
            String year = "" + (args[0].getYear() + 1900);
            System.out.println(year);
            Long timestamps = args[0].getTime();
            String link = new Link().getLink() + "/project11/" + username + "/Rooms/" + room + "/devices/" + device + "/GetDaily.php";
            //link = link+"?username="+username+"&password="+password;
            String result = "";
            System.out.println(link);
            try {
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader;
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                result = bufferedReader.readLine();
                link = new Link().getLink() + "/project11/" + username + "/Rooms/" + room + "/devices/" + device + "/"+result+".txt";
                System.out.println(link);
                url = new URL(link);
                con = (HttpURLConnection) url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while((result=bufferedReader.readLine())!=null)
                {
                    list.add(result);
                }



            } catch (MalformedURLException e) {

            } catch (ConnectException e) {
                result = "Connection Error";
            } catch (FileNotFoundException e) {
                result = "Wrong Username or Password";
            } catch (Exception e) {
                result = "Error Occurred";
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String result) {
                creategraph(context,"Daily Usage");

        }


    }
    class getMonthlyTimeStamp extends AsyncTask<Date,String,String>
    {
        private Context context;

        public getMonthlyTimeStamp(Context context) {
            this.context = context;
        }
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(Date... args) {
            listw.clear();
            String username = user.getUsername();
            String room = user.getRooms().get(posroom).getName();
            if (room.contains(" ")) {
                System.out.println("hello");
                String[] sen = room.split(" ");
                sen[0] = sen[0] +"%20";
                room = sen[0]+sen[1];
            }
            System.out.println(room);
            String device = devlist.get(pos).getName();
            String result = "";
            String link = new Link().getLink() + "/project11/" + username + "/Rooms/" + room + "/devices/" + device + "/getMonthly.php";
            try {
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader;
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                result = bufferedReader.readLine();
                String[] array = result.split(" ");
                for(int i=array.length-1;i>=0;i--)
                {
                    listw.add("For "+array[i]);
                    link = new Link().getLink() + "/project11/" + username + "/Rooms/" + room + "/devices/" + device + "/"+array[i]+".txt";
                    url = new URL(link);
                    con = (HttpURLConnection)url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    while((result=bufferedReader.readLine())!=null)
                    {
                        listw.add(result);
                    }
                }

            } catch (MalformedURLException e) {

            } catch (ConnectException e) {
                result = "Connection Error";
            } catch (FileNotFoundException e) {
                result = "File not Found";
            } catch (Exception e) {
                result = "Error Occurred";
                e.printStackTrace();
            }
            return result;
        }
        protected void onPostExecute(String result) {
            creategraph(context,"Monthly Usage");
        }
    }
}

