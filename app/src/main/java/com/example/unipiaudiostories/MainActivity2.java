package com.example.unipiaudiostories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.nio.InvalidMarkException;

public class MainActivity2 extends AppCompatActivity {
    FirebaseDatabase database;
    StorageReference storage;
    DatabaseReference databaseReference;
    TextView title;
    EditText descr;
    ImageView cover;
    Bundle b;
    String keyTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        title = findViewById(R.id.textView);
        cover = findViewById(R.id.imageView2);
        descr = findViewById(R.id.editTextTextMultiLine);
        Toolbar storyToolbar = findViewById(R.id.materialToolbar2);
        setSupportActionBar(storyToolbar);
        storyToolbar.showOverflowMenu();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        b = getIntent().getExtras();
        //assert b != null;
        keyTitle = b.getString("Title");
        databaseReference = database.getReference(keyTitle);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                descr.setText(snapshot.child("Descr").getValue().toString());
                try{
                    File file = File.createTempFile("temp","jpg");
                    StorageReference imageref = storage.child(snapshot.child("Image").getValue().toString());
                    imageref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            cover.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                        }
                    });
                } catch (IOException e) {}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }


}