package com.example.cantor.pruebamultiplayerv3;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.alljoyn.bus.AboutObj;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.Observer;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.SignalEmitter;
import org.alljoyn.bus.Status;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Cantor on 07/04/2016.
 */
public class LocalNetworkServices extends Service {
    private static final String TAG = "ServerService";
    static BusHandler busHandler;
    private ConcurrentHashMap<String, LobbyInterface> lstLobbies;
    private ConcurrentHashMap<LobbyInterface, String> reverseLstLobbies;
    private Lobby lobby;
    private User user;


    public void onCreate(){
        //La lista se inicializa en el onCreate del Manager
        lstLobbies = LocalNetworkManager.getLstLobbies();
        reverseLstLobbies = new ConcurrentHashMap<LobbyInterface, String>();
        HandlerThread thread = new HandlerThread("UserProvider");
        thread.start();
        busHandler = new BusHandler(thread.getLooper());
        LocalNetworkManager.setServiceHandler(busHandler);
        super.onCreate();
    }

    public class BusHandler extends Handler {
        private BusAttachment mBus;
        private AboutObj aboutObj;
        private LocalAboutDataListener aboutData;
        private Observer observer;

        public static final int CONNECT_LOBBY = 1;
        public static final int CONNECT_USER = 2;
        public static final int DISCONNECT_LOBBY = 3;
        public static final int DISCONNECT_USER = 5;
        public static final int ADD_LOBBY = 7;
        public static final int USER_REMOVE_LOBBY = 11;
        private static final int ADD_USER = 13;
        private static final int REMOVE_LOBBY = 17;
        private static final short CONTACT_PORT = 42;

        /**
         * Builds the class with the given message looper
         * @param looper
         */
        public BusHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch(msg.what){
                case CONNECT_LOBBY:
                    Log.d(TAG, "CONNECT_LOBBY");
                    /**
                     * Aqui seguro que entra
                     */
                    lobby = LocalNetworkManager.getLobby();
                    user = LocalNetworkManager.getUser();
                    org.alljoyn.bus.alljoyn.DaemonInit.PrepareDaemon(getApplicationContext());
                    mBus = new BusAttachment(getPackageName(), BusAttachment.RemoteMessage.Receive);

                    /*
                    * Register the BusObject with the path "/lobbyProperties"
                    */

                    Status status = mBus.registerBusObject(lobby, "/lobbyProperties");
                    if (status != Status.OK) {
                        //TODO: change this
                        Log.d(TAG, "Error registering bus object: " + status.toString());
                        return;
                    }

                    status = mBus.connect();
                    if (status != Status.OK) {
                        //TODO: change this
                        return;
                    }

                    status = mBus.registerSignalHandlers(LocalNetworkServices.this);
                    if (status != Status.OK) {
                        //TODO: change this
                        return;
                    }

                    /*
                     * Create a new session listening on the contact port of the user service
                     */
                    Mutable.ShortValue contactPort = new Mutable.ShortValue(CONTACT_PORT);

                    SessionOpts sessionOpts = new SessionOpts();
                    sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES;
                    sessionOpts.isMultipoint = false;
                    sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;
                    //Transport UDP
                    //Send information even without received confirmation
                    //TODO: transport_UPD?
                    sessionOpts.transports = SessionOpts.TRANSPORT_UDP;

                    status = mBus.bindSessionPort(contactPort, sessionOpts, new SessionPortListener() {
                        @Override
                        public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessionOpts) {
                            return sessionPort == CONTACT_PORT;
                        }
                    });
                    if (status != Status.OK) {
                        //TODO: change this
                        return;
                    }


                    aboutObj = new AboutObj(mBus);
                    aboutData = new LocalAboutDataListener();
                    status = aboutObj.announce(CONTACT_PORT, aboutData);
                    if (status != Status.OK) {
                        //TODO: change this
                        return;
                    }
                    //TODO: si algo peta, poner un observer aqui.
                    break;
                case CONNECT_USER:
                    org.alljoyn.bus.alljoyn.DaemonInit.PrepareDaemon(getApplicationContext());
                    mBus = new BusAttachment(getPackageName(), BusAttachment.RemoteMessage.Receive);
                    user = LocalNetworkManager.getUser();
                    /*
                    * Register the BusObject with the path "/lobbyProperties"
                    */

                    Log.d(TAG, "Registering user");
                    Status status2 = mBus.registerBusObject(user, "/lobbyProperties");
                    if (status2 != Status.OK) {
                        //TODO: change this
                        Log.d(TAG, "Error registering bus object: " + status2.toString());
                        return;
                    }
                    Log.d(TAG, "Reached connect: ");
                    status2 = mBus.connect();
                    if (status2 != Status.OK) {
                        //Message error: couldn't connect to the bus
                        //TODO: change this
                        return;
                    }

