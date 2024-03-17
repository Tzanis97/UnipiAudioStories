package com.example.unipiaudiostories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {
    FirebaseDatabase database;
    StorageReference storage;
    DatabaseReference databaseReference,userfavRef,usertimeRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    SharedPreferences languagePreference;
    MyTts myTts;
    TextView descr,authYear;
    ImageView cover;
    Bundle b;
    String keyTitle;
    SwitchMaterial seeText;
    CardView storyTextCardview;
    Menu menu;
    boolean isFavorite;
    Integer timesListened;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if language preference is set, otherwise set default to English
        languagePreference = getSharedPreferences("language",MODE_PRIVATE);
        String savedLang = languagePreference.getString("language", "en");
        setLocale(savedLang);
        setContentView(R.layout.activity_main2);

        //TTS
        myTts = new MyTts(this);

        //initializing views
        cover = findViewById(R.id.imageView2);
        authYear = findViewById(R.id.textView4);
        descr = findViewById(R.id.textView2);
        descr.setMovementMethod(new ScrollingMovementMethod());
        seeText = findViewById(R.id.switch1);
        storyTextCardview = findViewById(R.id.storyPresentCard);
        storyTextCardview.setVisibility(View.INVISIBLE);

        //toolbar setup
        Toolbar storyToolbar = findViewById(R.id.materialToolbar2);
        setSupportActionBar(storyToolbar);
        b = getIntent().getExtras();
        int key = b.getInt("TopbarTitle");
        switch (key){
            case 1:
                getSupportActionBar().setTitle(getResources().getString(R.string.little_red_riding_hood));
                break;
            case 2:
                getSupportActionBar().setTitle(getResources().getString(R.string.cinderella));
                break;
            case 3:
                getSupportActionBar().setTitle(getResources().getString(R.string.ugly_duckling));
                break;
            case 4:
                getSupportActionBar().setTitle(getResources().getString(R.string.the_magic_flute));
                break;
            case 5:
                getSupportActionBar().setTitle(getResources().getString(R.string.king_midas));
                break;
        }
        storyToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
        storyToolbar.showOverflowMenu();

        //authentication and user initializing
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //database and storage setup
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        b = getIntent().getExtras();
        keyTitle = b.getString("Title");
        assert keyTitle != null;
        databaseReference = database.getReference(keyTitle);
        userfavRef = database.getReference().child("users").child(user.getUid()).child("favorites").child(keyTitle);
        usertimeRef = database.getReference().child("users").child(user.getUid()).child("timesListened").child(keyTitle);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                descr.setText(snapshot.child("Descr").getValue().toString());
                authYear.setText(snapshot.child("AuthYear").getValue().toString());
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

        //listener in order for the user to be able to see or hide story's full text
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
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        this.menu = menu;
        onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //get user's current story favorite status
        userfavRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isFavorite = snapshot.getValue(Boolean.class);
                // Set the appropriate icon based on the isFavorite value
                onFavoriteChanged(isFavorite);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.favorites) {
            // Toggle the favorite status using the utility method
            PreferenceUtils.toggleFavorite(keyTitle,this::onFavoriteChanged);
            return true;
        } else if (item.getItemId() == R.id.voicetostats) {
            //voice command for the user to see stats and favs
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            startActivityForResult(intent,123);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void onFavoriteChanged(boolean isFavorite) {
        // Update UI based on the new favorite status
        // Find the menu item by its ID
        MenuItem favoriteItem = menu.findItem(R.id.favorites);
        if (favoriteItem != null) {
            favoriteItem.setIcon(isFavorite ? R.drawable.filledfavorite : R.drawable.favorite);
        }
    }



    //setup pop-up menu when the navigation icon is clicked
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.top_bar_main_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle item selection
                if (item.getItemId() == R.id.statistics) {
                    Intent intent = new Intent(MainActivity2.this, MainActivity4.class);
                    startActivity(intent);
                    return true;}
                else if (item.getItemId() == R.id.greek) {
                    saveLanguage("el");
                    setLocale("el");
                    recre();
                    return true;
                }
                else if (item.getItemId() == R.id.english) {
                    saveLanguage("en");
                    setLocale("en");
                    invalidateOptionsMenu();
                    recre();
                    return true;
                }
                else if (item.getItemId() == R.id.french) {
                    saveLanguage("fr");
                    setLocale("fr");
                    invalidateOptionsMenu();
                    recre();
                    return true;
                }
                else{
                    return false;
                }
            }
        });
        popupMenu.show();
    }

    //text to speech to start listening to story
    public void listen(View view){
        //increase listen counter of the story by 1 for this user
        usertimeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timesListened = snapshot.getValue(Integer.class);
                if (timesListened != null) {
                    // Update the value
                    timesListened++;
                    // Set the updated value back to the database
                    usertimeRef.setValue(timesListened);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        //Break the text to chunks less than 4000chars in order myTts to be able to put them in queue
        List<String> textChunks = TextSplit.splitText(descr.getText().toString());
        for (String chunk : textChunks) {
            myTts.speak(chunk);
        }
    }

    //stop listening
    public void stop(View view){
        myTts.stopSpeaking();
    }

    //whenever i get back the menu icons will be updated accordingly with the changes
    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        invalidateOptionsMenu();
        recre();
    }
    //    private void updateFavoriteIcon() {
//        MenuItem favoriteItem = menu.findItem(R.id.favorites);
//        if (favoriteItem != null) {
//            // Update the favorite icon based on the preference
//            if (isFavorite == null) {
//                favoriteItem.setIcon(R.drawable.favorite); // Set your default icon here
//            } else {
//                // Set the appropriate icon based on the isFavorite value
//                favoriteItem.setIcon(isFavorite ? R.drawable.filledfavorite : R.drawable.favorite);
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==123  && resultCode==RESULT_OK){
            ArrayList<String> recognisedText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            try{
                if (recognisedText.get(0).equals(getResources().getString(R.string.showstatsvoice))){
                    Intent intent = new Intent(MainActivity2.this, MainActivity4.class);
                    startActivity(intent);
                }
            } catch (IllegalAccessError e){
                showMessage("Error","Not valid message, try again");
            }
        }
    }
    //method to apply the new selected language
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    // Method to save language preference to SharedPreferences
    private void saveLanguage(String lang) {
        SharedPreferences.Editor editor = languagePreference.edit();
        editor.putString("language",lang);
        editor.apply();
    }
    //method to refresh the activity
    public void recre(){
        this.recreate();
    }

    private void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}