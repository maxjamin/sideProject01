package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start the login Activity
        startActivity();
    }

    void startActivity() {
        Intent intent = new Intent(this, loginPage.class );
        startActivity(intent);
    }
}
