package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class loginPage extends AppCompatActivity implements NetworkAsyncTask.AsyncResponse {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login_page);

    }



    @Override
    public void processFinish(String output){
        //Here you will receive the result fired from async class
        //of onPostExecute(result) method.
        Log.e("ProcessFinish: ", output);

        try {
            JSONObject jsonObj = new JSONObject(output.toString());

            Log.e("TEST process: ", (String) jsonObj.get("message"));
            if(jsonObj.get("message").toString().equals("authorization successful"))
                updatetoAppPage(output);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void validateUser(String user, String password) throws ExecutionException, InterruptedException {

        String url = "http://157.245.91.32:3000/authUser";
        String body = "username=" + user +"&password=" + password;

        NetworkAsyncTask asyncTask = new NetworkAsyncTask();
        asyncTask.delegate = this;

        asyncTask.isCancelled();
        asyncTask.execute(url,body);
    }



    // gets user input and compares them to database
    public void controller(View view) throws IOException, ExecutionException, InterruptedException {
        String[] auth;
       //gets users input
        auth = getUsersInput();

        //check to make sure the values are full
        if(auth[0] != "error" && auth[1] != "error")
        {

            validateUser(auth[0], auth[1]);

        }else
        {
            Log.d("Enter values", " for pass/username");
        }

    }

    //check and clean users input
    int checkusersInput(String username, String password)
    {
        if(!username.isEmpty() && !password.isEmpty())
            return 0;
        else
            return 1;
    }

    //updates activity to overallPage
    void updatetoAppPage(String output) throws JSONException {

        //start the camera page
        Intent intent = new Intent(this, cameraActivity.class);
        intent.putExtra(output, 1);
        startActivity(intent);

    }

    String[] getUsersInput()
    {
        EditText editTextUsername = findViewById(R.id.editText);
        String username = editTextUsername.getText().toString();

        EditText editTextPassword = findViewById(R.id.editText5);
        String password = editTextPassword.getText().toString();

        //check users input here
        int i = checkusersInput(username, password);
        String auth[] = {"error", "error"};

        if(i != 1) {

            auth[0] = username; auth[1] = password;
            return auth;
        }
        else
            return auth;
    }

}

