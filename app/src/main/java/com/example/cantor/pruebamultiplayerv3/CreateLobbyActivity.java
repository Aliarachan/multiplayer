package com.example.cantor.pruebamultiplayerv3;


import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class CreateLobbyActivity extends AppCompatActivity {
    private static final String TAG = "CreateLobbyActivity";
    private LocalNetworkManager managerApplication = null;
    private int currentUserId;
    private boolean confirmed = false;
    private EditText editTextRoomName;
    private Button confirmButton;
    private String batName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);

        managerApplication = (LocalNetworkManager)getApplication();
        managerApplication.checkin();


        editTextRoomName = (EditText) findViewById(R.id.editTextLobbyName);
        confirmButton = (Button)findViewById(R.id.buttonConfirm);

        Bundle b = getIntent().getExtras();
        currentUserId = b.getInt("tmpID");
        if (currentUserId == 1){
            batName = b.getString("roomName");
            confirmedRoom(batName);
            confirmButton.setFocusable(false);
            loop.start();
        }

        managerApplication.setActivityHandler(mHandler);

    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.buttonConfirm:
                if (!confirmed) {
                    batName = editTextRoomName.getText().toString();
                    managerApplication.createLobby(batName);
                    managerApplication.addUser(Constants.UUID_STRING);
                    confirmedRoom(batName);
                    loop.start();
                } else{
                    loop.cancel();
                    managerApplication.gameOn();
                    Intent intent = new Intent(CreateLobbyActivity.this, GamePlayActivity.class);
                    startActivity(intent);
                }
                //refreshUsersList();
                break;
            case R.id.buttonGoBack:
                //TODO: de momento lo dejo asi
                goBack();
                break;
        }
    }

    private void confirmedRoom(String roomName){
        confirmed = true;
        editTextRoomName.setFocusable(false);
        editTextRoomName.setText(roomName);
        confirmButton.setText("PlayStart!");
    }

    public void onBackPressed(){
        goBack();
    }

    private void goBack(){
        if (currentUserId == 0){
            managerApplication.disconnectLobby();
        } else {
            managerApplication.eraseUser(Constants.UUID_STRING);
        }
        loop.cancel();
        this.finish();
    }

    private void closeRoom(){
        if (currentUserId == 0){
            this.finish();
        } else {
            this.finish();
        }
    }

    private void refreshUsersList() throws Exception{
        ListView batList = (ListView)findViewById(R.id.listViewPlayers);
        ArrayAdapter<String> channelListAdapter = new ArrayAdapter<String>(this, android.R.layout.test_list_item);
        batList.setAdapter(channelListAdapter);
        //Encontramos los nombres de los users que han entrado
        String[] names = managerApplication.getLobbyUsersNames();
        int i = 0;
        String finalName = "";
        for (String name : names) {
            if (!name.equals("")) {
                switch(i){
                    case 0:
                        finalName = "Red";
                        break;
                    case 1:
                        finalName = "Green";
                        break;
                    case 2:
                        finalName = "Blue";
                        break;
                    case 3:
                        finalName = "Yellow";
                        break;
                }
                if (name.equals(Constants.UUID_STRING)){
                    finalName = "-> "+ finalName;
                }
                channelListAdapter.add(finalName);
            }
            i++;
        }
        channelListAdapter.notifyDataSetChanged();
    }
    //private static final int HANDLE_APPLICATION_QUIT_EVENT = 2;
    private static final int HANDLE_REFRESH_LOBBIES_AND_PLAYERS = 2;
    private static final int HANDLE_CLOSE_ROOM = 5;

    /**
     * El handler se suele definir aqui de forma implicita
     */
    //TODO probablemente quitaremos el Handler
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_REFRESH_LOBBIES_AND_PLAYERS:
                    try {
                        refreshUsersList();
                    } catch (Exception e){
                        CreateLobbyActivity.this.finish();
                    }
                    break;
                case HANDLE_CLOSE_ROOM:
                    closeRoom();
                default:
                    break;
            }
        }
    };

    private CountDownTimer loop = new CountDownTimer((long) Double.POSITIVE_INFINITY, 100) {
        public void onTick(long millisUntilFinished) {
            try {
                refreshUsersList();
                boolean bool = managerApplication.isGameOn();
                if (bool) {
                    loop.cancel();
                    Intent batIntent = new Intent(CreateLobbyActivity.this, GamePlayActivity.class);
                    startActivity(batIntent);
                }
            } catch (Exception e) {
                CreateLobbyActivity.this.finish();
            }
        }

        @Override
        public void onFinish() {

        }
    };

}

