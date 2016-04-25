package com.example.cantor.pruebamultiplayerv3;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.alljoyn.bus.BusException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Cantor on 07/04/2016.
 */
public class LocalNetworkManager extends Application {
    ComponentName mRunningService = null;
    private static String PACKAGE_NAME;
    private static final String TAG = "ManagerApplication";
    private static ConcurrentHashMap<String, LobbyInterface> lstLobbies;
    private static Lobby lobby;
    private static User user;
    private static UsersFacade facade;
    private static Handler activityHandler;
    private static Handler serviceHandler;

    public static final int CONNECT_LOBBY = 1;
    public static final int CONNECT_USER = 2;
    public static final int DISCONNECT_LOBBY = 3;
    public static final int DISCONNECT_USER = 5;
    private static final int HANDLE_REFRESH_LOBBIES = 2;
    public static final int USER_REMOVE_LOBBY = 11;
    private static final int ADD_USER = 13;

    public void onCreate() {
        PACKAGE_NAME = getApplicationContext().getPackageName();
        Intent intent = new Intent(this, LocalNetworkServices.class);
        mRunningService = startService(intent);
        lstLobbies = new ConcurrentHashMap<String, LobbyInterface>();
        user = new User();
    }

    public void checkin() {
        if (mRunningService == null) {
            Intent intent = new Intent(this, LocalNetworkServices.class);
            mRunningService = startService(intent);
            if (mRunningService == null) {
                //TODO: fatal_error impossible to create services
            }
        }
    }

    public static ConcurrentHashMap<String, LobbyInterface> getLstLobbies() {
        return lstLobbies;
    }

    public static void  setLstLobbies(ConcurrentHashMap<String, LobbyInterface> lstLobbies) {
        LocalNetworkManager.lstLobbies = lstLobbies;
    }

    public static Lobby getLobby() {
        return lobby;
    }

    public static void createLobby(String name){
        lobby = new Lobby(name);
        facade = new UsersFacade(lobby);
        Message message = serviceHandler.obtainMessage(CONNECT_LOBBY);
        serviceHandler.sendMessage(message);
    }
   public static void setLobby(Lobby lobby) {
        LocalNetworkManager.lobby = lobby;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        LocalNetworkManager.user = user;
    }

    public static Handler getServiceHandler() {
        return serviceHandler;
    }

    public static void setServiceHandler(Handler serviceHandler) {
        LocalNetworkManager.serviceHandler = serviceHandler;
    }

    public static Handler getActivityHandler() {
        return activityHandler;
    }

    public static void setActivityHandler(Handler activityHandler) {
        LocalNetworkManager.activityHandler = activityHandler;
    }

    public static List<String> getLobbyNames(){
        ArrayList<LobbyInterface> lobbies = new ArrayList<>(lstLobbies.values());
        ArrayList<String> names = new ArrayList<>();
        for (LobbyInterface lobby: lobbies)
            try {
                Log.d(TAG, "Get name lobby");
                names.add(lobby.getName());
            } catch (BusException e){
                Log.d(TAG, "cant refresh");
                e.printStackTrace();
            }
        return names;
    }


    public static void userConnect() {
        user = new User();
        Message message = serviceHandler.obtainMessage(CONNECT_USER);
        serviceHandler.sendMessage(message);
    }

    public static void disconnectLobby() {
        Message message = serviceHandler.obtainMessage(DISCONNECT_LOBBY);
        serviceHandler.sendMessage(message);
    }

    public static void disconnectUser() {
        Message message = serviceHandler.obtainMessage(DISCONNECT_USER);
        serviceHandler.sendMessage(message);
    }

    public static void refreshActivity() {
        Log.d(TAG, "Refresh activity to its handler");
        Message message = activityHandler.obtainMessage(HANDLE_REFRESH_LOBBIES);
        activityHandler.sendMessage(message);
    }

    public static void addUser(String uuid) {
        facade.addUser(uuid);
    }

    public static String[] getLobbyUsersNames() throws Exception{
        return facade.getLstUsers();
    }

    public boolean isGameOn() throws Exception{
        return facade.isGameOn();
    }

    public void gameOn(){
        facade.gameOn();
    }

    public void initFacade(String name){
        LobbyInterface lobbyI = lstLobbies.get(name);
        facade = new UsersFacade(lobbyI);
    }

    public static boolean isInLobby(LobbyInterface lobby){
        return facade.isInLobby(lobby);
    }

    public static void releaseLobby(){
        facade.releaseLobby();
    }

    public void eraseUser(String uuid){
        facade.eraseUser(uuid);
    }

    public void setInfo(String uuid, String info){
        facade.setInfo(uuid, info);
    }

    public String[] getInfo(){
        return facade.getInfo();
    }
}
