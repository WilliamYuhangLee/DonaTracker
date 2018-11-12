package edu.gatech.donatracker.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import edu.gatech.donatracker.R;
import edu.gatech.donatracker.model.user.User;

public class RegistrationActivity extends AppCompatActivity {

    private final String TAG = "RegistrationActivity.class";
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private User user;

    private Spinner mAccountTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        // Initialize UI references
        // UI references
        EditText mEmailView = findViewById(R.id.editText_registration_email);
        EditText mPasswordView = findViewById(R.id.editText_registration_password);
        mAccountTypeSpinner = findViewById(R.id.spinner_registration_account_type);

        // Set up the adapter for the account type spinner
        ArrayAdapter<User.UserType> spinner_adapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, User.UserType.legalValues());
        mAccountTypeSpinner.setAdapter(spinner_adapter);
    }

    public void onClickRegister(View view) {
        String password = ((EditText) findViewById(R.id.editText_registration_password)).getText().toString();
        String email = ((EditText) findViewById(R.id.editText_registration_email)).getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        @NonNull FirebaseUser firebaseUser = Objects.requireNonNull(mAuth.getCurrentUser());
                        String UID = firebaseUser.getUid();

                        // Create new User and save it to Model/Firebase
                        user = new User(UID, (User.UserType) mAccountTypeSpinner.getSelectedItem());
//                        Model.getModel().addUser(user, UID);
                        database.collection("users").document(UID).set(user.wrapData())
                                .addOnSuccessListener((v) -> Log.d(TAG, "User has been created and stored!")).addOnFailureListener((v) -> Log.d(TAG, "User creation failed!"));

                        // Go to the HomeActivity and clear task
                        // TODO: find a way to pass in the user type/permissions to customize home layout
                        Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegistrationActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onClickCancel(View view) {
        finish();
    }

}
