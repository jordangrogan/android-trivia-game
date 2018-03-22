package edu.pitt.cs1699.androidtriviagame;

import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Switch;

public class StartActivity extends AppCompatActivity {

    public static boolean textToSpeechTurnedOn = false;
    public static TextToSpeech tts;
    public static boolean ttsIsReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Initialize Text to Speech
        // Initializing it in StartActivity even though using it in PlayActivity because it takes a while to initialize.
        tts = new TextToSpeech(this,
            new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    // code to run when done loading
                    ttsIsReady = true;
                }
            });
    }

    public void play(View view) {
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
    }

    public void addWord(View view) {
        Intent intent = new Intent(this, AddWordActivity.class);
        startActivity(intent);
    }

    public void scoreHistory(View view) {
        Intent intent = new Intent(this, ScoreHistoryActivity.class);
        startActivity(intent);
    }

    public void profile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void readOnOff(View view) {
        Switch readSwitch = findViewById(R.id.readSwitch);

        if (readSwitch.isChecked()) {
            // MUSIC ON
            Log.d("Read Switch", "Is Checked");
            textToSpeechTurnedOn = true;
        } else {
            // MUSIC OFF
            Log.d("Read Switch", "Is Not Checked");
            textToSpeechTurnedOn = false;
        }
    }

}
