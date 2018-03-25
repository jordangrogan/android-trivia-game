package edu.pitt.cs1699.androidtriviagame;

import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;

public class StartActivity extends AppCompatActivity {

    public static boolean textToSpeechTurnedOn = false;
    public static TextToSpeech tts;
    public static boolean ttsIsReady = false;
    public static final int REQ_CODE_ADD_WORD = 1111;
    private static final int REQ_CODE_TAKE_PICTURE = 30210;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2222;
    private static File photoFile;
    public static boolean topTenDataLoaded = false;
    public static boolean wordsDataLoaded = false;
    private static LocalBroadcastManager mBroadcastMgr;

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


        // Display Name & Profile Pic
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getDisplayName();
        String uid = user.getUid();
        Log.d("Name", name);
        Log.d("ID", uid);
        TextView txtName = (TextView) findViewById(R.id.txtName);
        txtName.setText(name);

        // Check to see if profile pic exists ("UID".jpg)
        // If it does, set that to imgProfilePic imageview by calling setProfilePic method
        File photosDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!photosDir.exists()) { photosDir.mkdirs(); }
        photoFile = new File(photosDir, uid + ".jpg");
        setProfilePic();

        // Notify player when top 10 list changes
        notifyWhenTop10Changes();

        // Synchronize Word Database
        synchronizeWordDatabase();

        // Register Broadcast Receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("edu.pitt.cs1699.androidtriviagame.wordssynced");
        intentFilter.addCategory("android.intent.category.DEFAULT");
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sendWordAddedNotification();
            }
        };
        mBroadcastMgr = LocalBroadcastManager.getInstance(getApplicationContext());
        mBroadcastMgr.registerReceiver(mReceiver, intentFilter);

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

    public void topScores(View view) {
        Intent intent = new Intent(this, TopScoresActivity.class);
        startActivity(intent);
    }

    public void changeProfilePic(View view) {
        // Open camera, & ON ACTIVITY RESULT save it as an image as "UID".jpg & set imgProfilePic ImageView
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d("Save to", photoFile.toURI().toString());
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build()); // https://stackoverflow.com/questions/48117511/exposed-beyond-app-through-clipdata-item-geturi?rq=1
        picIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(picIntent, REQ_CODE_TAKE_PICTURE);
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

        // ADD WORD
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

        // TOOK PICTURE FROM CAMERA, now need to set the profile pic
        if (requestCode == REQ_CODE_TAKE_PICTURE && resultCode == RESULT_OK) {
            Log.d("action", "took pic");
            setProfilePic();
        }
    }

    private void setProfilePic() {
        Log.d("Set", "profile pic");

        if(photoFile.exists()) {
            Log.d("Exists?", "YES");

            // Check to make sure we have permission to read files from external storage
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // Request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            } else {

                // Permission has already been granted

                ImageView img = (ImageView) findViewById(R.id.imgProfilePic);
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.fromFile(photoFile)));
                    img.setImageBitmap(bitmap);
                } catch (FileNotFoundException fnfe) {
                    Log.wtf("onActivityResult", fnfe);
                }

            }

        } else {
            // The profile pic file doesn't exist yet.
            Log.d("Exists?", "NO");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, try to set the profile pic again
                setProfilePic();

            }
        }
    }

    private void notifyWhenTop10Changes() {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference scoresDB = fb.child("highscores");

        scoresDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Purpose of the topTenDataLoaded variable is that we only want to send the notification when data is CHANGED, not on the initial loading of the data
                if(topTenDataLoaded) {
                    Log.d("Top 10", "Data Change, sending notification");
                    Notification.Builder builder = new Notification.Builder(StartActivity.this)
                            .setContentTitle("Top 10 Scores Updated")
                            .setContentText("The top 10 scores in Android Trivia have been updated!")
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.topten);

                    Notification notification = builder.build();

                    NotificationManager manager = (NotificationManager)
                            getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(1, notification);

                } else {
                    topTenDataLoaded = true;
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("cancel", "db cancel");
            }
        });
    }

    private void synchronizeWordDatabase() {

        Log.d("Action", "Opening firebase database");
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference wordsDB = fb.child("words");

        wordsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("dataSnapshot", dataSnapshot.toString());

                final DataSnapshot data = dataSnapshot;

                Log.d("Action", "Creating thread");
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("Action", "Creating or opening local database");
                        final SQLiteDatabase db = openOrCreateDatabase("wordslist", MODE_PRIVATE, null);
                        db.execSQL("CREATE TABLE IF NOT EXISTS words (word text UNIQUE, def text);");

                        for (DataSnapshot snapshot : data.getChildren()) {
                            Log.d("Add word FB", snapshot.getKey().toString() + "//" + snapshot.getValue().toString());
                            db.execSQL("REPLACE INTO words (word, def) VALUES('" + snapshot.getKey().toString() + "', '" + snapshot.getValue().toString() + "');");
                        }

                        db.close();

                        Log.d("THREAD", "Data updated in local database.");


                        // When the thread is done, it notifies an activity using the Broadcast Receivers mechanism
                        Intent intent = new Intent();
                        intent.setAction("edu.pitt.cs1699.androidtriviagame.wordssynced");
                        intent.addCategory("android.intent.category.DEFAULT");
                        mBroadcastMgr.sendBroadcast(intent);

                    }
                }).start();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("cancel", "db cancel");
            }
        });


    }

    private void sendWordAddedNotification() {
        // Purpose of wordsDataLoaded global variable:
        // Requirement states to only notify when a word is ADDED (thus, on the initial load, a notification is not sent!)
        if(wordsDataLoaded) {
            Log.d("BR", "Thread complete & message received from Broadcast Receiver, sending notification.");
            Notification.Builder builder = new Notification.Builder(StartActivity.this)
                    .setContentTitle("Word Added")
                    .setContentText("A word has been added to Android Trivia!")
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.trivia);

            Notification notification = builder.build();

            NotificationManager manager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(2, notification);
        } else {
            wordsDataLoaded = true;
        }
    }

}
