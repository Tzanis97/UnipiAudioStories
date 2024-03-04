package com.example.unipiaudiostories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database;
    StorageReference storageReference;
    DatabaseReference redhoodReference,cinderellaReference,ducklingReference,fluteReference,midasReference;

    TextView redhood,cinderella,duckling,flute,midas;
    ImageView redhoodImage,cinderellaImage,ducklingImage,fluteImage,midasImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar Config
        Toolbar toolbar = findViewById(R.id.materialToolbar2);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        //Initialize views
        redhood = findViewById(R.id.redHoodTextView);
        cinderella = findViewById(R.id.cinderellaTextView);
        duckling = findViewById(R.id.DuckingTextView);
        flute = findViewById(R.id.fluteTextView);
        midas = findViewById(R.id.midasTextView);
        redhoodImage = findViewById(R.id.imageViewStory1);
        cinderellaImage = findViewById(R.id.imageStory2);
        ducklingImage = findViewById(R.id.imageStory3);
        fluteImage = findViewById(R.id.imageViewStory4);
        midasImage = findViewById(R.id.imageViewStory5);

        //Firebase Config
        database = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        redhoodReference = database.getReference("RedHood");
        cinderellaReference = database.getReference("Cinderella");
        ducklingReference = database.getReference("UglyDuckling");
        fluteReference = database.getReference("MagicFlute");
        midasReference = database.getReference("Midas");
        redhoodReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    File file = File.createTempFile("temp","jpg");
                    StorageReference imageref = storageReference.child(snapshot.child("Image").getValue().toString());
                    imageref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            redhoodImage.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                        }
                    });
                } catch (IOException e) {}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        cinderellaReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    File file = File.createTempFile("temp","jpg");
                    StorageReference imageref = storageReference.child(snapshot.child("Image").getValue().toString());
                    imageref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            cinderellaImage.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                        }
                    });
                } catch (IOException e) {}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        ducklingReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    File file = File.createTempFile("temp","jpg");
                    StorageReference imageref = storageReference.child(snapshot.child("Image").getValue().toString());
                    imageref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            ducklingImage.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                        }
                    });
                } catch (IOException e) {}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        fluteReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    File file = File.createTempFile("temp","jpg");
                    StorageReference imageref = storageReference.child(snapshot.child("Image").getValue().toString());
                    imageref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            fluteImage.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                        }
                    });
                } catch (IOException e) {}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        midasReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    File file = File.createTempFile("temp","jpg");
                    StorageReference imageref = storageReference.child(snapshot.child("Image").getValue().toString());
                    imageref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            midasImage.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                        }
                    });
                } catch (IOException e) {}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    //setup pop-up menu when the navigation icon is clicked
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.top_bar_main_menu);
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                // Handle item selection
//                switch (item.getItemId()) {
//                    case :
//                        Intent intent = new Intent(MainActivity.this, MainActivity4.class);
//                        startActivity(intent);
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//        });
        popupMenu.show();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.top_bar_main_menu,menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }

    public void goCinderella(View view){
        Intent intent = new Intent(this,MainActivity2.class);
        intent.putExtra("Title","Cinderella");
        intent.putExtra("TopbarTitle",cinderella.getText().toString());
        startActivity(intent);
    }

    public void goRedHood(View view){
        Intent intent = new Intent(this,MainActivity2.class);
        intent.putExtra("Title","RedHood");
        intent.putExtra("TopbarTitle",redhood.getText().toString());
        startActivity(intent);
    }

    public void goUgly(View view){
        Intent intent = new Intent(this,MainActivity2.class);
        intent.putExtra("Title","UglyDuckling");
        intent.putExtra("TopbarTitle",duckling.getText().toString());
        startActivity(intent);
    }

    public void goFlute(View view){
        Intent intent = new Intent(this,MainActivity2.class);
        intent.putExtra("Title","MagicFlute");
        intent.putExtra("TopbarTitle",flute.getText().toString());
        startActivity(intent);
    }

    public void goMidas(View view){
        Intent intent = new Intent(this,MainActivity2.class);
        intent.putExtra("Title","Midas");
        intent.putExtra("TopbarTitle",midas.getText().toString());
        startActivity(intent);
    }
}