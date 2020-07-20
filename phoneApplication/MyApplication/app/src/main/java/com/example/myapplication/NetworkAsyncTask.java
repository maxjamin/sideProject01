package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkAsyncTask extends AsyncTask<String, Void, String> {

    public NetworkAsyncTask() {

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    /*INTERFACE */
    public interface AsyncResponse {
        void processFinish(String output);
    }
    public AsyncResponse delegate = null;

    public NetworkAsyncTask(AsyncResponse delegate){
        this.delegate = delegate;
    }


    /*
    *  Used from: Bhargav Thanki
    *  https://stackoverflow.com/questions/8654876/http-get-using-android-httpurlconnection
    * */
    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    @Override
    protected String doInBackground(String... params) {
        String returnValues = "NULL STATMENT";
        try {

            //------------------>>
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");

            connection.setDoOutput(true);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            writer.write(params[1]);
            writer.close();
            connection.connect();

            // Response: 400
            Log.e("Response1 ", connection.getResponseMessage() + "");
            int responseCode = connection.getResponseCode();
            String server_response = "";
            if(responseCode == HttpURLConnection.HTTP_OK){
                server_response = readStream(connection.getInputStream());
                Log.v("CatalogClient", server_response);
            }

            return server_response;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(e.toString(), "Something with request");
        }
        return returnValues;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.e("Downloaded " , result);
        if(delegate != null)
            delegate.processFinish(result);
        else
            Log.e("You have not", " assigned delegate");
    }
}