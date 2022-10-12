package com.martinkondor.mooncalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Activity for creating new Events
 */
public class CreateEventActivity extends AppCompatActivity {

    private DatabaseManager db;

    // Elements
    private TextView titleDate;
    private Button saveBtn, cancelBtn;
    private EditText titleInput, descInput;
    private TimePicker startTimeInput, endTimeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        initialize();
    }

    private void initialize() {
        db = new DatabaseManager(this);
        titleDate = (TextView) findViewById(R.id.create_event_title_date);
        setTitleDate();

        // Buttons
        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        saveBtn = (Button) findViewById(R.id.save_btn);

        // Input fields
        titleInput = (EditText) findViewById(R.id.title);
        descInput = (EditText) findViewById(R.id.desc);
        startTimeInput = (TimePicker) findViewById(R.id.start_time);
        endTimeInput = (TimePicker) findViewById(R.id.end_time);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateEventActivity.this, CalendarActivity.class));
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String errorMessage = saveEvent();
                if (errorMessage.length() == 0) {
                    Utils.sendToastMessage(getApplicationContext(), getResources().getString(R.string.event_save_success));
                    startActivity(new Intent(CreateEventActivity.this, CalendarActivity.class));
                }
                else {
                    Utils.sendToastMessage(getApplicationContext(), errorMessage);
                }
            }
        });
    }

    private void setTitleDate() {
        String titleDateText = "";
        int year = SharedPreference.getSharedPreferenceInt(
                CreateEventActivity.this,
                getResources().getString(R.string.SPNAME_CURRENT_YEAR)
        );
        int month = SharedPreference.getSharedPreferenceInt(
                CreateEventActivity.this,
                getResources().getString(R.string.SPNAME_CURRENT_MONTH)
        );
        int day = SharedPreference.getSharedPreferenceInt(
                CreateEventActivity.this,
                getResources().getString(R.string.SPNAME_LAST_OPENED_DAY)
        );
        titleDateText += String.valueOf(year) + ".";
        titleDateText += String.valueOf(month) + ".";
        titleDateText += String.valueOf(day);
        titleDate.setText(titleDateText);
    }

    /**
     * Saves the event from the EditText elements
     * to the database
     */
    private String saveEvent() {

        // Precheck inputs
        String titleInputed = titleInput.getText().toString();
        String descInputed = descInput.getText().toString();

        if (titleInputed.length() == 0) {
            return getString(R.string.please_give_event_name);
        }

        Event e = new Event();
        int currentYear = SharedPreference.getSharedPreferenceInt(
                CreateEventActivity.this,
                getResources().getString(R.string.SPNAME_CURRENT_YEAR)
        );
        int currentMonth = SharedPreference.getSharedPreferenceInt(
                CreateEventActivity.this,
                getResources().getString(R.string.SPNAME_CURRENT_MONTH)
        );
        int lastOpenedDay = SharedPreference.getSharedPreferenceInt(
                CreateEventActivity.this,
                getResources().getString(R.string.SPNAME_LAST_OPENED_DAY)
        );

        e.setId(-1);
        e.setTitle(titleInputed);
        e.setDesc(descInputed);
        e.setYear(currentYear);
        e.setMonth(currentMonth);
        e.setTime(Utils.createTimeStringFromInput(startTimeInput, endTimeInput));
        e.setDay(lastOpenedDay);

        // Insert event to database
        db.insertEvent(e);
        return "";
    }
}