                    status2 = mBus.registerSignalHandlers(LocalNetworkServices.this);
                    if (status2 != Status.OK) {
                        //TODO: change this
                        return;
                    }
                   /*
                     * Create a new session listening on the contact port of the user service
                     */
                    Mutable.ShortValue contactPort2 = new Mutable.ShortValue(CONTACT_PORT);

                    SessionOpts sessionOpts2 = new SessionOpts();
                    sessionOpts2.traffic = SessionOpts.TRAFFIC_MESSAGES;
                    sessionOpts2.isMultipoint = false;
                    sessionOpts2.proximity = SessionOpts.PROXIMITY_ANY;
                    //Transport UDP
                    //Send information even without received confirmation
                    sessionOpts2.transports = SessionOpts.TRANSPORT_UDP;

                    status = mBus.bindSessionPort(contactPort2, sessionOpts2, new SessionPortListener() {
                        @Override
                        public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessionOpts) {
                            return sessionPort == CONTACT_PORT;
                        }
                    });
                    if (status != Status.OK) {
                        //TODO: change this
                        return;
                    }

                    aboutObj = new AboutObj(mBus);
                    aboutData = new LocalAboutDataListener();
                    status = aboutObj.announce(CONTACT_PORT, aboutData);
                    if (status != Status.OK) {
                        //TODO: change this
                        Toast toast = Toast.makeText(LocalNetworkServices.this, "Error sending about info", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    /**
                     * The observer used to discover and control the users in the network
                     */
                    observer = new Observer(mBus, new Class[]{LobbyInterface.class});
                    observer.registerListener(new Observer.Listener() {
                        @Override
                        /**
                         * When a new lobby is discovered, automatically put this in our list of lobbies.
                         * Lobbies are only created if an user requires it, so we will only discover valid lobbies
                         */
                        public void objectDiscovered(ProxyBusObject proxyBusObject) {
                            //TODO: PROVISIONAL------------------------------------------------------------------------------------------------------------

                            Message msg = obtainMessage(ADD_LOBBY);
                            msg.obj = proxyBusObject;
                            sendMessage(msg);

                        }

                        @Override
                        /**
                         * If a lobby is lost, it means that the host has closed it.
                         * Remove it from our list of lobbies.
                         */
                        public void objectLost(ProxyBusObject proxyBusObject) {
                            Log.d(TAG, "User lost a lobby");
                            LobbyInterface lobbiI = (LobbyInterface)proxyBusObject.getInterface(LobbyInterface.class);
                            String name = reverseLstLobbies.get(lobbiI);
                            if (LocalNetworkManager.isInLobby(lobbiI)){
                                LocalNetworkManager.releaseLobby();
                            }
                            lstLobbies.remove(name);
                            reverseLstLobbies.remove(lobbiI);
                            LocalNetworkManager.refreshActivity();
                        }
                    });
                    break;
                case DISCONNECT_LOBBY:
                    try {
                        Log.d(TAG, "Clear users list");
                        lobby.clearLstUsers();
                    } catch (BusException e) {
                        e.printStackTrace();
                    }
                    observer.close();
                    mBus.unregisterBusObject(lobby);
                    //TODO: watch clear
                    //lstLobbies.clear();
                    lobby = null;
                    LocalNetworkManager.setLobby(null);
                    mBus.disconnect();
                    break;
                case DISCONNECT_USER:
                    observer.close();
                    mBus.unregisterBusObject(user);
                    lstLobbies.clear();
                    user = null;
                    LocalNetworkManager.setUser(null);
                    mBus.disconnect();
                    break;
                case ADD_LOBBY:
                    ProxyBusObject obj = (ProxyBusObject) msg.obj;
                    // We enable the property cache so as to avoid calling multiple times each phone
                    obj.enablePropertyCaching();
                    obj.setReplyTimeout(1000);
                    LobbyInterface lobbyI = obj.getInterface(LobbyInterface.class);
                    String name = null;
                    try {
                        name = lobbyI.getName();
                        Log.d(TAG, "Got name: " + name);
                        lstLobbies.put(name, lobbyI);
                        reverseLstLobbies.put(lobbyI, name);
                        //lobby.addUser();
                        LocalNetworkManager.refreshActivity();
                    } catch (BusException e) {
                        Log.d(TAG, "Error bus exception catching a lobby's name");
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

    static {
        System.loadLibrary("alljoyn_java");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
