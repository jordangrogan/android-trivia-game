package edu.pitt.cs1699.androidtriviagame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d("Action", "Opening firebase database");
        final DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference scoresDB = fb.child("playerscores").child(uid);

        scoresDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> scoresList = new ArrayList<>();
                double max = 0.0;
                NumberFormat formatter = new DecimalFormat("#0");

                Log.d("dataSnapshot", dataSnapshot.toString());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    double percentage = Double.parseDouble(snapshot.getValue().toString()) / 5 * 100;

                    if (percentage > max) {
                        max = percentage;
                    }
                    Log.d("add2scoreslist", snapshot.getKey().toString() + "\t\t\t\t\t" + formatter.format(percentage) + "%");
                    scoresList.add(snapshot.getKey().toString() + "\t\t\t\t\t" + formatter.format(percentage) + "%");
                }

                Log.d("action", "Putting scoresList on list");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ScoreHistoryActivity.this, android.R.layout.simple_list_item_1, scoresList);
                ListView list = (ListView) findViewById(R.id.listScoreHistory);
                list.setAdapter(adapter);

                // Update High Score in Database and on UI
                TextView highestScore = (TextView) findViewById(R.id.txtHighestScore);
                String highestScoreText = "Highest Score: " + formatter.format(max) + "%";
                highestScore.setText(highestScoreText);
                DatabaseReference playerHighScoresDB = fb.child("playerhighscores");
                playerHighScoresDB.child(uid).setValue(max/100*5);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("cancel", "db cancel");
            }
        });

    }

    public void back(View view) {
        finish(); // Will finish this Activity & go back to the Start Activity
    }

}
