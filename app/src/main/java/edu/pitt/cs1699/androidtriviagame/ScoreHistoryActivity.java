package edu.pitt.cs1699.androidtriviagame;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class ScoreHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_history);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        SharedPreferences prefs = getSharedPreferences("triviagamescores",MODE_PRIVATE);
        String s = prefs.getString("scores", "");
        Log.d("Scores", s);

        if(!s.equals("")) {

            String scores[]= s.split(";");
            Log.d("Number of Scores=",scores.length+"");

            ArrayList<String> scoresList = new ArrayList<>();

            double max = 0.0;
            NumberFormat formatter = new DecimalFormat("#0");

            for(int i=(scores.length-1); i>=0; i--) {
                String[] scoreItem = scores[i].split(",");
                double percentage = Double.parseDouble(scoreItem[1])/5*100;

                if(percentage > max) {
                    max = percentage;
                }

                scoresList.add(scoreItem[0] + "\t\t\t\t\t" + formatter.format(percentage) + "%");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scoresList);
            ListView list = (ListView) findViewById(R.id.listScoreHistory);
            list.setAdapter(adapter);

            // Set txtHighestScore
            TextView highestScore = (TextView) findViewById(R.id.txtHighestScore);
            highestScore.setText("Highest Score: " + formatter.format(max) + "%");

        }

    }


    public void back(View view) {
        finish(); // Will finish this Activity & go back to the Start Activity
    }

}
