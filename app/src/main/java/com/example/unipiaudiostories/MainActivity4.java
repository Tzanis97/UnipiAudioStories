package com.example.unipiaudiostories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MainActivity4 extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    SharedPreferences languagePreference;
    TextView textViewTimesRedHood,textViewTimesCinderella,textViewTimesDuckling,textViewTimesFlute,textViewTimesMidas;
    ImageButton imageButtonRedHood,imageButtonCinderella,imageButtonDuckling,imageButtonFlute,imageButtonMidas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if language preference is set, otherwise set default to English
        languagePreference = getSharedPreferences("language",MODE_PRIVATE);
        String savedLang = languagePreference.getString("language", "en");
        setLocale(savedLang);
        setContentView(R.layout.activity_main4);

        //initialize views
        textViewTimesRedHood = findViewById(R.id.textViewTimesRedHood);
        textViewTimesCinderella = findViewById(R.id.textViewTimesCinderella);
        textViewTimesDuckling = findViewById(R.id.textViewTimesDuckling);
        textViewTimesFlute = findViewById(R.id.textViewTimesFlute);
        textViewTimesMidas = findViewById(R.id.textViewTimesMidas);

        //authentication and getting current user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        //Toolbar Config
        Toolbar toolbar = findViewById(R.id.materialToolbar2);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        //initialize views
        imageButtonRedHood = findViewById(R.id.imageButtonRedHood);
        imageButtonCinderella = findViewById(R.id.imageButtonCinderella);
        imageButtonDuckling = findViewById(R.id.imageButtonDuckling);
        imageButtonFlute = findViewById(R.id.imageButtonFlute);
        imageButtonMidas = findViewById(R.id.imageButtonMidas);

        // Set click listeners for ImageButtons
        imageButtonRedHood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.toggleFavorite("RedHood", (isFavorite) -> onFavoriteChanged("RedHood", isFavorite));
            }
        });

        imageButtonCinderella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.toggleFavorite("Cinderella", (isFavorite) -> onFavoriteChanged("Cinderella", isFavorite));
            }
        });

        imageButtonDuckling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.toggleFavorite("UglyDuckling", (isFavorite) -> onFavoriteChanged("UglyDuckling", isFavorite));
            }
        });

        imageButtonFlute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.toggleFavorite("MagicFlute", (isFavorite) -> onFavoriteChanged("MagicFlute", isFavorite));
            }
        });

        imageButtonMidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.toggleFavorite("Midas", (isFavorite) -> onFavoriteChanged("Midas", isFavorite));
            }
        });


        //initialize stats and favs according to the current user's userPreference
        String[] storyKeys = {"RedHood", "Cinderella", "UglyDuckling","MagicFlute","Midas"};
        // Iterate through each story key for the user
        for (String storyKey : storyKeys) {
            // Get the number of times the story has been listened to
            database.getReference().child("users").child(user.getUid()).child("timesListened").child(storyKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String timesListened = snapshot.getValue(Integer.class).toString();

                    // Set the text for the corresponding text view
                    switch (storyKey) {
                        case "RedHood":
                            textViewTimesRedHood.setText(timesListened);
                            break;
                        case "Cinderella":
                            textViewTimesCinderella.setText(timesListened);
                            break;
                        case "UglyDuckling":
                            textViewTimesDuckling.setText(timesListened);
                            break;
                        case "MagicFlute":
                            textViewTimesFlute.setText(timesListened);
                            break;
                        case "Midas":
                            textViewTimesMidas.setText(timesListened);
                            break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });


            // Get the favorite status of the story
            database.getReference().child("users").child(user.getUid()).child("favorites").child(storyKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isFavorite = snapshot.getValue(Boolean.class);

                    // Set the image button accordingly
                    int imageResource = isFavorite ? R.drawable.filledfavorite : R.drawable.favorite;
                    switch (storyKey) {
                        case "RedHood":
                            imageButtonRedHood.setImageResource(imageResource);
                            break;
                        case "Cinderella":
                            imageButtonCinderella.setImageResource(imageResource);
                            break;
                        case "UglyDuckling":
                            imageButtonDuckling.setImageResource(imageResource);
                            break;
                        case "MagicFlute":
                            imageButtonFlute.setImageResource(imageResource);
                            break;
                        case "Midas":
                            imageButtonMidas.setImageResource(imageResource);
                            break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
    public void onFavoriteChanged(String storyKey, boolean isFavorite) {
        // Update the corresponding ImageButton's icon based on the new favorite status
        ImageButton imageButton = getImageButtonForStory(storyKey);
        if (imageButton != null) {
            int imageResource = isFavorite ? R.drawable.filledfavorite : R.drawable.favorite;
            imageButton.setImageResource(imageResource);
        }
    }

    private ImageButton getImageButtonForStory(String storyKey) {
        switch (storyKey) {
            case "RedHood":
                return imageButtonRedHood;
            case "Cinderella":
                return imageButtonCinderella;
            case "UglyDuckling":
                return imageButtonDuckling;
            case "MagicFlute":
                return imageButtonFlute;
            case "Midas":
                return imageButtonMidas;
            default:
                return null;
        }
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
}