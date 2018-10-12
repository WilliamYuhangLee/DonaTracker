package edu.gatech.donatracker.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import edu.gatech.donatracker.R;
import edu.gatech.donatracker.model.Model;
import edu.gatech.donatracker.model.user.User;

public class HomeActivity extends AppCompatActivity {

    // UI references
    private Button log_out_button;
    private TextView greeting_textView;
    private Button view_location_list_button;

    // User credentials
    private FirebaseAuth mAuth;
    private User user;

    // Exit application when click BACK button
    boolean isBackButtonClicked = false;

    // DEBUG
    private final String TAG = "HomeActivity.class";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        user = Model.getModel().getUser(mAuth.getUid());

        // Initialize references
        log_out_button = findViewById(R.id.button_home_logout);
        greeting_textView = findViewById(R.id.textView_home_greeting);
        greeting_textView.append(", " + user.getUserType() + "!");
        view_location_list_button = findViewById(R.id.button_home_view_location);

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
            startActivity(intent);
        });
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
