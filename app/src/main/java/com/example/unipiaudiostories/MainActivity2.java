package com.example.unipiaudiostories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
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
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    FirebaseDatabase database;
    StorageReference storage;
    DatabaseReference databaseReference;
    MyTts myTts;
    TextView descr;
    ImageView cover;
    Bundle b;
    String keyTitle;
    SwitchMaterial seeText;
    CardView storyTextCardview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //TTS
        myTts = new MyTts(this);

        //get the database reference
        b = getIntent().getExtras();

        //initializing views
        cover = findViewById(R.id.imageView2);
        descr = findViewById(R.id.textView2);
        descr.setMovementMethod(new ScrollingMovementMethod());
        seeText = findViewById(R.id.switch1);
        storyTextCardview = findViewById(R.id.storyPresentCard);
        storyTextCardview.setVisibility(View.INVISIBLE);

        //toolbar setup
        Toolbar storyToolbar = findViewById(R.id.materialToolbar2);
        setSupportActionBar(storyToolbar);
        getSupportActionBar().setTitle(b.getString("TopbarTitle"));
        storyToolbar.showOverflowMenu();

        //database and storage setup
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
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

        seeText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    storyTextCardview.setVisibility(View.VISIBLE);
                } else {
                    storyTextCardview.setVisibility(View.GONE);
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void listen(View view){
        //Break the text to chunks less than 4000chars in order myTts to be able to put them in queue
        List<String> textChunks = TextSplit.splitText(descr.getText().toString());
        for (String chunk : textChunks) {
            myTts.speak(chunk);
        }
    }
    public void stop(View view){
        myTts.stopSpeaking();
    }
}