package edu.pitt.cs1699.androidtriviagame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileNotFoundException;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQ_CODE_TAKE_PICTURE = 30210;
    private static File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, REQ_CODE_TAKE_PICTURE);

        File photosDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!photosDir.exists()) { photosDir.mkdirs(); }

        photoFile = new File(photosDir, "profilePic.jpg");
        Button btn = (Button) findViewById(R.id.btnSetPic);
        if(photoFile.exists()) {
            btn.setEnabled(false);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getDisplayName();
        Log.d("Name", name);
        TextView txtName = (TextView) findViewById(R.id.txtName);
        txtName.setText(name);

    }

    public void setProfilePic(View view) {

        Intent picIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // create the file to save the image into
        startActivityForResult(picIntent, REQ_CODE_TAKE_PICTURE);

        picIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

        // Disable button
        Button btn = (Button) findViewById(R.id.btnSetPic);
        btn.setEnabled(false);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == REQ_CODE_TAKE_PICTURE && resultCode == RESULT_OK) {
            Uri targetUri = intent.getData(); // location of photo file
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(targetUri));
                ImageView img = (ImageView) findViewById(R.id.camera_image);
                img.setImageBitmap(bitmap);
            } catch (FileNotFoundException fnfe) {
                Log.e("onActivityResult", fnfe.toString());
            }
        }
    }

}
