package com.example.cantor.pruebamultiplayerv3;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;


import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Cantor on 07/04/2016.
 */

@BusInterface(name = "com.example.cantor.pruebamultiplayerv3.lobby", announced = "true")
public interface LobbyInterface {

    @BusProperty(annotation = BusProperty.ANNOTATE_EMIT_CHANGED_SIGNAL)
    String getName() throws BusException;

    @BusProperty
    void setName(String name) throws BusException;

    @BusMethod(replySignature = "b")
    boolean isGameOn() throws BusException;

    @BusMethod(signature = "s", replySignature = "i")
    int addUser(String uuid) throws BusException;

    @BusMethod(signature = "s")
    void eraseUser(String uuid) throws BusException;


    @BusMethod
    void clearLstUsers() throws BusException;

    @BusMethod(replySignature = "as")
    String[] getLstUsers() throws BusException;

    @BusMethod(signature = "ss")
    void setInfo(String uuid, String string) throws BusException;

    @BusMethod(replySignature = "as")
    String[] getInfo() throws BusException;
}
