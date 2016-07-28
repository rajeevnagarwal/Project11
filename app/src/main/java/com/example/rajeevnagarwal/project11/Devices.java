package com.example.rajeevnagarwal.project11;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Rajeev Nagarwal on 7/13/2016.
 */
public class Devices implements Serializable {
    private String name;
    private Boolean status;
    private ArrayList<Date> login;
    private ArrayList<Date> logout;
    private String IP_Addr;
    private String Port;
    public Devices(String name,Boolean status,String IP_Addr,String Port)
    {
        this.name = name;
        this.status = status;
        this.login = new ArrayList<Date>();
        this.logout = new ArrayList<Date>();
        this.IP_Addr =  IP_Addr;
        this.Port = Port;
    }
    public String getName()
    {
        return this.name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public Boolean getStatus()
    {
        return this.status;
    }
    public void setStatus(Boolean status)
    {
        this.status = status;
    }
    public ArrayList<Date> getLogin()
    {
        return this.login;
    }
    public ArrayList<Date> getLogout(){ return this.logout;}
    public String getIP_Addr()
    {
        return this.IP_Addr;
    }
    public String getPort()
    {
        return this.Port;
    }
    public void setIP_Addr(String IP_Addr)
    {
        this.IP_Addr = IP_Addr;
    }
    public void setPort(String Port)
    {
        this.Port = Port;
    }








}
