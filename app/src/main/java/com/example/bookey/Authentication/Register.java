package com.example.bookey.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.bookey.Instances.BaseActivity;
import com.example.bookey.Objects.Client;
import com.example.bookey.R;
import com.google.firebase.firestore.FirebaseFirestore;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etUsername, etPassword, etConfirmPassword;

    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_CONFIRM_PASSWORD = "confirm_password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize the views
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        TextView textViewLogin = findViewById(R.id.textViewLogin);
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opens login
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        // Restore the entered data on screen rotation
        if (savedInstanceState != null) {
            etFirstName.setText(savedInstanceState.getString(KEY_FIRST_NAME));
            etLastName.setText(savedInstanceState.getString(KEY_LAST_NAME));
            etEmail.setText(savedInstanceState.getString(KEY_EMAIL));
            etUsername.setText(savedInstanceState.getString(KEY_USERNAME));
            etPassword.setText(savedInstanceState.getString(KEY_PASSWORD));
            etConfirmPassword.setText(savedInstanceState.getString(KEY_CONFIRM_PASSWORD));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the entered data on screen rotation
        outState.putString(KEY_FIRST_NAME, etFirstName.getText().toString());
        outState.putString(KEY_LAST_NAME, etLastName.getText().toString());
        outState.putString(KEY_EMAIL, etEmail.getText().toString());
        outState.putString(KEY_USERNAME, etUsername.getText().toString());
        outState.putString(KEY_PASSWORD, etPassword.getText().toString());
        outState.putString(KEY_CONFIRM_PASSWORD, etConfirmPassword.getText().toString());
    }

    private void registerUser() {
        // Get the input values
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Perform validation checks
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        // check if email is valid
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }
        // check if username is valid
        if (username.length() < 6) {
            Toast.makeText(this, "Username must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }
        // check if password is valid
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }
        // check if first name and last name are valid and don't contain numbers
        if (firstName.matches(".*\\d.*") || lastName.matches(".*\\d.*")) {
            Toast.makeText(this, "First name and last name must not contain numbers", Toast.LENGTH_SHORT).show();
            return;
        }


        // TODO: Implement your registration logic here
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        writeToDb(db, new Client(firstName, lastName, email, username, password,"User"));

}
    public void onBackPressed() {
        // Show confirmation dialog when back button is pressed
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to leave? All entered data will be lost.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Finish the activity and return to the previous screen
                        Register.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    private void writeToDb(FirebaseFirestore db, Client cl) {
        // Search for a user
        db.collection("Users")
                .whereEqualTo("username", cl.getUsername())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "Chose another username", Toast.LENGTH_SHORT).show();
                    } else {
                        // Add the user
                        db.collection("Users")
                                .add(cl)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Register.this, Login.class);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to add user", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
                });


    }


}
