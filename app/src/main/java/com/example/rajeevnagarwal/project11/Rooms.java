package com.example.rajeevnagarwal.project11;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rajeev Nagarwal on 7/13/2016.
 */
public class Rooms implements Serializable {
    private String name;
    private ArrayList<Devices> dev;
    public Rooms(String name)
    {
        this.name = name;
        dev = new ArrayList<Devices>();

    }
    public String getName()
    {
        return this.name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public ArrayList<Devices> getDev()
    {
        return dev;
    }
    public void AddDevice(Devices device)
    {
        this.dev.add(device);
    }





}
