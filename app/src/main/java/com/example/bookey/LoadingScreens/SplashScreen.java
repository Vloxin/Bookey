package com.example.bookey.LoadingScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.bookey.Authentication.Login;
import com.example.bookey.MainActivity;
import com.example.bookey.NavigationScreens.NavigationActivity;
import com.example.bookey.R;

public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Start the data loading task
        new LoadDataAsyncTask().execute();
    }
    private class LoadDataAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void...voids) {
            // Load the large data here
            // Replace the sleep with your actual data loading logic
            try {

                Thread.sleep(3000);



            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            // Data loading is complete, start the main activity
            Intent intent = new Intent(SplashScreen.this, Login.class);
            startActivity(intent);
            finish();
        }
    }

}

