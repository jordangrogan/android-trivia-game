package edu.pitt.cs1699.androidtriviagame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class AddWordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);
    }

    public void addWord(View view) {

        EditText txtWord = findViewById(R.id.txtWord);
        String word = txtWord.getText().toString();

        EditText txtDef = findViewById(R.id.txtDef);
        String def = txtDef.getText().toString();

        PrintStream output = null;
        try {
            output = new PrintStream(
                    openFileOutput("customwordsdefinitions.txt", MODE_APPEND));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        output.println(word);
        output.println(def);
        output.close();

        Log.d("Add Word", "File written to");

        Toast.makeText(this, "Word Added", Toast.LENGTH_SHORT).show();
        finish(); // Will finish this Activity & go back to the Start Activity

    }
}
