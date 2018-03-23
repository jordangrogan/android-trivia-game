package edu.pitt.cs1699.androidtriviagame;

import android.app.FragmentManager;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Switch;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StartActivity extends AppCompatActivity {

    public static boolean textToSpeechTurnedOn = false;
    public static TextToSpeech tts;
    public static boolean ttsIsReady = false;
    public static final int REQ_CODE_ADD_WORD = 1111;

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
        FragmentManager manager = getFragmentManager();
        AddWordFragment fragment = new AddWordFragment();
        fragment.show(manager, "add_word_fragment_name");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQ_CODE_ADD_WORD && resultCode == RESULT_OK) {
            // extract returned parameters from the intent
            String word = intent.getStringExtra("word");
            String defn = intent.getStringExtra("definition");
            // ADD WORD TO DATABASE
            Log.d("word", word);
            Log.d("defn", defn);
            DatabaseReference fb = FirebaseDatabase.getInstance().getReference().child("words");
            fb.child(word).setValue(defn);
        }
    }

}
