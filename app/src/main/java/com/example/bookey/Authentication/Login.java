package com.example.bookey.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.bookey.Instances.BaseActivity;
import com.example.bookey.NavigationScreens.AdminNavigation;
import com.example.bookey.NavigationScreens.NavigationActivity;
import com.example.bookey.Objects.Client;
import com.example.bookey.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.content.Intent;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        Button loginButton = findViewById(R.id.buttonLogin);
        TextView registerTextView = findViewById(R.id.textViewRegister);
        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();

                String password = editTextPassword.getText().toString().trim();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                loginSuccessful(db, username, password);


            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle register text view click event
                // Start the register activity here
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });



    }

    private void loginSuccessful(FirebaseFirestore db, String username, String password) {


        // Search for the client in the database based on the username
        db.collection("Users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // User found, check password
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Client client = document.toObject(Client.class);


                            if (client.getPassword().equals(password)) {
                                // Password matches, login successful

                                // save the client in the shared preferences
                                saveClient(client);

                                if (client.getType().equals("admin")){
                                    Toast.makeText(Login.this, "Admin Login", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, AdminNavigation.class);
                                    startActivity(intent);

                                }else{
                                    // Start the main activity here
                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, NavigationActivity.class);
                                    startActivity(intent);
                                }

                            } else {
                                // Incorrect password
                                Toast.makeText(Login.this, "Login Failed check your data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        // User not found
                        Toast.makeText(Login.this, "Login Failed Check your data", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Login.this, "Connection error", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveClient(Client client) {
        // Save the client in the shared preferences

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", client.getUsername());
        editor.putString("email", client.getEmail());
        editor.putString("first", client.getFirstName());
        editor.putString("last", client.getLastName());
        editor.apply();

    }


}

