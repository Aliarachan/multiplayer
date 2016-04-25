package com.example.cantor.pruebamultiplayerv3;

import android.util.Log;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.Position;
import org.alljoyn.bus.annotation.Signature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Cantor on 07/04/2016.
 */
public class Lobby implements LobbyInterface, BusObject {
    private static final String TAG = "Lobby";
    private ArrayList<String> lstUsers;
    private String[] lstColors, mensages;
    private String name;
    private boolean gameOn = false;

    public Lobby(String name){
        this.name = name;
        lstUsers = new ArrayList<>();
        lstColors = new String[4];
        mensages = new String[4];
        for(int i = 0; i <= 3; i++){
            lstColors[i] = "";
            mensages[i] = "";
        }
    }

    @Override
    public String getName() throws BusException {
        return name;
    }

    @Override
    public void setName(String name) throws BusException {
        this.name = name;

    }
    @Override
    public boolean isGameOn() throws BusException{
        return gameOn;
    }

    public void gameOn(){
        gameOn = true;
    }


    @Override
    public int addUser(String uuid) throws BusException {
        int size = lstUsers.size();
        Log.d(TAG, "size " + size);
        Log.d(TAG, "uuid " + uuid);
        if (size < 4){
            lstUsers.add(uuid);
            return 0;
        }
        return 1;
    }


    @Override
    public void eraseUser(String uuid) throws BusException{
        lstUsers.remove(uuid);
    }

    @Override
    public void clearLstUsers() throws BusException {
        lstUsers.clear();
    }

    @Override
    public String[] getLstUsers() throws BusException {
        for(int i = 0; i <= 3; i++){
            lstColors[i] = "";
        }
        int i = 0;
        for(String user: lstUsers){
            lstColors[i] = user;
            i++;
        }
        return lstColors;
    }

    @Override
    public void setInfo(String uuid, String string) throws BusException {
        int i = 0;
        for(String user: lstUsers){
            if (uuid.equals(user)){
                mensages[i]=string;
            }
            i++;
        }
    }

    @Override
    public String[] getInfo() throws BusException {
        return mensages;
    }

}
