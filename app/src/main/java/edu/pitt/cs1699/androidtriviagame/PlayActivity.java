package edu.pitt.cs1699.androidtriviagame;

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

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class PlayActivity extends AppCompatActivity {

    private static List<Word> words = new ArrayList<>();

    private static List<String> defs = new ArrayList<>();

    private static List<Word> currentGameWordList;

    private static List<String> currentWordAnswerList;

    private static int currentWord = 0;

    private static int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);


        // ADD WORDS FROM RESOURCE
        Scanner scan = new Scanner(
                getResources().openRawResource(R.raw.wordsdefinitions));
        while (scan.hasNextLine()) {
            String w = scan.nextLine();
            Log.d("Word:", w);
            String d = scan.nextLine();
            Log.d("Definition:", d);
            words.add(new Word(w,d));
            defs.add(d);
        }

        // ADD WORDS FROM CUSTOM WORDS DEFINITIONS FILE
        try {
            scan = new Scanner(
                    openFileInput("customwordsdefinitions.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (scan.hasNextLine()) {
            String w = scan.nextLine();
            Log.d("Word:", w);
            String d = scan.nextLine();
            Log.d("Definition:", d);
            words.add(new Word(w,d));
            defs.add(d);
        }

        scan.close();

        // Current Game: Generate 5 words by shuffling and truncating list to 5 words
        currentGameWordList = new ArrayList<>(words);
        Collections.shuffle(currentGameWordList);
        currentGameWordList = currentGameWordList.subList(0, 5);

        Log.d("Word 1", currentGameWordList.get(0).word);
        Log.d("Word 2", currentGameWordList.get(1).word);
        Log.d("Word 3", currentGameWordList.get(2).word);
        Log.d("Word 4", currentGameWordList.get(3).word);
        Log.d("Word 5", currentGameWordList.get(4).word);

        askQuestion();

    }

    private void askQuestion() {

        // Set Progress Bar
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress((currentWord + 1) * 20);

        // Current Word: Generate answer list by removing answer, shuffling, selecting first 4, adding back answer, shuffling again
        currentWordAnswerList = new ArrayList<>(defs);
        currentWordAnswerList.remove(currentGameWordList.get(currentWord).def);
        Collections.shuffle(currentWordAnswerList);
        currentWordAnswerList = currentWordAnswerList.subList(0, 4);
        currentWordAnswerList.add(currentGameWordList.get(currentWord).def);
        Collections.shuffle(currentWordAnswerList);

        Log.d("Word " + currentWord + " Answer Option 1", currentWordAnswerList.get(0));
        Log.d("Word " + currentWord + " Answer Option 2", currentWordAnswerList.get(1));
        Log.d("Word " + currentWord + " Answer Option 3", currentWordAnswerList.get(2));
        Log.d("Word " + currentWord + " Answer Option 4", currentWordAnswerList.get(3));
        Log.d("Word " + currentWord + " Answer Option 5", currentWordAnswerList.get(4));

        TextView text = (TextView) findViewById(R.id.textWord);
        text.setText(currentGameWordList.get(currentWord).word);
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

        // End Activity
        finish();

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
