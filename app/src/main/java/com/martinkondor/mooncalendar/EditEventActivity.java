package com.martinkondor.mooncalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditEventActivity extends AppCompatActivity {

    private DatabaseManager db;

    private Event eventToEdit;
    private Button saveBtn, cancelBtn;
    private TextView titleDate;
    private EditText titleInput, descInput;
    private TimePicker startTimeInput, endTimeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        initialize();
    }

    /**
     * Fill the Event object from the extras that
     * the activity was passed over with
     */
    private void getEventData() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            System.out.println("No extras present!");
            Utils.sendToastMessage(getApplicationContext(), getString(R.string.event_open_error));
            startActivity(new Intent(EditEventActivity.this, CalendarActivity.class));
        }

        eventToEdit.setId(extras.getInt("e.id"));
        eventToEdit.setTitle(extras.getString("e.title"));
        eventToEdit.setDesc(extras.getString("e.desc"));
        eventToEdit.setTime(extras.getString("e.time"));
        eventToEdit.setYear(extras.getInt("e.year"));
        eventToEdit.setMonth(extras.getInt("e.month"));
        eventToEdit.setDay(extras.getInt("e.day"));

        // Check inputs
        if (eventToEdit.hasNull()) {
            System.out.println("Event has null values!");
            System.out.println(eventToEdit.toString());
            Utils.sendToastMessage(getApplicationContext(), getString(R.string.event_open_error));
            startActivity(new Intent(EditEventActivity.this, CalendarActivity.class));
        }
    }

    /**
     * Setting the UI elements with the data from
     * the Event object of this class
     */
    private void setUIForEvent() {
        titleInput.setText(eventToEdit.getTitle());
        descInput.setText(eventToEdit.getDesc());

        // Set time
        // example string: 11:00 - 12:00
        String[] timeInPieces = eventToEdit.getTime().split("-");
        String startTimeStr = timeInPieces[0].trim();
        String endTimeStr = timeInPieces[1].trim();
        int[] startTime = Utils.getTime(startTimeStr);
        int[] endTime = Utils.getTime(endTimeStr);

        startTimeInput.setHour(startTime[0]);
        startTimeInput.setMinute(startTime[1]);
        endTimeInput.setHour(endTime[0]);
        endTimeInput.setMinute(endTime[1]);
    }

    /**
     * Sets the local event object to the new
     * edited values from the UI
     */
    private boolean setEventFromUI() {
        String titleStringInput = titleInput.getText().toString();
        String timeStringInput = Utils.createTimeStringFromInput(startTimeInput, endTimeInput);

        // Check input
        if (titleStringInput.length() == 0 || timeStringInput.length() == 0) {
            Utils.sendToastMessage(getApplicationContext(), getString(R.string.please_give_event_name));
            return false;
        }

        eventToEdit.setTitle(titleStringInput);
        eventToEdit.setDesc(descInput.getText().toString());
        eventToEdit.setTime(timeStringInput);
        return true;
    }

    private void setTitleDate() {
        String titleDateText = String.valueOf(eventToEdit.getYear()) + ".";
        titleDateText += String.valueOf(eventToEdit.getMonth()) + ".";
        titleDateText += String.valueOf(eventToEdit.getDay());
        titleDate.setText(titleDateText);
    }

    private void initialize() {
        db = new DatabaseManager(this);
        titleDate = (TextView) findViewById(R.id.edit_event_title_date);
        titleInput = (EditText) findViewById(R.id.title_input);
        descInput = (EditText) findViewById(R.id.desc_input);
        startTimeInput = (TimePicker) findViewById(R.id.start_time);
        endTimeInput = (TimePicker) findViewById(R.id.end_time);

        eventToEdit = new Event();
        getEventData();
        setUIForEvent();
        setTitleDate();

        // Buttons
        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        saveBtn = (Button) findViewById(R.id.save_btn);

        // Button click listeners
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditEventActivity.this, CalendarActivity.class));
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEventFromUI();
                db.updateEvent(eventToEdit);
                Utils.sendToastMessage(getApplicationContext(), getString(R.string.event_edit_success));
                startActivity(new Intent(EditEventActivity.this, CalendarActivity.class));
            }
        });

    }
}