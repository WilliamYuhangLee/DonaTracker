package edu.gatech.donatracker.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import edu.gatech.donatracker.R;
import edu.gatech.donatracker.database.FirebaseManager;
import edu.gatech.donatracker.model.user.User;

public class HomeActivity extends AppCompatActivity {

    private static final String WELCOME = "Welcome!";
    // DEBUG
    private final String TAG = "HomeActivity.class";
    // Exit application when click BACK button
    private boolean isBackButtonClicked = false;
    private TextView greeting_textView;
    // User credentials
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DocumentReference userDocRef;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize UI references
        // UI references
        Button log_out_button = findViewById(R.id.button_home_logout);
        greeting_textView = findViewById(R.id.textView_home_greeting);
        greeting_textView.setText(WELCOME);
        Button view_location_list_button = findViewById(R.id.button_home_view_location);

        // Set up models
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        String UID = Objects.requireNonNull(firebaseUser).getUid();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        userDocRef = database.collection("users").document(UID);

        // Set handlers
        log_out_button.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        view_location_list_button.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ViewLocationsActivity.class);
            intent.putExtra("User Model", (Parcelable) user);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        FirebaseManager.updateObject(this, User::unwrapData, result -> {
            user = result;
            greeting_textView.setText(String.format("Welcome, %s!", user.getUserType()));
        }, userDocRef);
    }

    @Override
    public void onBackPressed() {
        if (isBackButtonClicked) {
            super.onBackPressed();
        } else {
            isBackButtonClicked = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        isBackButtonClicked = false;
        super.onResume();
    }
}
