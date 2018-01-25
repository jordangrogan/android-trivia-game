package edu.pitt.cs1699.androidtriviagame;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Switch;

public class StartActivity extends AppCompatActivity {

    private static MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mp = MediaPlayer.create(this, R.raw.gamemusic);
        mp.setLooping(true);
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

    public void musicOnOff(View view) {
        Switch musicSwitch = findViewById(R.id.musicSwitch);

        if (musicSwitch.isChecked()) {
            // MUSIC ON
            Log.d("Music Switch", "Is Checked");
            mp.start();
        } else {
            // MUSIC OFF
            Log.d("Music Switch", "Is Not Checked");
            mp.pause();
        }
    }

}
