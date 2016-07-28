package com.example.rajeevnagarwal.project11;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Stats extends AppCompatActivity {

   Spinner lst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        final ArrayList<Devices> devlist = (ArrayList<Devices>)intent.getExtras().get("devlist");
        setContentView(R.layout.activity_stats);
        lst = (Spinner)findViewById(R.id.listView);
        final int pos = intent.getExtras().getInt("pos");
        String[] values = {"Weekly Usage","Monthly Usage","Daily Usage"};
        final FrameLayout frame = (FrameLayout)findViewById(R.id.frame);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,values);
        lst.setAdapter(adapter);
        lst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(lst.getSelectedItem());
                if(lst.getSelectedItem().equals("Weekly Usage"))
                {

                    frame.removeAllViews();
                    BarChart chart = new BarChart(getApplicationContext());

                    HashMap<String,Long> login = new HashMap<String,Long>();
                    int ln = devlist.get(pos).getLogin().size();
                    int lg = devlist.get(pos).getLogout().size();
                    int x = Math.min(ln, lg);
                    for(int i=0;i<x;i++)
                    {
                        String day1 = dtod(devlist.get(pos).getLogin().get(i).getDay());
                        String day2 = dtod(devlist.get(pos).getLogout().get(i).getDay());
                        long diff = (Math.abs(devlist.get(pos).getLogin().get(i).getTime()-devlist.get(pos).getLogout().get(i).getTime())/1000);
                        if(day1.equals(day2))
                        {
                            if(!login.containsKey(day1))
                            {
                                login.put(day1,diff);
                            }
                            else if(login.containsKey(day1))
                            {
                                login.put(day1,login.get(day1)+diff);
                            }

                        }

                    }

                    ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
                    int i=0;
                    for(Map.Entry<String,Long> entry: login.entrySet())
                    {
                        System.out.println(entry.getKey()+entry.getValue());

                    }
                    String[] array = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
                    for(int j=0;j<array.length;j++)
                    {
                        if(login.containsKey(array[j]))
                        {
                            entries.add(new BarEntry((float)login.get(array[j]),j));
                        }
                        else
                        {
                            entries.add(new BarEntry(0f,j));
                        }
                    }
                    BarDataSet dataset = new BarDataSet(entries, "# of Hours");
                    ArrayList<String> labels = new ArrayList<String>();
                    labels.add("Monday");
                    labels.add("Tuesday");
                    labels.add("Wednesday");
                    labels.add("Thursday");
                    labels.add("Friday");
                    labels.add("Saturday");
                    labels.add("Sunday");
                    chart.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
                    BarData data = new BarData(labels, dataset);
                    chart.setData(data);
                    chart.setDescription("Your weekly usage of this device");
                    chart.animateXY(2000, 2000);
                    dataset.setColors(ColorTemplate.COLORFUL_COLORS);
                    frame.addView(chart);

                }
                else if(lst.getSelectedItem().equals("Monthly Usage"))
                {
                    frame.removeAllViews();
                    HashMap<String,Long> login = new HashMap<String,Long>();
                    int ln = devlist.get(pos).getLogin().size();
                    int lg = devlist.get(pos).getLogout().size();
                    int x = Math.min(ln, lg);
                    for(int i=0;i<x;i++)
                    {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(devlist.get(pos).getLogin().get(i));
                        int m1 = cal.get(Calendar.MONTH);
                        cal.setTime(devlist.get(pos).getLogout().get(i));
                        int m2 = cal.get(Calendar.MONTH);
                        System.out.println(m1+m2);
                        String month1 = dtom(m1);
                        String month2 = dtom(m2);
                        System.out.println(month1+month2);
                        long diff = (Math.abs(devlist.get(pos).getLogin().get(i).getTime()-devlist.get(pos).getLogout().get(i).getTime())/1000);
                        if(month1.equals(month2))
                        {
                            if(!login.containsKey(month1))
                            {
                                login.put(month1,diff);
                            }
                            else if(login.containsKey(month1))
                            {
                                login.put(month1,login.get(month1)+diff);
                            }

                        }

                    }
                    ArrayList<Entry> entries = new ArrayList<Entry>();
                    PieChart p = new PieChart(getApplicationContext());
                    String[] array = {"January","February","March","April","May","June","July","August","September","October","November","December"};
                    ArrayList<String> labels = new ArrayList<String>();
                    for(int i=0;i<array.length;i++)
                    {

                        if(login.containsKey(array[i]))
                        {
                            labels.add(array[i]);
                            entries.add(new Entry((float)login.get(array[i]),i));
                        }


                    }
                    PieDataSet dataset = new PieDataSet(entries,"No. of hours");
                    dataset.setColors(ColorTemplate.COLORFUL_COLORS);
                    PieData data = new PieData(labels,dataset);
                    p.setData(data);
                    p.getLegend().setPosition(Legend.LegendPosition.LEFT_OF_CHART);
                    p.animateXY(2000,2000);

                    frame.addView(p);



                }
                else if(lst.getSelectedItem().equals("Daily Usage"))
                {
                    frame.removeAllViews();
                    int ln = devlist.get(pos).getLogin().size();
                    int lg = devlist.get(pos).getLogout().size();
                    int x = Math.max(ln, lg);
                    ArrayList<Entry> entries = new ArrayList<Entry>();
                    ArrayList<String> lables = new ArrayList<String>();
                    LineChart lineChart = new LineChart(getApplicationContext());


                    for(int i=0;i<ln+lg;i++)
                    {
                        if(i<ln&&i<lg)
                        {
                            /*if(devlist.get(pos).getLogin().get(i).before(devlist.get(pos).getLogout().get(i)))
                            {*/
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(devlist.get(pos).getLogin().get(i));
                                int hour = cal.get(Calendar.SECOND);
                                System.out.println(hour);
                            lables.add(devlist.get(pos).getLogin().get(i).toString());
                            entries.add(new Entry(10.0f, i));
                            System.out.println("hello");
                            cal.setTime(devlist.get(pos).getLogout().get(i));
                                hour = cal.get(Calendar.SECOND);
                                System.out.println(hour);
                            entries.add(new Entry(5.0f, i));
                            lables.add(devlist.get(pos).getLogout().get(i).toString());


                        }
                        else if(i>ln&&i<lg)
                        {
                            System.out.println("hello");
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(devlist.get(pos).getLogout().get(i));
                            int hour = cal.get(Calendar.SECOND);
                            System.out.println(hour);
                            entries.add(new Entry(5.0f, i));
                            lables.add(devlist.get(pos).getLogout().get(i).toString());

                        }
                        else if(i>lg&&i<ln)
                        {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(devlist.get(pos).getLogin().get(i));
                            int hour = cal.get(Calendar.SECOND);
                            System.out.println(hour);
                            entries.add(new Entry(10.0f,i));
                            lables.add(devlist.get(pos).getLogin().get(i).toString());

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

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }
    public String dtod(int d)
    {
        switch(d)
        {
            case 1: return "Monday";
            case 2: return "Tuesday";
            case 3: return "Wednesday";
            case 4: return "Thursday";
            case 5: return "Friday";
            case 6: return "Saturday";
            case 7: return "Sunday";
            default: return "noday";

        }
    }
    public String dtom(int d)
    {
        switch(d+1)
        {
            case 1: return "January";
            case 2: return "February";
            case 3: return "March";
            case 4: return "April";
            case 5: return "May";
            case 6: return "June";
            case 7: return "July";
            case 8: return "August";
            case 9: return "September";
            case 10: return "October";
            case 11: return "November";
            case 12: return "December";
            default: return "nomonth";
        }
    }



}
