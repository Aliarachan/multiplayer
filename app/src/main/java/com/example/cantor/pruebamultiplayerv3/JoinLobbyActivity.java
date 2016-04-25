package com.example.cantor.pruebamultiplayerv3;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class JoinLobbyActivity extends AppCompatActivity{
    private LocalNetworkManager managerApplication = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_lobby);

        managerApplication = (LocalNetworkManager)getApplication();
        managerApplication.checkin();
        managerApplication.userConnect();


        final ListView batList = (ListView)findViewById(R.id.listViewLobbies);
        managerApplication.setActivityHandler(mHandler);

        batList.setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = batList.getItemAtPosition(position).toString();
                managerApplication.initFacade(name);
                managerApplication.addUser(Constants.UUID_STRING);
                Intent batIntent = new Intent(JoinLobbyActivity.this, CreateLobbyActivity.class);
                batIntent.putExtra("tmpID", 1);
                batIntent.putExtra("roomName", name);
                startActivity(batIntent);
                JoinLobbyActivity.this.finish();
            }
        });
        //A lo mejor al entrar ya hay salas creadas
        refreshLobbies();

    }

    public void onClick(View v){
        refreshLobbies();
    }

    public void onBackPressed(){
        managerApplication.disconnectUser();
        this.finish();
    }



    private void refreshLobbies() {
        Toast toast = Toast.makeText(JoinLobbyActivity.this, "Refreshing lobbies", Toast.LENGTH_SHORT);
        toast.show();
        ListView batList = (ListView)findViewById(R.id.listViewLobbies);
        ArrayAdapter<String> channelListAdapter = new ArrayAdapter<String>(this, android.R.layout.test_list_item);
        batList.setAdapter(channelListAdapter);
        //Encontramos los nombres de los users que han entrado
        List<String> names = managerApplication.getLobbyNames();
        for (String name : names) {
            channelListAdapter.add(name);
        }
        channelListAdapter.notifyDataSetChanged();
    }


    private static final int HANDLE_REFRESH_LOBBIES = 2;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_REFRESH_LOBBIES:
                    refreshLobbies();
                default:
                    break;
            }
        }
    };
}
