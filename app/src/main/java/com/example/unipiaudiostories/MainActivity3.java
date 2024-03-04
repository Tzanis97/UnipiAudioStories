package com.example.unipiaudiostories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashSet;
import java.util.Set;

public class MainActivity3 extends AppCompatActivity {
    EditText email,password;
    FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        //initializing
        email = findViewById(R.id.editTextText);
        password = findViewById(R.id.editTextTextPassword);

        //authorization
        mAuth = FirebaseAuth.getInstance();
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
                        showMessage("Success","User signed in!");
                        user = mAuth.getCurrentUser();
                        Intent intent = new Intent(MainActivity3.this, MainActivity.class);
                        startActivity(intent);
                    } else{
                        //the task will note us on what is wrong
                        showMessage("Error",task.getException().getLocalizedMessage());
                    }
                }
            });
        } else {
            showMessage("Error","Please provide valid email or password!");
        }
        //mAuth.signOut();
    }

    public void signup(View view){
        if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
            mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        showMessage("Success","Your profile is created!");
                        user = mAuth.getCurrentUser();

//                        //create new SharedPreference for keeping user stats and favs
//                        if (user!=null){
//                            String user_id = user.getUid();
//                            SharedPreferences userPreference = getSharedPreferences(user_id+"_preferences",MODE_PRIVATE);
//                            SharedPreferences.Editor editor = userPreference.edit();
//
//                            //user id is the key for the users preference
//                            editor.putString("user_id",user_id);
//
//                            //initialize favorite stories set
//                            Set<String> favoriteStories = new HashSet<>();
//                            editor.putStringSet("favorite_stories",favoriteStories);
//
//                            //initialize story counters for all 5 stories with a counter of 0
//                            editor.putInt("RedHood",0);
//                            editor.putInt("Cinderella",0);
//                            editor.putInt("UglyDuckling",0);
//                            editor.putInt("MagicFlute",0);
//                            editor.putInt("Midas",0);
//                            editor.apply();
//                        }
                        Intent intent = new Intent(MainActivity3.this, MainActivity.class);
                        startActivity(intent);
                    } else{
                        //what is wrong the task will note us
                        showMessage("Error",task.getException().getLocalizedMessage());
                    }
                }
            });
        } else {
            showMessage("Error","Please provide email and password!");
        }
        //mAuth.signOut();
    }
    private void showMessage(String title,String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}