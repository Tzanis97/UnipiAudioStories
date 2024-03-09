package com.example.unipiaudiostories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity3 extends AppCompatActivity {
    EditText email,password;
    FirebaseAuth mAuth;
    FirebaseUser user;
    SharedPreferences languagePreference;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if language preference is set, otherwise set default to English
        languagePreference = getSharedPreferences("language",MODE_PRIVATE);
        String savedLang = languagePreference.getString("language", "en");
        setLocale(savedLang);
        setContentView(R.layout.activity_main3);

        //initializing
        email = findViewById(R.id.editTextText);
        password = findViewById(R.id.editTextTextPassword);

        //authorization
        mAuth = FirebaseAuth.getInstance();

//        // Check if language preference is set, otherwise set default to English
//        languagePreference = getSharedPreferences("language",MODE_PRIVATE);
//        String savedLang = languagePreference.getString("language", "en");
//        if (!savedLang.equals("el")){
//            setLocale(savedLang);
//        }
        //Toolbar Config
        Toolbar toolbar = findViewById(R.id.materialToolbar2);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    public void signin(View view){
        //automatically signing-in if user is connected
        if (user!=null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
            mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        user = mAuth.getCurrentUser();
                        Intent intent = new Intent(MainActivity3.this, MainActivity.class);
                        showMessage("Success","User signed in!",intent);
                    } else{
                        //the task will note us on what is wrong
                        showMessage("Error",task.getException().getLocalizedMessage(),null);
                    }
                }
            });
        } else {
            showMessage("Error","Please provide valid email or password!",null);
        }
        //mAuth.signOut();
    }

    public void signup(View view){
        if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
            mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        user = mAuth.getCurrentUser();

                        //create new SharedPreference for keeping user stats and favs
                        //this will be stored locally in the users device
                        if (user!=null){
                            String userId = user.getUid();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
                            //create new node in users
                            userRef.setValue(userId);
                            //update the userRef to target the root of new users node
                            userRef = userRef.child(userId);


//                            SharedPreferences userPreference = getSharedPreferences(userId+"_preferences",MODE_PRIVATE);
//                            SharedPreferences.Editor editor = userPreference.edit();
                            String[] storyKeys = {"RedHood", "Cinderella", "UglyDuckling","MagicFlute","Midas"};

                            // Iterate through each story key for the user
                            for (String storyKey : storyKeys) {
                                // Sample favorite and times listened values
//                                boolean favorite = false; // Example: Replace with logic to determine if it's a favorite
//                                int timesListened = 0; // Example: Replace with actual value

//                                // Store the values in SharedPreferences
//                                editor.putBoolean(storyKey + "_favorite", favorite);
//                                editor.putInt(storyKey + "_times_listened", timesListened);
                                userRef.child("favorites").child(storyKey).setValue(false);
                                userRef.child("timesListened").child(storyKey).setValue(0);
                            }

//                            // Commit changes
//                            editor.apply();
                        }
                        Intent intent = new Intent(MainActivity3.this, MainActivity.class);
                        showMessage("Success","Your profile is created!",intent);
                    } else{
                        //what is wrong the task will note us
                        showMessage("Error",task.getException().getLocalizedMessage(),null);
                    }
                }
            });
        } else {
            showMessage("Error","Please provide email and password!",null);
        }
        //mAuth.signOut();
    }

    //setup pop-up menu when the navigation icon is clicked
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.top_bar_main_menu);
        //hide the statistics option
        popupMenu.getMenu().findItem(R.id.statistics).setVisible(false);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.greek) {
                    saveLanguage("el");
                    setLocale("el");
                    recre();
                    return true;
                }
                else if (item.getItemId() == R.id.english) {
                    saveLanguage("en");
                    setLocale("en");
                    recre();
                    return true;
                }
                else if (item.getItemId() == R.id.french) {
                    saveLanguage("fr");
                    setLocale("fr");
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

    //method to apply the new selected language
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    //method to refresh the activity
    public void recre(){
        this.recreate();
    }

    // Method to save language preference to SharedPreferences
    private void saveLanguage(String lang) {
        SharedPreferences.Editor editor = languagePreference.edit();
        editor.putString("language",lang);
        editor.apply();
    }
    private void showMessage(String title, String message, final Intent intent) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (intent!=null){
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .show();
    }
}