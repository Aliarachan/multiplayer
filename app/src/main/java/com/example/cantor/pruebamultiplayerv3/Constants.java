package com.example.cantor.pruebamultiplayerv3;
import android.os.Build;
import java.util.UUID;

/**
 * Created by Cantor on 07/04/2016.
 */
public class Constants {

    public static final String UUID_STRING = UUID.randomUUID().toString();
    public static final String VERSION_NUMBER = Build.VERSION.RELEASE;
    public static final byte[] APP_ID = {42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42};
    public static final String INTERFACE_NAME = "com.example.cantor.pruebamultiplayerv3.lobby";
    public static final String SHARED_PREFERENCES_NAME = "pruebamultiplayerv3";
}
