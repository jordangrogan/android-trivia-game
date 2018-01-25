package edu.pitt.cs1699.androidtriviagame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class PlayActivity extends AppCompatActivity {

    private static ArrayList<Word> words = new ArrayList<>();

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
        }

        scan.close();

    }

    private class Word {
        public String word;
        public String def;
        public Word(String w, String d) {
            word = w;
            def = d;
        }
    }
}
