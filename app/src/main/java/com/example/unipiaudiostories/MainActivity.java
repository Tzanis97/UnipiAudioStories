package com.example.unipiaudiostories;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.Firebase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goCinderella(View view){
        Intent intent = new Intent(this,MainActivity2.class);
        intent.putExtra("Title","Cinderella");
        startActivity(intent);
    }

    public void goRedHood(View view){
        Intent intent = new Intent(this,MainActivity2.class);
        intent.putExtra("Title","RedHood");
        startActivity(intent);
    }

    public void goUgly(View view){
        Intent intent = new Intent(this,MainActivity2.class);
        intent.putExtra("Title","UglyDuckling");
        startActivity(intent);
    }

    public void goFlute(View view){
        Intent intent = new Intent(this,MainActivity2.class);
        intent.putExtra("Title","MagicFlute");
        startActivity(intent);
    }

    public void goMidas(View view){
        Intent intent = new Intent(this,MainActivity2.class);
        intent.putExtra("Title","Midas");
        startActivity(intent);
    }
}