package com.example.cantor.pruebamultiplayerv3;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GamePlayActivity extends AppCompatActivity {
    private LocalNetworkManager managerApplication;
    private TextView textRed;
    private TextView textGreen;
    private TextView textBlue;
    private TextView textYellow;
    private EditText textWrite;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        managerApplication = (LocalNetworkManager)getApplication();
        managerApplication.checkin();


        textRed = (TextView) findViewById(R.id.textViewRed);
        textGreen = (TextView) findViewById(R.id.textViewGreen);
        textBlue = (TextView) findViewById(R.id.textViewBlue);
        textYellow = (TextView) findViewById(R.id.textViewYellow);
        textWrite = (EditText) findViewById(R.id.editTextWrite);
        sendButton = (Button)findViewById(R.id.buttonSend);

        loop.start();


    }

    public void onClick(View v){
        managerApplication.setInfo(Constants.UUID_STRING, textWrite.getText().toString());
    }


    private CountDownTimer loop = new CountDownTimer((long) Double.POSITIVE_INFINITY, 100) {
        public void onTick(long millisUntilFinished) {
            String[] list = managerApplication.getInfo();
            textRed.setText(list[0]);
            textGreen.setText(list[1]);
            textBlue.setText(list[2]);
            textYellow.setText(list[3]);
        }

        @Override
        public void onFinish() {

        }
    };

    public void onBackPressed(){
        loop.cancel();
    }
}
