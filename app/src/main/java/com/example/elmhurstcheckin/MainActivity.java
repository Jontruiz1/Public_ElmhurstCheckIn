package com.example.elmhurstcheckin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedInputStream;
import java.io.BufferedReader;

public class MainActivity extends AppCompatActivity {
    private TextView NAME;
    // just a debug string to know where a debugging message is being sent from
    private final static String DEBUG = "MA1";

    private int realUID;

    private final String path = "Connection url";

    // path to the actual receiving page, this is probably incredibly unsafe but mvp
    private final static String fileDir = "/Android/Receiver";
    // what tells the webpage to get the query, needs to be changed at some point
    private final static String params = "?id=1";
    // combined url
    private final String url = path+fileDir+params;
    private static HttpURLConnection urlConnection = null;
    EditText text_send;

    public MainActivity(){
        // Just need this once
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private String readStream(InputStream is) throws IOException {

        // reads the inputbuffer or the html page, it should just get the student info from the page
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        String line = null;

        // just gets the line that starts with the Students json array
        try {
            while ((line = r.readLine()) != null) {
                if(line.contains("Students")){
                    sb.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        // try connecting to the webpage to query, url should contain the query
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // getting the name and password from the text boxes
        NAME = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);
        MaterialButton loginBTN = (MaterialButton) findViewById(R.id.loginBtn);

        // when login button hit, this executes
        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String page = "";
                JSONObject mainObject = null;
                try {
                    // tries making the http connection to the website and reads the site data into page
                    URL link = new URL(url);
                    urlConnection = (HttpURLConnection) link.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    page = readStream(in);

                    // getting rid of some unnecessary characters, there's probably a better way to do this
                    page = page.replaceAll("[;#]", "");
                    page = page.replaceAll("(&quot)", "");
                    page = page.replaceAll("(&xD&xA)", "");
                    page = page.replaceAll("</?p>", "");
                    page = page.replaceAll(" ", "");

                    // get the initial JSON object and then get the Students array from that
                    mainObject = new JSONObject(page);
                    JSONArray students = mainObject.getJSONArray("Students");

                    // pull the username and passwords from the object
                    // if they're equal to the entered ones continue to the next page
                    for(int i = 0; i < students.length(); ++i){
                        JSONObject student = students.getJSONObject(i);
                        String eNum = student.getString("eNumber");
                        String pw = student.getString("password");
                        int uid = student.getInt("user_id");
                        realUID = uid;

                        // matches the enumber and password entered to the ones in the json string
                        if(eNum.equals(NAME.getText().toString()) && pw.equals(password.getText().toString())){

                            // disconnect from website and send data over to the next screen
                            // can reconnect to website later
                            urlConnection.disconnect();
                            sendData();
                            return;
                        }
                    }

                    // the information entered in the boxes was not in the DB
                    Toast.makeText(MainActivity.this, "LOGIN FAILED", Toast.LENGTH_SHORT).show();
                }
                // error with reading the data from the website
                catch(IOException ex){
                    Log.v(DEBUG, Thread.currentThread().getStackTrace()[2].getLineNumber() + ex.toString());
                    Toast.makeText(MainActivity.this, "Something went wrong accessing the server", Toast.LENGTH_SHORT).show();
                }
                // catch-all error
                catch(Exception ex){
                    Log.v(DEBUG, Thread.currentThread().getStackTrace()[2].getLineNumber() + ex.toString());
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                // even if no errors thrown, just disconnect
                finally{
                    urlConnection.disconnect();
                }
            }
        });
    }

    public void sendData(){
        text_send = (EditText) findViewById(R.id.username);
        Intent sender = new Intent (this,MainActivity2.class);
        sender.putExtra("NAME_SENDER",text_send.getText().toString());
        //startActivity(sender);
        sender.putExtra("KEY_SENDER", String.valueOf(realUID));
        startActivity(sender);
        Toast.makeText(MainActivity.this, "LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show();
    }
}