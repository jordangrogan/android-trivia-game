package edu.pitt.cs1699.androidtriviagame;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class PlayActivity extends AppCompatActivity {


    private static List<Word> currentGameWordList;

    private static List<String> currentWordAnswerList;

    private static int currentWord = 0;

    private static int score = 0;

    private static FirebaseAuth mAuth;

    private static String uid;
    private static String uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        uname = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // Reset
        score = 0;
        currentWord = 0;
        setGameWordList();
        askQuestion();
    }

    private void setGameWordList() {

        // Generate currentGameWordList ArrayList from local database
        currentGameWordList = new ArrayList<>();
        SQLiteDatabase db = openOrCreateDatabase("wordslist", MODE_PRIVATE, null);
        Cursor cr = db.rawQuery("SELECT * FROM words", null);
        // Wait for there to be things in the local database
        if(cr.getCount() < 5) {
            Log.d("Local", "Local database not ready yet... querying again...");
            Toast.makeText(this, "Either not enough words are available or the local not synced with remote database.", Toast.LENGTH_LONG).show();
            finish();
        }
        Log.d("Local", "Things are in the local database!");
        if (cr.moveToFirst()) {
            do {
                String w = cr.getString(cr.getColumnIndex("word"));
                String d = cr.getString(cr.getColumnIndex("def"));
                Log.d("adding", w+":"+d);
                currentGameWordList.add(new Word(w, d));
            } while (cr.moveToNext());
            cr.close();
        }
        db.close();

        for(int j=0; j<currentGameWordList.size(); j++) {
            Log.d("currentGameWordList", j + ":" + currentGameWordList.get(j));
        }
        Log.d("currentGameWordList=", Arrays.toString(currentGameWordList.toArray()));
        // Current Game: Generate 5 words by shuffling and truncating list to 5 words
        //currentGameWordList = new ArrayList<>(words);
        Collections.shuffle(currentGameWordList);
        currentGameWordList = currentGameWordList.subList(0, 5);

        Log.d("Word 1", currentGameWordList.get(0).word);
        Log.d("Word 2", currentGameWordList.get(1).word);
        Log.d("Word 3", currentGameWordList.get(2).word);
        Log.d("Word 4", currentGameWordList.get(3).word);
        Log.d("Word 5", currentGameWordList.get(4).word);



    }

    private void askQuestion() {

        // Set Progress Bar
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress((currentWord + 1) * 20);

        // Generate currentWordAnswerList ArrayList from local database
        currentWordAnswerList = new ArrayList<>();
        SQLiteDatabase db = openOrCreateDatabase("wordslist", MODE_PRIVATE, null);
        Cursor cr = db.rawQuery("SELECT def FROM words", null);
        if (cr.moveToFirst()) {
            do {
                String d = cr.getString(cr.getColumnIndex("def"));
                currentWordAnswerList.add(d);
            } while (cr.moveToNext());
            cr.close();
        }
        db.close();

        //Log.d("defs=", Arrays.toString(defs.toArray()));
        // Current Word: Generate answer list by removing answer, shuffling, selecting first 4, adding back answer, shuffling again
        //currentWordAnswerList = new ArrayList<>(defs);
        Log.d("currentWordAnswerList=", Arrays.toString(currentWordAnswerList.toArray()));
        currentWordAnswerList.remove(currentGameWordList.get(currentWord).def);
        Log.d("currAnsLst PostRemoval=", Arrays.toString(currentWordAnswerList.toArray()));
        Collections.shuffle(currentWordAnswerList);
        Log.d("AnsList Shuffled=", Arrays.toString(currentWordAnswerList.toArray()));
        currentWordAnswerList = currentWordAnswerList.subList(0, 4);
        Log.d("AnsList Chopped=", Arrays.toString(currentWordAnswerList.toArray()));
        currentWordAnswerList.add(currentGameWordList.get(currentWord).def);
        Log.d("AnsListWithAns=", Arrays.toString(currentWordAnswerList.toArray()));
        Collections.shuffle(currentWordAnswerList);
        Log.d("Final Ans List =", Arrays.toString(currentWordAnswerList.toArray()));

        Log.d("Word " + currentWord + " Answer Option 1", currentWordAnswerList.get(0));
        Log.d("Word " + currentWord + " Answer Option 2", currentWordAnswerList.get(1));
        Log.d("Word " + currentWord + " Answer Option 3", currentWordAnswerList.get(2));
        Log.d("Word " + currentWord + " Answer Option 4", currentWordAnswerList.get(3));
        Log.d("Word " + currentWord + " Answer Option 5", currentWordAnswerList.get(4));

        TextView text = (TextView) findViewById(R.id.textWord);
        String word = currentGameWordList.get(currentWord).word;
        text.setText(word);
        // Text to Speech Word
        if (StartActivity.ttsIsReady && StartActivity.textToSpeechTurnedOn) StartActivity.tts.speak(word, TextToSpeech.QUEUE_ADD, null);

        // Text to Speech Answers
        for (int i = 0; i < currentWordAnswerList.size(); i++) {
            if (StartActivity.ttsIsReady && StartActivity.textToSpeechTurnedOn) {
                StartActivity.tts.speak("Answer " + (i+1) + ". " + currentWordAnswerList.get(i), TextToSpeech.QUEUE_ADD, null);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currentWordAnswerList);
        ListView list = (ListView) findViewById(R.id.listDefinitions);
        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> list, View row, int index, long rowID) {
                        // code to run when user clicks that item
                        checkAnswer(index);
                        Log.d("Clicked Index", index + "");
                    }
                }
        );
        list.setAdapter(adapter);

    }

    private void checkAnswer(int index) {

        if(currentWordAnswerList.get(index).equals(currentGameWordList.get(currentWord).def)) {
            Log.d("Correct?", "YES!");
            score++;
            Toast.makeText(this, "Correct Answer! Your score is " + score, Toast.LENGTH_SHORT).show();
        } else {
            Log.d("Correct?", "NOPE!");
            Toast.makeText(this, "Wrong Answer!", Toast.LENGTH_SHORT).show();
        }

        if(currentWord != 4) {

            // Go to next word

            currentWord++;
            askQuestion();

        } else {
            endGame();
        }

    }

    private void endGame() {

        // Display Score
        Toast.makeText(this, "Final Score: " + score, Toast.LENGTH_SHORT).show();

        // Save Score
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference player = fb.child("playerscores").child(uid);

        Date currentDateTime = Calendar.getInstance().getTime();
        Log.d("CurrentDateTime", currentDateTime.toString());

        player.child(currentDateTime.toString()).setValue(score);

        refreshTopScores();

        // End Activity
        finish();

    }

    private void refreshTopScores() {

        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference scoresDB = fb.child("highscores");

        Query query1 = scoresDB.orderByKey();
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
                DatabaseReference scoresDB = fb.child("highscores");

                int place = 0;

                Log.d("dataSnapshot", dataSnapshot.toString());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    int thisScore = Integer.parseInt(snapshot.child("score").getValue().toString());
                    String thisUser = snapshot.child("user").getValue().toString();
                    if(score > thisScore) {
                        // Replace top score
                        place = Integer.parseInt(snapshot.getKey().toString());
                        Log.d("place", place+"");
                        break;
                    }
                }

                if(place > 0) {
                    // It needs to be **inserted** somewhere in the list of top scores (not at the end)
                    Log.d("topten","Needs to be inserted");
                    int numPlaces = (int) dataSnapshot.getChildrenCount();
                    // Bubble the places down
                    for(int j=(numPlaces+1); j>place; j--) {
                        if(j != 11) { // Don't care about 11th place, not going to add it to database
                            int prevScore = Integer.parseInt(dataSnapshot.child(j-1 + "").child("score").getValue().toString());
                            String prevUser = dataSnapshot.child(j-1 + "").child("user").getValue().toString();
                            scoresDB.child(j+"").child("score").setValue(prevScore);
                            scoresDB.child(j+"").child("user").setValue(prevUser);
                        }
                    }
                    scoresDB.child(place+"").child("score").setValue(score);
                    scoresDB.child(place+"").child("user").setValue(uname);

                    Toast.makeText(PlayActivity.this, "Congrats! You made the top 10 overall!", Toast.LENGTH_LONG).show();
                } else if((int)dataSnapshot.getChildrenCount() < 10) {
                    // It can go in last place & there's room in the list
                    int lastPlace = (int)dataSnapshot.getChildrenCount() + 1;
                    scoresDB.child(lastPlace+"").child("score").setValue(score);
                    scoresDB.child(lastPlace+"").child("user").setValue(uname);
                    Toast.makeText(PlayActivity.this, "Congrats! You made the top 10 overall!", Toast.LENGTH_LONG).show();
                }
                // else it doesn't make the list


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("cancel", "db cancel");
            }
        });

    }

    private static class Word {
        public String word;
        public String def;

        public Word(String w, String d) {
            word = w;
            def = d;
        }
    }
}
