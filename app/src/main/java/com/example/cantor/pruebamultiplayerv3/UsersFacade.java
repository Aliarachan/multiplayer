package com.example.cantor.pruebamultiplayerv3;

import android.util.Log;

import org.alljoyn.bus.BusException;

/**
 * Created by Cantor on 23/04/2016.
 */
public class UsersFacade {
    private static final String TAG = "UsersFacade";
    private Lobby lobby = null;
    private LobbyInterface lobbyI = null;
    private boolean isHost = false;

    public UsersFacade(Lobby lobby){
        this.lobby = lobby;
        isHost = true;
    }

    public UsersFacade(LobbyInterface lobbyI){
        this.lobbyI = lobbyI;
    }
    /**
    public String getName(){
        return null;
    }

    public void setName(String name){

    } **/

    public void addUser(String uuid){
        if (isHost){
            try {
                Log.d(TAG, "HOST could  add an user");
                lobby.addUser(uuid);
            } catch (BusException e) {
                e.printStackTrace();
                Log.d(TAG, " HOST error adding an user");
            }
        } else {
            try {
                Log.d(TAG, "USER could be added");
                lobbyI.addUser(uuid);
            } catch (BusException e) {
                e.printStackTrace();
                Log.d(TAG, "USER error adding user");
            }
        }

    }

    public void clearUsersList(){

    }

    public String[] getLstUsers() throws Exception{
        if (isHost){
            try {
                Log.d(TAG, "HOST could get users list");
                return lobby.getLstUsers();
            } catch (BusException e) {
                e.printStackTrace();
                Log.d(TAG, "HOST ERROR getting users list");
            }
        } else {
            try {
                Log.d(TAG, "USER could get users list");
                return lobbyI.getLstUsers();
            } catch (BusException e) {
                Log.d(TAG, "USER error getting users lst");
                e.printStackTrace();
            }
        }
        return null;
    }


    public boolean isGameOn() throws Exception{
        if (isHost){
            try {
                Log.d(TAG, "HOST game on");
                return lobby.isGameOn();
            } catch (BusException e) {
                Log.d(TAG, "HOST error game on");
                e.printStackTrace();
            }
        } else {
            try {
                Log.d(TAG, "USER game on");
                return lobbyI.isGameOn();
            } catch (BusException e) {
                Log.d(TAG, "USER error game on");
                e.printStackTrace();
            }
        }
        return false;
    }

    public void gameOn(){
        if (isHost){
            lobby.gameOn();
        }
    }

    public boolean isInLobby(LobbyInterface lobbyI){
        return (this.lobbyI.equals(lobbyI));
    }

    public void releaseLobby(){
        if (isHost){
            lobby = null;
        } else {
            lobbyI = null;
        }
    }

    public void eraseUser(String uuid){
        if (!isHost){
            try {
                lobbyI.eraseUser(uuid);
            } catch (BusException e) {
                e.printStackTrace();
            }
        }
    }

    public void setInfo(String uuid, String info){
        if (isHost){
            try {
                lobby.setInfo(uuid, info);
            } catch (BusException e) {
                e.printStackTrace();
            }
        } else {
            try {
                lobbyI.setInfo(uuid, info);
            } catch (BusException e) {
                e.printStackTrace();
            }
        }
    }

    public String[] getInfo(){
        String[] ret = new String[4];
        if (isHost){
            try {
                ret = lobby.getInfo();
            } catch (BusException e) {
                e.printStackTrace();
            }
        } else {
            try {
                ret = lobbyI.getInfo();
            } catch (BusException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}
