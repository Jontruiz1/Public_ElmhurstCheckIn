package com.example.elmhurstcheckin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataTest extends AppCompatActivity {

    SimpleDateFormat simpleDateFormat;
    SimpleDateFormat time_format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_test);

        //name received
        Intent received = getIntent();
        String e_name = received.getStringExtra("KEY_SENDER");
        TextView name = findViewById(R.id.name_e);
        name.setText(e_name);

        //date received
        Calendar calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = simpleDateFormat.format(calendar.getTime());
        TextView current_Date = findViewById(R.id.current_Date);
        current_Date.setText(currentDate);

        //time received
        time_format = new SimpleDateFormat("HH:mm:ss");
        String currentTime = time_format.format(calendar.getTime());
        TextView current_Time = findViewById(R.id.current_time);
        current_Time.setText(currentTime);


    }
}