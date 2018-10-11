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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.gatech.donatracker.R;
import edu.gatech.donatracker.model.Model;
import edu.gatech.donatracker.model.user.User;

public class RegistrationActivity extends AppCompatActivity {

    private final String TAG = "RegistrationActivity.class";
    private FirebaseAuth mAuth;
    private User user;

    // UI references
    private EditText mEmailView;
    private EditText mPasswordView;
    private Spinner mAccountTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        // Initialize UI references
        mEmailView = findViewById(R.id.editText_registration_email);
        mPasswordView = findViewById(R.id.editText_registration_password);
        mAccountTypeSpinner = findViewById(R.id.spinner_registration_account_type);

        // Set up the adapter for the account type spinner
        ArrayAdapter<User.UserType> spinner_adapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, User.UserType.values());
        mAccountTypeSpinner.setAdapter(spinner_adapter);
    }

    public void onClickRegister(View view) {
        String password = ((EditText) findViewById(R.id.editText_registration_password)).getText().toString();
        String email = ((EditText) findViewById(R.id.editText_registration_email)).getText().toString();
        user = new User((User.UserType) mAccountTypeSpinner.getSelectedItem());

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        // Retrieve user's UID
                        String UID = null;
                        try {
                            UID = firebaseUser.getUid();
                        } catch(NullPointerException e) {
                            Log.e(TAG, "Unable to retrieve UID", e);
                        }

                        // Add new User to Model
                        user.setUID(UID);
                        Model.getModel().addUser(user, UID);

                        // Go to the HomeActivity and clear task
                        // TODO: find a way to pass in the user type/permissions to customize home layout
                        Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    }
                });
    }

    public void onClickCancel(View view) {
        finish();
    }

}
