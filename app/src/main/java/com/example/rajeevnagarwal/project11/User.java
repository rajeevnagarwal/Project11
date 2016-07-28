package com.example.rajeevnagarwal.project11;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rajeev Nagarwal on 7/13/2016.
 */
public class User implements Serializable{
        private String name;
        private String username;
        private String password;
        private ArrayList<Rooms> rooms;
        public User(String name,String username,String password)
        {
            this.name = name;
            this.username = username;
            this.password  = password;
            rooms = new ArrayList<Rooms>();
        }
        public String getName()
        {
            return this.name;
        }
        public String getUsername()
        {
            return this.username;
        }
        public String getPassword()
        {
            return this.password;
        }
        public void sePassword(String password)
        {
            this.password = password;
        }


        public void setName(String name)
        {
            this.name = name;
        }
        public void setUsername(String username)
        {
            this.username = username;
        }
        public ArrayList<Rooms> getRooms()
        {
            return this.rooms;
        }
        public void addRooms(Rooms room)
        {
            this.rooms.add(room);
        }







}
