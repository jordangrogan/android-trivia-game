package edu.pitt.cs1699.androidtriviagame;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

public class TopScoresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_scores);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d("Action", "Opening firebase database");
        final DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference scoresDB = fb.child("highscores");

        Query query1 = scoresDB.orderByKey();
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> scoresList = new ArrayList<>();
                NumberFormat formatter = new DecimalFormat("#0");

                Log.d("dataSnapshot", dataSnapshot.toString());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String place = snapshot.getKey().toString();
                    String scoreStr = "";
                    String nameStr = "";
                    try {
                        scoreStr = snapshot.child("score").getValue().toString();
                        nameStr = snapshot.child("user").getValue().toString();
                    } catch(NullPointerException e) { // Issue where database was being updated & getValue returning null values during that time
                        scoreStr = "0";
                        nameStr = "";
                        Log.d("Exception", "Null pointer exception");
                    }
                    double percentage = Double.parseDouble(scoreStr) / 5 * 100;
                    scoresList.add(place + ".\t" + nameStr + "\t\t\t\t\t" + formatter.format(percentage) + "%");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, scoresList);
                ListView list = (ListView) findViewById(R.id.listScoreHistory);
                list.setAdapter(adapter);

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
