package com.example.elmhurstcheckin;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;

public class MainActivity2 extends AppCompatActivity {
    TextView name; //e_number

    int realUID;
    Button btn_scan;
    Button passing;

    private final static String DEBUG = "MA2";
    private final String path = "Connection Url";
    private final static String fileDir = "/Android/Receiver";
    private static String base = "?id=2";
    private static String eid  = null;
    private static String uid = null;
    private String url = null;
    private static HttpURLConnection urlConnection = null;
    private Connection connection = null;

    public void btnBack(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        name = (TextView) findViewById(R.id.welcome);

        Intent receiver = getIntent();
        String receiverValue = receiver.getStringExtra("NAME_SENDER");
        name.setText(receiverValue);

        uid = receiver.getStringExtra("KEY_SENDER");

        passing = findViewById(R.id.passing_btn);
        passing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             openActivity();
            }
        });

        btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(view -> {
            scanCode();
        });
    }

    public void openActivity(){
        Intent intent = new Intent(this,DataTest.class);
        //currently has name.
        intent.putExtra("KEY_SENDER",String.valueOf(realUID));
        intent.putExtra("NAME_SENDER",name.getText().toString());
        startActivity(intent);
    }
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up for flashlight");
        options.setBeepEnabled(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {

        if(result.getContents()!= null){
            Log.v(DEBUG, "non empty string");
            eid = result.getContents();
            url = path+fileDir+base+"&uid="+uid+"&eid="+eid;

            Log.v(DEBUG, "Hello"+ url );

            try{
                URL link = new URL(url);
                urlConnection = (HttpURLConnection) link.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            }catch(Exception ex){
                Log.v(DEBUG, Thread.currentThread().getStackTrace()[2].getLineNumber() + ex.toString());
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this );
            builder.setTitle("Results");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });
}