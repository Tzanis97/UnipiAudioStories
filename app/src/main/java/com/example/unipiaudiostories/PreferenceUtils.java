package com.example.unipiaudiostories;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PreferenceUtils {
    //event listener to trigger activities' methods, whenever we toggle a favorite preference
    public interface OnFavoriteChangedListener {
        void onFavoriteChanged(boolean isFavorite);
    }
    public static void toggleFavorite(String keyTitle, OnFavoriteChangedListener listener) {
//        // Get the current favorite status
//        boolean isFavorite = userPreference.getBoolean(keyTitle + "_favorite", false);
//
//        // Toggle the favorite status
//        isFavorite = !isFavorite;
//
//        // Update the SharedPreferences with the new favorite status
//        SharedPreferences.Editor editor = userPreference.edit();
//        editor.putBoolean(keyTitle + "_favorite", isFavorite);
//        editor.apply();
//
//        // Trigger event listener
//        if (listener != null) {
//            listener.onFavoriteChanged(isFavorite);
//        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            String userId = user.getUid();
            DatabaseReference userRef = database.getReference().child("users").child(userId).child("favorites").child(keyTitle);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //get the current favorite status
                    boolean isFavorite = snapshot.getValue(Boolean.class);
                    //toggle the favorite status
                    isFavorite = !isFavorite;
                    //update database with the new favorite status
                    userRef.setValue(isFavorite);
                    //Trigger listener
                    if(listener!=null){
                        listener.onFavoriteChanged(isFavorite);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
}
