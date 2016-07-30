package com.example.rajeevnagarwal.project11;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Rajeev Nagarwal on 7/30/2016.
 */
public class GetStatus extends AsyncTask<Void,String,String> {
    private Context context;
    private User user;
    public GetStatus(Context context,User user)
    {
        this.context = context;
        this.user = user;
    }
    protected void onPreExecute()
    {
        super.onPreExecute();

    }
    protected String doInBackground(Void... args)
    {
        String result="";
        for(int i=0;i<user.getRooms().size();i++)
        {
            String[] sen={};
            String link="";
            String room="";
            for(int j=0;j<user.getRooms().get(i).getDev().size();j++)
            {
                if(user.getRooms().get(i).getName().contains(" "))
                {
                    sen = user.getRooms().get(i).getName().split(" ");
                    room = sen[0]+"%20"+sen[1];
                     //link = Link.link+"/project11/"+user.getUsername()+"/Rooms/"+room+"/devices/"+user.getRooms().get(i).getDev().get(j).getName()+"/FindStatus.php";

                }
                else
                    room = user.getRooms().get(i).getName();
                link = Link.link+"/project11/"+user.getUsername()+"/Rooms/"+room+"/devices/"+user.getRooms().get(i).getDev().get(j).getName()+"/FindStatus.php";

                System.out.println(link);

                try
                {
                    URL url = new URL(link);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    BufferedReader bufferedReader;
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    result = bufferedReader.readLine();
                    if(!result.equals("No Such Record"))
                    {

                        link = Link.link+"/project11/"+user.getUsername()+"/Rooms/"+room+"/devices/"+user.getRooms().get(i).getDev().get(j).getName()+"/"+result+".txt";
                        url = new URL(link);
                        con = (HttpURLConnection)url.openConnection();
                        bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        while((result=bufferedReader.readLine())!=null)
                        {
                            System.out.println(result);
                        }
                        if(result!=null)
                        if(result.contains(" ")) {
                            String[] words = result.split(" ");
                            if (words[1].equals("1"))
                                user.getRooms().get(i).getDev().get(j).setStatus(true);
                            else if (words[0].equals("0"))
                                user.getRooms().get(i).getDev().get(j).setStatus(false);
                        }
                    }

                }
                catch(MalformedURLException e)
                {

                }
                catch(IOException e)
                {

                }

            }
        }
        return result;
    }
    protected void onPostExecute(String result)
    {
        Intent i = new Intent(context, Homepage.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("user", user);
        context.startActivity(i);
    }


}
